package com.aseuro.pms.manager.service;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;

import com.aseuro.pms.manager.dto.KpiItemDto;
import com.aseuro.pms.manager.dto.KpiRatingInputDto;
import com.aseuro.pms.manager.dto.MyKpisResponseDto;
import com.aseuro.pms.manager.dto.SaveSelfRatingRequestDto;
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
public class MyKpisService {

    private final PmsAssignmentRepository pmsAssignmentRepository;
    private final PmsCycleRepository pmsCycleRepository;
    private final PmsKpiRepository pmsKpiRepository;
    private final EmployeeReviewRepository employeeReviewRepository;
    private final EmployeeKpiRatingRepository employeeKpiRatingRepository;
    private final ManagerReviewRepository managerReviewRepository;
    private final ManagerKpiRatingRepository managerKpiRatingRepository;
    private final FinalPmsResultRepository finalPmsResultRepository;
    private final AuditLogRepository auditLogRepository;
    private final EmployeeRepository employeeRepository;

    public MyKpisService(
            PmsAssignmentRepository pmsAssignmentRepository,
            PmsCycleRepository pmsCycleRepository,
            PmsKpiRepository pmsKpiRepository,
            EmployeeReviewRepository employeeReviewRepository,
            EmployeeKpiRatingRepository employeeKpiRatingRepository,
            ManagerReviewRepository managerReviewRepository,
            ManagerKpiRatingRepository managerKpiRatingRepository,
            FinalPmsResultRepository finalPmsResultRepository,
            AuditLogRepository auditLogRepository,
            EmployeeRepository employeeRepository) {
        this.pmsAssignmentRepository = pmsAssignmentRepository;
        this.pmsCycleRepository = pmsCycleRepository;
        this.pmsKpiRepository = pmsKpiRepository;
        this.employeeReviewRepository = employeeReviewRepository;
        this.employeeKpiRatingRepository = employeeKpiRatingRepository;
        this.managerReviewRepository = managerReviewRepository;
        this.managerKpiRatingRepository = managerKpiRatingRepository;
        this.finalPmsResultRepository = finalPmsResultRepository;
        this.auditLogRepository = auditLogRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public MyKpisResponseDto getMyActiveKpis(Long managerEmployeeId) {
        Optional<PmsCycle> activeCycleOpt = pmsCycleRepository.findFirstByStatusOrderByYearDescMonthDesc(RecordStatus.ACTIVE);
        if (activeCycleOpt.isEmpty()) {
            throw new ResourceNotFoundException("No active PMS cycle found.");
        }

        PmsCycle activeCycle = activeCycleOpt.get();
        Optional<PmsAssignment> assignmentOpt = pmsAssignmentRepository.findByEmployeeIdAndPmsCycleId(managerEmployeeId, activeCycle.getId());
        if (assignmentOpt.isEmpty()) {
            throw new ResourceNotFoundException("No active PMS assignment found for your profile.");
        }

        return mapToMyKpisResponse(assignmentOpt.get());
    }

    @Transactional(readOnly = true)
    public List<MyKpisResponseDto> getMyKpisHistory(Long managerEmployeeId) {
        List<PmsAssignment> assignments = pmsAssignmentRepository.findByEmployeeIdOrderByCycleDesc(managerEmployeeId);
        List<MyKpisResponseDto> result = new ArrayList<>();
        for (PmsAssignment pa : assignments) {
            result.add(mapToMyKpisResponse(pa));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public MyKpisResponseDto getMyKpisByAssignmentId(Long managerEmployeeId, Long assignmentId) {
        PmsAssignment assignment = pmsAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("PMS assignment not found with id: " + assignmentId));

        if (!assignment.getEmployee().getId().equals(managerEmployeeId)) {
            throw new ForbiddenOperationException("Access denied. You can only view your own PMS assignment.");
        }

        return mapToMyKpisResponse(assignment);
    }

    @Transactional
    public MyKpisResponseDto saveDraftSelfRating(Long managerEmployeeId, Long assignmentId, SaveSelfRatingRequestDto request) {
        PmsAssignment assignment = validateAndGetOwnedAssignment(managerEmployeeId, assignmentId);
        assertAssignmentIsEditable(assignment);

        EmployeeReview review = employeeReviewRepository.findByPmsAssignmentId(assignment.getId())
                .orElseGet(() -> {
                    EmployeeReview newReview = new EmployeeReview();
                    newReview.setPmsAssignment(assignment);
                    newReview.setEmployee(assignment.getEmployee());
                    newReview.setStatus("DRAFT");
                    return newReview;
                });

        if ("SUBMITTED".equalsIgnoreCase(review.getStatus())) {
            throw new RatingLockedException("Self-rating has already been submitted and cannot be modified.");
        }

        review.setComments(request.getGeneralComments());
        review.setStatus("DRAFT");
        employeeReviewRepository.save(review);

        if (assignment.getStatus() == PmsStatus.PMS_NOT_STARTED || assignment.getStatus() == PmsStatus.PMS_STARTED) {
            assignment.setStatus(PmsStatus.SELF_ASSESSMENT_DRAFT);
            pmsAssignmentRepository.save(assignment);
        }

        saveRatingsInternal(review, request.getRatings(), false);

        return mapToMyKpisResponse(assignment);
    }

    @Transactional
    public MyKpisResponseDto submitSelfRating(Long managerEmployeeId, Long assignmentId, SaveSelfRatingRequestDto request) {
        PmsAssignment assignment = validateAndGetOwnedAssignment(managerEmployeeId, assignmentId);
        assertAssignmentIsEditable(assignment);

        List<PmsKpi> pmsKpis = pmsKpiRepository.findByPmsAssignmentIdOrderByIdAsc(assignment.getId());
        if (pmsKpis.isEmpty()) {
            throw new ResourceNotFoundException("No KPIs mapped to this PMS assignment.");
        }

        // Validate that all KPIs have ratings between 1.00 and 5.00
        Map<Long, KpiRatingInputDto> inputMap = new HashMap<>();
        if (request.getRatings() != null) {
            for (KpiRatingInputDto item : request.getRatings()) {
                inputMap.put(item.getPmsKpiId(), item);
            }
        }

        for (PmsKpi pk : pmsKpis) {
            KpiRatingInputDto ratingInput = inputMap.get(pk.getId());
            if (ratingInput == null || ratingInput.getRating() == null) {
                throw new InvalidRatingException("Rating is required for KPI: " + pk.getKpiName());
            }
            validateRatingValue(ratingInput.getRating());
        }

        EmployeeReview review = employeeReviewRepository.findByPmsAssignmentId(assignment.getId())
                .orElseGet(() -> {
                    EmployeeReview newReview = new EmployeeReview();
                    newReview.setPmsAssignment(assignment);
                    newReview.setEmployee(assignment.getEmployee());
                    return newReview;
                });

        if ("SUBMITTED".equalsIgnoreCase(review.getStatus())) {
            throw new RatingLockedException("Self-rating has already been submitted and cannot be modified.");
        }

        review.setComments(request.getGeneralComments());
        review.setStatus("SUBMITTED");
        review.setSubmittedAt(OffsetDateTime.now());
        employeeReviewRepository.save(review);

        saveRatingsInternal(review, request.getRatings(), true);

        // Update Assignment Status to SELF_ASSESSMENT_SUBMITTED (or MANAGER_REVIEW_PENDING)
        assignment.setStatus(PmsStatus.SELF_ASSESSMENT_SUBMITTED);
        pmsAssignmentRepository.save(assignment);

        // Record Audit Log
        User user = assignment.getEmployee().getUser();
        auditLogRepository.save(new AuditLog(
                user,
                "SUBMIT_SELF_RATING",
                "PMS_ASSIGNMENT",
                assignment.getId(),
                "{\"status\":\"SELF_ASSESSMENT_DRAFT\"}",
                "{\"status\":\"SELF_ASSESSMENT_SUBMITTED\",\"submittedAt\":\"" + review.getSubmittedAt() + "\"}"
        ));

        return mapToMyKpisResponse(assignment);
    }

    private void saveRatingsInternal(EmployeeReview review, List<KpiRatingInputDto> ratings, boolean strictValidation) {
        if (ratings == null) return;

        for (KpiRatingInputDto input : ratings) {
            if (input.getRating() != null) {
                validateRatingValue(input.getRating());
            } else if (strictValidation) {
                throw new InvalidRatingException("Rating value cannot be empty.");
            }

            PmsKpi pmsKpi = pmsKpiRepository.findById(input.getPmsKpiId())
                    .orElseThrow(() -> new ResourceNotFoundException("PMS KPI not found with id: " + input.getPmsKpiId()));

            EmployeeKpiRating rating = employeeKpiRatingRepository
                    .findByEmployeeReviewIdAndPmsKpiId(review.getId(), pmsKpi.getId())
                    .orElseGet(() -> {
                        EmployeeKpiRating newRating = new EmployeeKpiRating();
                        newRating.setEmployeeReview(review);
                        newRating.setPmsKpi(pmsKpi);
                        return newRating;
                    });

            rating.setSelfRating(input.getRating());
            rating.setComments(input.getComments());
            employeeKpiRatingRepository.save(rating);
        }
    }

    private void validateRatingValue(BigDecimal rating) {
        if (rating == null || rating.compareTo(BigDecimal.valueOf(1.00)) < 0 || rating.compareTo(BigDecimal.valueOf(5.00)) > 0) {
            throw new InvalidRatingException("Rating must be between 1.0 and 5.0. Received: " + rating);
        }
    }

    private PmsAssignment validateAndGetOwnedAssignment(Long managerEmployeeId, Long assignmentId) {
        PmsAssignment assignment = pmsAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("PMS assignment not found with id: " + assignmentId));

        if (!assignment.getEmployee().getId().equals(managerEmployeeId)) {
            throw new ForbiddenOperationException("Access denied. You can only modify your own PMS self-rating.");
        }
        return assignment;
    }

    private void assertAssignmentIsEditable(PmsAssignment assignment) {
        PmsStatus status = assignment.getStatus();
        if (status == PmsStatus.SELF_ASSESSMENT_SUBMITTED ||
            status == PmsStatus.MANAGER_REVIEW_PENDING ||
            status == PmsStatus.MANAGER_REVIEW_SUBMITTED ||
            status == PmsStatus.HR_REVIEW_PENDING ||
            status == PmsStatus.HR_REVIEW_COMPLETED ||
            status == PmsStatus.RATING_AND_POINTS_CALCULATED ||
            status == PmsStatus.FINAL_ANALYSIS ||
            status == PmsStatus.FINAL_RESULT_PUBLISHED ||
            status == PmsStatus.COMPLETED) {
            throw new RatingLockedException("PMS self-assessment is locked and cannot be changed (Current status: " + status + ").");
        }
    }

    private MyKpisResponseDto mapToMyKpisResponse(PmsAssignment assignment) {
        MyKpisResponseDto dto = new MyKpisResponseDto();
        dto.setAssignmentId(assignment.getId());
        dto.setCycleId(assignment.getPmsCycle().getId());
        dto.setCycleName(assignment.getPmsCycle().getName());
        dto.setMonth(assignment.getPmsCycle().getMonth());
        dto.setYear(assignment.getPmsCycle().getYear());
        dto.setDesignation(assignment.getDesignation().getName());
        dto.setReportingManagerName(assignment.getManager().getFullName());
        dto.setStatus(assignment.getStatus().name());

        boolean isSubmitted = assignment.getStatus() != PmsStatus.PMS_NOT_STARTED &&
                              assignment.getStatus() != PmsStatus.PMS_STARTED &&
                              assignment.getStatus() != PmsStatus.SELF_ASSESSMENT_DRAFT;

        boolean isFinalized = assignment.getStatus() == PmsStatus.FINAL_RESULT_PUBLISHED ||
                              assignment.getStatus() == PmsStatus.COMPLETED;

        boolean isEditable = (assignment.getStatus() == PmsStatus.PMS_NOT_STARTED ||
                              assignment.getStatus() == PmsStatus.PMS_STARTED ||
                              assignment.getStatus() == PmsStatus.SELF_ASSESSMENT_DRAFT);

        dto.setSubmitted(isSubmitted);
        dto.setFinalized(isFinalized);
        dto.setEditable(isEditable);

        // Employee self review details
        Optional<EmployeeReview> empReviewOpt = employeeReviewRepository.findByPmsAssignmentId(assignment.getId());
        Map<Long, EmployeeKpiRating> selfRatingsMap = new HashMap<>();

        if (empReviewOpt.isPresent()) {
            EmployeeReview empReview = empReviewOpt.get();
            dto.setReviewStatus(empReview.getStatus());
            dto.setSubmittedAt(empReview.getSubmittedAt());
            dto.setGeneralComments(empReview.getComments());

            List<EmployeeKpiRating> selfRatings = employeeKpiRatingRepository.findByEmployeeReviewId(empReview.getId());
            for (EmployeeKpiRating r : selfRatings) {
                selfRatingsMap.put(r.getPmsKpi().getId(), r);
            }
        } else {
            dto.setReviewStatus("NOT_STARTED");
        }

        // Manager review details (by manager's reviewer)
        Optional<ManagerReview> mgrReviewOpt = managerReviewRepository.findByPmsAssignmentId(assignment.getId());
        Map<Long, ManagerKpiRating> mgrRatingsMap = new HashMap<>();

        if (mgrReviewOpt.isPresent() && "SUBMITTED".equalsIgnoreCase(mgrReviewOpt.get().getStatus())) {
            List<ManagerKpiRating> mgrRatings = managerKpiRatingRepository.findByManagerReviewId(mgrReviewOpt.get().getId());
            for (ManagerKpiRating mr : mgrRatings) {
                mgrRatingsMap.put(mr.getPmsKpi().getId(), mr);
            }
        }

        // Published Final Results
        if (isFinalized) {
            Optional<FinalPmsResult> finalResultOpt = finalPmsResultRepository.findByPmsAssignmentId(assignment.getId());
            if (finalResultOpt.isPresent()) {
                FinalPmsResult fr = finalResultOpt.get();
                dto.setFinalScore(fr.getOverallScore());
                dto.setRatingCategory(fr.getRatingCategory());
                dto.setHrComments(fr.getHrComments());
                dto.setPublishedAt(fr.getPublishedAt());
            }
        }

        // Load Snapshot KPIs
        List<PmsKpi> pmsKpis = pmsKpiRepository.findByPmsAssignmentIdOrderByIdAsc(assignment.getId());
        List<KpiItemDto> kpiDtos = new ArrayList<>();

        for (PmsKpi pk : pmsKpis) {
            KpiItemDto item = new KpiItemDto();
            item.setPmsKpiId(pk.getId());
            item.setKpiId(pk.getKpi().getId());
            item.setKpiName(pk.getKpiName());
            item.setMeasurementCriteria(pk.getMeasurementCriteria());
            item.setWeightage(pk.getWeightage());

            EmployeeKpiRating selfR = selfRatingsMap.get(pk.getId());
            if (selfR != null) {
                item.setSelfRating(selfR.getSelfRating());
                item.setSelfComments(selfR.getComments());
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

