package com.aseuro.pms.manager.service;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.DesignationRepository;
import com.aseuro.pms.repository.TeamRepository;
import com.aseuro.pms.repository.UserRepository;

import com.aseuro.pms.manager.dto.*;
import com.aseuro.pms.manager.entity.*;
import com.aseuro.pms.manager.enums.PmsStatus;

import com.aseuro.pms.manager.exception.ForbiddenOperationException;
import com.aseuro.pms.manager.exception.ResourceNotFoundException;
import com.aseuro.pms.manager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ManagerReportsService {

    private final EmployeeRepository employeeRepository;
    private final DesignationRepository designationRepository;
    private final TeamRepository teamRepository;
    private final PmsCycleRepository pmsCycleRepository;
    private final PmsAssignmentRepository pmsAssignmentRepository;
    private final PmsKpiRepository pmsKpiRepository;
    private final EmployeeReviewRepository employeeReviewRepository;
    private final EmployeeKpiRatingRepository employeeKpiRatingRepository;
    private final ManagerReviewRepository managerReviewRepository;
    private final ManagerKpiRatingRepository managerKpiRatingRepository;
    private final FinalPmsResultRepository finalPmsResultRepository;

    public ManagerReportsService(
            EmployeeRepository employeeRepository,
            DesignationRepository designationRepository,
            TeamRepository teamRepository,
            PmsCycleRepository pmsCycleRepository,
            PmsAssignmentRepository pmsAssignmentRepository,
            PmsKpiRepository pmsKpiRepository,
            EmployeeReviewRepository employeeReviewRepository,
            EmployeeKpiRatingRepository employeeKpiRatingRepository,
            ManagerReviewRepository managerReviewRepository,
            ManagerKpiRatingRepository managerKpiRatingRepository,
            FinalPmsResultRepository finalPmsResultRepository) {
        this.employeeRepository = employeeRepository;
        this.designationRepository = designationRepository;
        this.teamRepository = teamRepository;
        this.pmsCycleRepository = pmsCycleRepository;
        this.pmsAssignmentRepository = pmsAssignmentRepository;
        this.pmsKpiRepository = pmsKpiRepository;
        this.employeeReviewRepository = employeeReviewRepository;
        this.employeeKpiRatingRepository = employeeKpiRatingRepository;
        this.managerReviewRepository = managerReviewRepository;
        this.managerKpiRatingRepository = managerKpiRatingRepository;
        this.finalPmsResultRepository = finalPmsResultRepository;
    }

    private String getDesignationName(Long id) {
        return id != null ? designationRepository.findById(id).map(Designation::getName).orElse("") : "";
    }

    private String getTeamName(Long id) {
        return id != null ? teamRepository.findById(id).map(Team::getName).orElse("") : "";
    }

    @Transactional(readOnly = true)
    public List<EmployeeDropdownDto> getReportingEmployees(Long managerEmployeeId) {
        List<Employee> list = employeeRepository.findByManagerId(managerEmployeeId);
        List<EmployeeDropdownDto> result = new ArrayList<>();
        for (Employee e : list) {
            result.add(new EmployeeDropdownDto(
                    e.getId(),
                    e.getEmployeeCode(),
                    e.getFullName(),
                    getDesignationName(e.getDesignationId()),
                    getTeamName(e.getTeamId())
            ));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<RatingHistoryItemDto> getEmployeeRatingHistory(Long managerEmployeeId, Long employeeId) {
        // Enforce IDOR Security
        Employee employee = employeeRepository.findByIdAndManagerId(employeeId, managerEmployeeId)
                .orElseThrow(() -> new ForbiddenOperationException("Access denied. Employee is not assigned to you."));

        List<PmsAssignment> assignments = pmsAssignmentRepository.findByEmployeeIdOrderByCycleDesc(employee.getId());
        List<RatingHistoryItemDto> historyList = new ArrayList<>();

        for (PmsAssignment pa : assignments) {
            RatingHistoryItemDto item = new RatingHistoryItemDto();
            item.setAssignmentId(pa.getId());
            item.setCycleId(pa.getPmsCycle().getId());
            item.setCycleName(pa.getPmsCycle().getName());
            item.setMonth(pa.getPmsCycle().getMonth());
            item.setYear(pa.getPmsCycle().getYear());
            item.setPmsStatus(pa.getStatus().name());

            // Check Final Published Results
            Optional<FinalPmsResult> finalOpt = finalPmsResultRepository.findByPmsAssignmentId(pa.getId());
            if (finalOpt.isPresent()) {
                FinalPmsResult fr = finalOpt.get();
                item.setFinalOverallScore(fr.getOverallScore());
                item.setRatingCategory(fr.getRatingCategory());
                item.setPublishedAt(fr.getPublishedAt());
            }

            // Employee self review
            Optional<EmployeeReview> empReviewOpt = employeeReviewRepository.findByPmsAssignmentId(pa.getId());
            Map<Long, EmployeeKpiRating> selfRatingsMap = new HashMap<>();
            if (empReviewOpt.isPresent()) {
                List<EmployeeKpiRating> selfRatings = employeeKpiRatingRepository.findByEmployeeReviewId(empReviewOpt.get().getId());
                for (EmployeeKpiRating r : selfRatings) {
                    selfRatingsMap.put(r.getPmsKpi().getId(), r);
                }
            }

            // Manager review
            Optional<ManagerReview> mgrReviewOpt = managerReviewRepository.findByPmsAssignmentId(pa.getId());
            Map<Long, ManagerKpiRating> mgrRatingsMap = new HashMap<>();
            if (mgrReviewOpt.isPresent() && "SUBMITTED".equalsIgnoreCase(mgrReviewOpt.get().getStatus())) {
                List<ManagerKpiRating> mgrRatings = managerKpiRatingRepository.findByManagerReviewId(mgrReviewOpt.get().getId());
                for (ManagerKpiRating r : mgrRatings) {
                    mgrRatingsMap.put(r.getPmsKpi().getId(), r);
                }
            }

            // Snapshot KPIs & calculations
            List<PmsKpi> pmsKpis = pmsKpiRepository.findByPmsAssignmentIdOrderByIdAsc(pa.getId());
            List<EmployeeKpiReviewItemDto> kpiBreakdowns = new ArrayList<>();

            BigDecimal totalSelfScore = BigDecimal.ZERO;
            int selfCount = 0;
            BigDecimal totalMgrScore = BigDecimal.ZERO;
            int mgrCount = 0;

            for (PmsKpi pk : pmsKpis) {
                EmployeeKpiReviewItemDto kpiDto = new EmployeeKpiReviewItemDto();
                kpiDto.setPmsKpiId(pk.getId());
                kpiDto.setKpiId(pk.getKpi().getId());
                kpiDto.setKpiName(pk.getKpiName());
                kpiDto.setMeasurementCriteria(pk.getMeasurementCriteria());
                kpiDto.setWeightage(pk.getWeightage());

                EmployeeKpiRating selfR = selfRatingsMap.get(pk.getId());
                if (selfR != null && selfR.getSelfRating() != null) {
                    kpiDto.setSelfRating(selfR.getSelfRating());
                    kpiDto.setEmployeeComments(selfR.getComments());
                    totalSelfScore = totalSelfScore.add(selfR.getSelfRating());
                    selfCount++;
                }

                ManagerKpiRating mgrR = mgrRatingsMap.get(pk.getId());
                if (mgrR != null && mgrR.getManagerRating() != null) {
                    kpiDto.setManagerRating(mgrR.getManagerRating());
                    kpiDto.setManagerComments(mgrR.getComments());
                    totalMgrScore = totalMgrScore.add(mgrR.getManagerRating());
                    mgrCount++;
                }

                kpiBreakdowns.add(kpiDto);
            }

            item.setKpis(kpiBreakdowns);

            if (selfCount > 0) {
                item.setAverageSelfRating(totalSelfScore.divide(BigDecimal.valueOf(selfCount), 2, RoundingMode.HALF_UP));
            }
            if (mgrCount > 0) {
                item.setAverageManagerRating(totalMgrScore.divide(BigDecimal.valueOf(mgrCount), 2, RoundingMode.HALF_UP));
            }

            historyList.add(item);
        }

        return historyList;
    }

    @Transactional(readOnly = true)
    public MonthlyPmsStatusReportDto getMonthlyPmsStatus(Long managerEmployeeId, Long cycleId) {
        PmsCycle cycle;
        if (cycleId != null) {
            cycle = pmsCycleRepository.findById(cycleId)
                    .orElseThrow(() -> new ResourceNotFoundException("PMS cycle not found with id: " + cycleId));
        } else {
            cycle = pmsCycleRepository.findFirstByStatusOrderByYearDescMonthDesc(RecordStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("No active PMS cycle found."));
        }

        List<Employee> assignedEmployees = employeeRepository.findByManagerId(managerEmployeeId);

        MonthlyPmsStatusReportDto report = new MonthlyPmsStatusReportDto();
        report.setCycleId(cycle.getId());
        report.setCycleName(cycle.getName());
        report.setMonth(cycle.getMonth());
        report.setYear(cycle.getYear());
        report.setTotalAssigned(assignedEmployees.size());

        int pendingSelfCount = 0;
        int completedSelfCount = 0;
        int pendingMgrCount = 0;
        int completedMgrCount = 0;
        int finalizedCount = 0;

        List<MonthlyPmsStatusItemDto> itemList = new ArrayList<>();

        for (Employee emp : assignedEmployees) {
            MonthlyPmsStatusItemDto item = new MonthlyPmsStatusItemDto();
            item.setEmployeeId(emp.getId());
            item.setEmployeeCode(emp.getEmployeeCode());
            item.setFullName(emp.getFullName());
            item.setDesignation(getDesignationName(emp.getDesignationId()));
            item.setTeam(getTeamName(emp.getTeamId()));

            Optional<PmsAssignment> paOpt = pmsAssignmentRepository.findByEmployeeIdAndPmsCycleId(emp.getId(), cycle.getId());
            if (paOpt.isPresent()) {
                PmsAssignment pa = paOpt.get();
                item.setAssignmentId(pa.getId());
                item.setPmsStatus(pa.getStatus().name());

                boolean selfSubmitted = pa.getStatus() != PmsStatus.PMS_NOT_STARTED &&
                                        pa.getStatus() != PmsStatus.PMS_STARTED &&
                                        pa.getStatus() != PmsStatus.SELF_ASSESSMENT_DRAFT;

                Optional<ManagerReview> mrOpt = managerReviewRepository.findByPmsAssignmentId(pa.getId());
                boolean mgrSubmitted = mrOpt.isPresent() && "SUBMITTED".equalsIgnoreCase(mrOpt.get().getStatus());

                boolean isFinalized = pa.getStatus() == PmsStatus.FINAL_RESULT_PUBLISHED ||
                                      pa.getStatus() == PmsStatus.COMPLETED;

                if (selfSubmitted) {
                    item.setSelfRatingStatus("COMPLETED");
                    completedSelfCount++;
                } else {
                    item.setSelfRatingStatus("PENDING");
                    pendingSelfCount++;
                }

                if (mgrSubmitted) {
                    item.setManagerReviewStatus("COMPLETED");
                    completedMgrCount++;
                } else if (selfSubmitted) {
                    item.setManagerReviewStatus("PENDING");
                    pendingMgrCount++;
                } else {
                    item.setManagerReviewStatus("AWAITING_SELF_RATING");
                }

                if (isFinalized) {
                    finalizedCount++;
                    Optional<FinalPmsResult> finalOpt = finalPmsResultRepository.findByPmsAssignmentId(pa.getId());
                    if (finalOpt.isPresent()) {
                        item.setFinalScore(finalOpt.get().getOverallScore());
                        item.setRatingCategory(finalOpt.get().getRatingCategory());
                    }
                }
            } else {
                item.setPmsStatus("NOT_ASSIGNED");
                item.setSelfRatingStatus("NOT_ASSIGNED");
                item.setManagerReviewStatus("NOT_ASSIGNED");
            }

            itemList.add(item);
        }

        report.setPendingSelfRatingCount(pendingSelfCount);
        report.setCompletedSelfRatingCount(completedSelfCount);
        report.setPendingManagerReviewCount(pendingMgrCount);
        report.setCompletedManagerReviewCount(completedMgrCount);
        report.setFinalizedCount(finalizedCount);
        report.setEmployees(itemList);

        return report;
    }
}
