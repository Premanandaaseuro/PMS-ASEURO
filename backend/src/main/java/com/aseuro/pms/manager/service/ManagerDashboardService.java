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

import com.aseuro.pms.manager.dto.DashboardMetricsDto;
import com.aseuro.pms.manager.entity.*;
import com.aseuro.pms.manager.enums.PmsStatus;

import com.aseuro.pms.manager.exception.ResourceNotFoundException;
import com.aseuro.pms.manager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerDashboardService {

    private final PmsCycleRepository pmsCycleRepository;
    private final PmsAssignmentRepository pmsAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ManagerReviewRepository managerReviewRepository;

    public ManagerDashboardService(
            PmsCycleRepository pmsCycleRepository,
            PmsAssignmentRepository pmsAssignmentRepository,
            EmployeeRepository employeeRepository,
            ManagerReviewRepository managerReviewRepository) {
        this.pmsCycleRepository = pmsCycleRepository;
        this.pmsAssignmentRepository = pmsAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.managerReviewRepository = managerReviewRepository;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsDto getDashboardMetrics(Long managerEmployeeId) {
        Employee manager = employeeRepository.findById(managerEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager profile not found"));

        DashboardMetricsDto metrics = new DashboardMetricsDto();
        metrics.setManagerName(manager.getFullName());

        Optional<PmsCycle> activeCycleOpt = pmsCycleRepository.findFirstByStatusOrderByYearDescMonthDesc(RecordStatus.ACTIVE);
        if (activeCycleOpt.isEmpty()) {
            return metrics;
        }

        PmsCycle activeCycle = activeCycleOpt.get();
        metrics.setActiveCycleId(activeCycle.getId());
        metrics.setActiveCycleName(activeCycle.getName());
        metrics.setActiveCycleMonth(activeCycle.getMonth());
        metrics.setActiveCycleYear(activeCycle.getYear());
        metrics.setActiveCycleStartDate(activeCycle.getStartDate());
        metrics.setActiveCycleEndDate(activeCycle.getEndDate());

        // Assigned employees metrics
        List<Employee> assignedEmployees = employeeRepository.findByManagerId(managerEmployeeId);
        metrics.setAssignedEmployeesCount(assignedEmployees.size());

        List<PmsAssignment> assignedPms = pmsAssignmentRepository.findByManagerIdAndPmsCycleId(managerEmployeeId, activeCycle.getId());

        int pendingCount = 0;
        int completedCount = 0;

        for (PmsAssignment pa : assignedPms) {
            Optional<ManagerReview> mrOpt = managerReviewRepository.findByPmsAssignmentId(pa.getId());
            if (mrOpt.isPresent() && "SUBMITTED".equalsIgnoreCase(mrOpt.get().getStatus())) {
                completedCount++;
            } else if (pa.getStatus() == PmsStatus.MANAGER_REVIEW_SUBMITTED ||
                       pa.getStatus() == PmsStatus.HR_REVIEW_PENDING ||
                       pa.getStatus() == PmsStatus.HR_REVIEW_COMPLETED ||
                       pa.getStatus() == PmsStatus.FINAL_RESULT_PUBLISHED ||
                       pa.getStatus() == PmsStatus.COMPLETED) {
                completedCount++;
            } else {
                pendingCount++;
            }
        }

        metrics.setPendingReviewsCount(pendingCount);
        metrics.setCompletedReviewsCount(completedCount);

        // Manager's own self-PMS status
        Optional<PmsAssignment> selfPmsOpt = pmsAssignmentRepository.findByEmployeeIdAndPmsCycleId(managerEmployeeId, activeCycle.getId());
        if (selfPmsOpt.isPresent()) {
            PmsAssignment selfPms = selfPmsOpt.get();
            metrics.setSelfPmsAssignmentId(selfPms.getId());
            metrics.setSelfPmsStatus(selfPms.getStatus().name());

            boolean isSubmitted = selfPms.getStatus() != PmsStatus.PMS_NOT_STARTED &&
                                  selfPms.getStatus() != PmsStatus.PMS_STARTED &&
                                  selfPms.getStatus() != PmsStatus.SELF_ASSESSMENT_DRAFT;
            boolean isFinalized = selfPms.getStatus() == PmsStatus.FINAL_RESULT_PUBLISHED ||
                                  selfPms.getStatus() == PmsStatus.COMPLETED;

            metrics.setSelfPmsSubmitted(isSubmitted);
            metrics.setSelfPmsFinalized(isFinalized);
        }

        return metrics;
    }
}

