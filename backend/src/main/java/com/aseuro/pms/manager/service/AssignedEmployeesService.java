package com.aseuro.pms.manager.service;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.DepartmentRepository;
import com.aseuro.pms.repository.DesignationRepository;
import com.aseuro.pms.repository.TeamRepository;
import com.aseuro.pms.repository.UserRepository;

import com.aseuro.pms.manager.dto.*;
import com.aseuro.pms.manager.entity.*;
import com.aseuro.pms.manager.enums.PmsStatus;

import com.aseuro.pms.manager.exception.ForbiddenOperationException;
import com.aseuro.pms.manager.exception.InvalidRatingException;
import com.aseuro.pms.manager.exception.RatingLockedException;
import com.aseuro.pms.manager.exception.ResourceNotFoundException;
import com.aseuro.pms.manager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class AssignedEmployeesService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final TeamRepository teamRepository;
    private final PmsCycleRepository pmsCycleRepository;
    private final PmsAssignmentRepository pmsAssignmentRepository;
    private final PmsKpiRepository pmsKpiRepository;
    private final EmployeeReviewRepository employeeReviewRepository;
    private final EmployeeKpiRatingRepository employeeKpiRatingRepository;
    private final ManagerReviewRepository managerReviewRepository;
    private final ManagerKpiRatingRepository managerKpiRatingRepository;
    private final AuditLogRepository auditLogRepository;

    public AssignedEmployeesService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            DesignationRepository designationRepository,
            TeamRepository teamRepository,
            PmsCycleRepository pmsCycleRepository,
            PmsAssignmentRepository pmsAssignmentRepository,
            PmsKpiRepository pmsKpiRepository,
            EmployeeReviewRepository employeeReviewRepository,
            EmployeeKpiRatingRepository employeeKpiRatingRepository,
            ManagerReviewRepository managerReviewRepository,
            ManagerKpiRatingRepository managerKpiRatingRepository,
            AuditLogRepository auditLogRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
        this.teamRepository = teamRepository;
        this.pmsCycleRepository = pmsCycleRepository;
        this.pmsAssignmentRepository = pmsAssignmentRepository;
        this.pmsKpiRepository = pmsKpiRepository;
        this.employeeReviewRepository = employeeReviewRepository;
        this.employeeKpiRatingRepository = employeeKpiRatingRepository;
        this.managerReviewRepository = managerReviewRepository;
        this.managerKpiRatingRepository = managerKpiRatingRepository;
        this.auditLogRepository = auditLogRepository;
    }

    private String getDepartmentName(Long id) {
        return id != null ? departmentRepository.findById(id).map(Department::getName).orElse("") : "";
    }

    private String getDesignationName(Long id) {
        return id != null ? designationRepository.findById(id).map(Designation::getName).orElse("") : "";
    }

    private String getTeamName(Long id) {
        return id != null ? teamRepository.findById(id).map(Team::getName).orElse("") : "";
    }

    @Transactional(readOnly = true)
    public List<AssignedEmployeeSummaryDto> getAssignedEmployees(Long managerEmployeeId) {
        List<Employee> assignedEmployees = employeeRepository.findByManagerId(managerEmployeeId);

        Optional<PmsCycle> activeCycleOpt = pmsCycleRepository.findFirstByStatusOrderByYearDescMonthDesc(RecordStatus.ACTIVE);
        Long activeCycleId = activeCycleOpt.map(PmsCycle::getId).orElse(null);

        List<AssignedEmployeeSummaryDto> result = new ArrayList<>();

        for (Employee emp : assignedEmployees) {
            AssignedEmployeeSummaryDto dto = new AssignedEmployeeSummaryDto();
            dto.setEmployeeId(emp.getId());
            dto.setEmployeeCode(emp.getEmployeeCode());
            dto.setFullName(emp.getFullName());
            dto.setEmail(emp.getEmail());
            dto.setDepartment(getDepartmentName(emp.getDepartmentId()));
            dto.setDesignation(getDesignationName(emp.getDesignationId()));
            dto.setTeam(getTeamName(emp.getTeamId()));

            if (activeCycleId != null) {
                Optional<PmsAssignment> paOpt = pmsAssignmentRepository.findByEmployeeIdAndPmsCycleId(emp.getId(), activeCycleId);
                if (paOpt.isPresent()) {
                    PmsAssignment pa = paOpt.get();
                    dto.setAssignmentId(pa.getId());
                    dto.setPmsStatus(pa.getStatus().name());

                    boolean selfSubmitted = pa.getStatus() != PmsStatus.PMS_NOT_STARTED &&
                                            pa.getStatus() != PmsStatus.PMS_STARTED &&
                                            pa.getStatus() != PmsStatus.SELF_ASSESSMENT_DRAFT;
                    dto.setSelfAssessmentSubmitted(selfSubmitted);

                    Optional<ManagerReview> mrOpt = managerReviewRepository.findByPmsAssignmentId(pa.getId());
                    boolean mgrSubmitted = mrOpt.isPresent() && "SUBMITTED".equalsIgnoreCase(mrOpt.get().getStatus());
                    dto.setManagerReviewSubmitted(mgrSubmitted);

                    if (mgrSubmitted) {
                        dto.setManagerReviewStatus("SUBMITTED");
                    } else if (selfSubmitted) {
                        dto.setManagerReviewStatus("PENDING_REVIEW");
                    } else {
                        dto.setManagerReviewStatus("AWAITING_SELF_RATING");
                    }
                } else {
                    dto.setPmsStatus("NOT_ASSIGNED");
                    dto.setManagerReviewStatus("NOT_ASSIGNED");
                }
            }

            result.add(dto);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public EmployeePmsReviewDetailsDto getEmployeePmsDetails(Long managerEmployeeId, Long employeeId) {
        // Enforce IDOR Cross-Manager Security
        Employee employee = employeeRepository.findByIdAndManagerId(employeeId, managerEmployeeId)
                .orElseThrow(() -> new ForbiddenOperationException("Access denied. Employee is not assigned to you."));

        Optional<PmsCycle> activeCycleOpt = pmsCycleRepository.findFirstByStatusOrderByYearDescMonthDesc(RecordStatus.ACTIVE);
        if (activeCycleOpt.isEmpty()) {
            throw new ResourceNotFoundException("No active PMS cycle found.");
        }

        PmsAssignment assignment = pmsAssignmentRepository.findByEmployeeIdAndPmsCycleId(employee.getId(), activeCycleOpt.get().getId())
                .orElseThrow(() -> new ResourceNotFoundException("PMS assignment not found for this employee in the active cycle."));

        return buildEmployeePmsReviewDetails(employee, assignment);
    }

    @Transactional(readOnly = true)
    public EmployeePmsReviewDetailsDto getEmployeePmsDetailsByAssignmentId(Long managerEmployeeId, Long employeeId, Long assignmentId) {
        Employee employee = employeeRepository.findByIdAndManagerId(employeeId, managerEmployeeId)
                .orElseThrow(() -> new ForbiddenOperationException("Access denied. Employee is not assigned to you."));

        PmsAssignment assignment = pmsAssignmentRepository.findByIdAndManagerId(assignmentId, managerEmployeeId)
                .orElseThrow(() -> new ForbiddenOperationException("Access denied. PMS Assignment is not assigned to you."));

        if (!assignment.getEmployee().getId().equals(employee.getId())) {
            throw new ForbiddenOperationException("Access denied. Assignment does not match employee.");
        }

        return buildEmployeePmsReviewDetails(employee, assignment);
    }

    @Transactional
    public EmployeePmsReviewDetailsDto saveDraftManagerRating(
            Long managerEmployeeId,
            Long employeeId,
            Long assignmentId,
            SaveManagerRatingRequestDto request) {

        Employee employee = employeeRepository.findByIdAndManagerId(employeeId, managerEmployeeId)
                .orElseThrow(() -> new ForbiddenOperationException("Access denied. Employee is not assigned to you."));

        PmsAssignment assignment = pmsAssignmentRepository.findByIdAndManagerId(assignmentId, managerEmployeeId)
                .orElseThrow(() -> new ForbiddenOperationException("Access denied. PMS Assignment is not assigned to you."));

        if (!assignment.getEmployee().getId().equals(employee.getId())) {
            throw new ForbiddenOperationException("Access denied. Assignment does not match employee.");
        }

        assertManagerReviewIsEditable(assignment);

        ManagerReview review = managerReviewRepository.findByPmsAssignmentId(assignment.getId())
                .orElseGet(() -> {
                    ManagerReview newReview = new ManagerReview();
                    newReview.setPmsAssignment(assignment);
                    newReview.setManager(assignment.getManager());
                    newReview.setStatus("DRAFT");
                    return newReview;
                });

        if ("SUBMITTED".equalsIgnoreCase(review.getStatus())) {
            throw new RatingLockedException("Manager review has already been submitted and cannot be modified.");
        }

        review.setComments(request.getGeneralComments());
        review.setStatus("DRAFT");
        managerReviewRepository.save(review);

        saveManagerRatingsInternal(review, request.getRatings(), false);

        return buildEmployeePmsReviewDetails(employee, assignment);
    }

    @Transactional
    public EmployeePmsReviewDetailsDto submitManagerRating(
            Long managerEmployeeId,
            Long employeeId,
            Long assignmentId,
            SaveManagerRatingRequestDto request) {

        Employee employee = employeeRepository.findByIdAndManagerId(employeeId, managerEmployeeId)
                .orElseThrow(() -> new ForbiddenOperationException("Access denied. Employee is not assigned to you."));

        PmsAssignment assignment = pmsAssignmentRepository.findByIdAndManagerId(assignmentId, managerEmployeeId)
                .orElseThrow(() -> new ForbiddenOperationException("Access denied. PMS Assignment is not assigned to you."));

        if (!assignment.getEmployee().getId().equals(employee.getId())) {
            throw new ForbiddenOperationException("Access denied. Assignment does not match employee.");
        }

        assertManagerReviewIsEditable(assignment);

        List<PmsKpi> pmsKpis = pmsKpiRepository.findByPmsAssignmentIdOrderByIdAsc(assignment.getId());
        if (pmsKpis.isEmpty()) {
            throw new ResourceNotFoundException("No KPIs mapped to this PMS assignment.");
        }

        Map<Long, KpiRatingInputDto> inputMap = new HashMap<>();
        if (request.getRatings() != null) {
            for (KpiRatingInputDto item : request.getRatings()) {
                inputMap.put(item.getPmsKpiId(), item);
            }
        }

        for (PmsKpi pk : pmsKpis) {
            KpiRatingInputDto ratingInput = inputMap.get(pk.getId());
            if (ratingInput == null || ratingInput.getRating() == null) {
                throw new InvalidRatingException("Manager rating is required for KPI: " + pk.getKpiName());
            }
            validateRatingValue(ratingInput.getRating());
        }

        ManagerReview review = managerReviewRepository.findByPmsAssignmentId(assignment.getId())
                .orElseGet(() -> {
                    ManagerReview newReview = new ManagerReview();
                    newReview.setPmsAssignment(assignment);
                    newReview.setManager(assignment.getManager());
                    return newReview;
                });

        if ("SUBMITTED".equalsIgnoreCase(review.getStatus())) {
            throw new RatingLockedException("Manager review has already been submitted and cannot be modified.");
        }

        review.setComments(request.getGeneralComments());
        review.setStatus("SUBMITTED");
        review.setSubmittedAt(OffsetDateTime.now());
        managerReviewRepository.save(review);

        saveManagerRatingsInternal(review, request.getRatings(), true);

        // Update Assignment Status to MANAGER_REVIEW_SUBMITTED (or HR_REVIEW_PENDING)
        assignment.setStatus(PmsStatus.MANAGER_REVIEW_SUBMITTED);
        pmsAssignmentRepository.save(assignment);

        // Record Audit Log
        if (assignment.getManager() != null && assignment.getManager().getUser() != null) {
            User managerUser = assignment.getManager().getUser();
            auditLogRepository.save(new AuditLog(
                    managerUser,
                    "SUBMIT_MANAGER_RATING",
                    "PMS_ASSIGNMENT",
                    assignment.getId(),
                    "{\"status\":\"" + assignment.getStatus() + "\"}",
                    "{\"status\":\"MANAGER_REVIEW_SUBMITTED\",\"employeeId\":" + employee.getId() + ",\"submittedAt\":\"" + review.getSubmittedAt() + "\"}"
            ));
        }

        return buildEmployeePmsReviewDetails(employee, assignment);
    }

    private void saveManagerRatingsInternal(ManagerReview review, List<KpiRatingInputDto> ratings, boolean strictValidation) {
        if (ratings == null) return;

        for (KpiRatingInputDto input : ratings) {
            if (input.getRating() != null) {
                validateRatingValue(input.getRating());
            } else if (strictValidation) {
                throw new InvalidRatingException("Manager rating value cannot be empty.");
            }

            PmsKpi pmsKpi = pmsKpiRepository.findById(input.getPmsKpiId())
                    .orElseThrow(() -> new ResourceNotFoundException("PMS KPI not found with id: " + input.getPmsKpiId()));

            ManagerKpiRating rating = managerKpiRatingRepository
                    .findByManagerReviewIdAndPmsKpiId(review.getId(), pmsKpi.getId())
                    .orElseGet(() -> {
                        ManagerKpiRating newRating = new ManagerKpiRating();
                        newRating.setManagerReview(review);
                        newRating.setPmsKpi(pmsKpi);
                        return newRating;
                    });

            rating.setManagerRating(input.getRating());
            rating.setComments(input.getComments());
            managerKpiRatingRepository.save(rating);
        }
    }

    private void validateRatingValue(BigDecimal rating) {
        if (rating == null || rating.compareTo(BigDecimal.valueOf(1.00)) < 0 || rating.compareTo(BigDecimal.valueOf(5.00)) > 0) {
            throw new InvalidRatingException("Manager rating must be between 1.0 and 5.0. Received: " + rating);
        }
    }

    private void assertManagerReviewIsEditable(PmsAssignment assignment) {
        PmsStatus status = assignment.getStatus();
        if (status == PmsStatus.MANAGER_REVIEW_SUBMITTED ||
            status == PmsStatus.HR_REVIEW_PENDING ||
            status == PmsStatus.HR_REVIEW_COMPLETED ||
            status == PmsStatus.RATING_AND_POINTS_CALCULATED ||
            status == PmsStatus.FINAL_ANALYSIS ||
            status == PmsStatus.FINAL_RESULT_PUBLISHED ||
            status == PmsStatus.COMPLETED) {
            throw new RatingLockedException("Manager review is locked and cannot be changed (Current status: " + status + ").");
        }
    }

    private EmployeePmsReviewDetailsDto buildEmployeePmsReviewDetails(Employee employee, PmsAssignment assignment) {
        EmployeePmsReviewDetailsDto dto = new EmployeePmsReviewDetailsDto();
        dto.setAssignmentId(assignment.getId());
        dto.setCycleId(assignment.getPmsCycle().getId());
        dto.setCycleName(assignment.getPmsCycle().getName());
        dto.setMonth(assignment.getPmsCycle().getMonth());
        dto.setYear(assignment.getPmsCycle().getYear());
        dto.setStatus(assignment.getStatus().name());

        AssignedEmployeeSummaryDto empSummary = new AssignedEmployeeSummaryDto();
        empSummary.setEmployeeId(employee.getId());
        empSummary.setEmployeeCode(employee.getEmployeeCode());
        empSummary.setFullName(employee.getFullName());
        empSummary.setEmail(employee.getEmail());
        empSummary.setDepartment(getDepartmentName(employee.getDepartmentId()));
        empSummary.setDesignation(getDesignationName(employee.getDesignationId()));
        empSummary.setTeam(getTeamName(employee.getTeamId()));
        empSummary.setAssignmentId(assignment.getId());
        empSummary.setPmsStatus(assignment.getStatus().name());
        dto.setEmployee(empSummary);

        // Employee self review details (READ-ONLY)
        Optional<EmployeeReview> empReviewOpt = employeeReviewRepository.findByPmsAssignmentId(assignment.getId());
        Map<Long, EmployeeKpiRating> selfRatingsMap = new HashMap<>();

        if (empReviewOpt.isPresent()) {
            EmployeeReview empReview = empReviewOpt.get();
            dto.setEmployeeGeneralComments(empReview.getComments());
            List<EmployeeKpiRating> selfRatings = employeeKpiRatingRepository.findByEmployeeReviewId(empReview.getId());
            for (EmployeeKpiRating r : selfRatings) {
                selfRatingsMap.put(r.getPmsKpi().getId(), r);
            }
        }

        // Manager review details
        Optional<ManagerReview> mgrReviewOpt = managerReviewRepository.findByPmsAssignmentId(assignment.getId());
        Map<Long, ManagerKpiRating> mgrRatingsMap = new HashMap<>();

        boolean isManagerSubmitted = false;
        if (mgrReviewOpt.isPresent()) {
            ManagerReview mgrReview = mgrReviewOpt.get();
            isManagerSubmitted = "SUBMITTED".equalsIgnoreCase(mgrReview.getStatus());
            dto.setManagerSubmittedAt(mgrReview.getSubmittedAt());
            dto.setManagerGeneralComments(mgrReview.getComments());

            List<ManagerKpiRating> mgrRatings = managerKpiRatingRepository.findByManagerReviewId(mgrReview.getId());
            for (ManagerKpiRating r : mgrRatings) {
                mgrRatingsMap.put(r.getPmsKpi().getId(), r);
            }
        }

        dto.setManagerSubmitted(isManagerSubmitted);

        boolean isEditableByManager = !isManagerSubmitted &&
                (assignment.getStatus() == PmsStatus.SELF_ASSESSMENT_SUBMITTED ||
                 assignment.getStatus() == PmsStatus.MANAGER_REVIEW_PENDING ||
                 assignment.getStatus() == PmsStatus.SELF_ASSESSMENT_DRAFT ||
                 assignment.getStatus() == PmsStatus.PMS_STARTED);

        dto.setEditableByManager(isEditableByManager);

        // Load Snapshot KPIs
        List<PmsKpi> pmsKpis = pmsKpiRepository.findByPmsAssignmentIdOrderByIdAsc(assignment.getId());
        List<EmployeeKpiReviewItemDto> kpiDtos = new ArrayList<>();

        for (PmsKpi pk : pmsKpis) {
            EmployeeKpiReviewItemDto item = new EmployeeKpiReviewItemDto();
            item.setPmsKpiId(pk.getId());
            item.setKpiId(pk.getKpi().getId());
            item.setKpiName(pk.getKpiName());
            item.setMeasurementCriteria(pk.getMeasurementCriteria());
            item.setWeightage(pk.getWeightage());

            EmployeeKpiRating selfR = selfRatingsMap.get(pk.getId());
            if (selfR != null) {
                item.setSelfRating(selfR.getSelfRating());
                item.setEmployeeComments(selfR.getComments());
            }

            ManagerKpiRating mgrR = mgrRatingsMap.get(pk.getId());
            if (mgrR != null) {
                item.setManagerRating(mgrR.getManagerRating());
                item.setManagerComments(mgrR.getComments());
            }

            kpiDtos.add(item);
        }

        dto.setKpis(kpiDtos);
        return dto;
    }
}
