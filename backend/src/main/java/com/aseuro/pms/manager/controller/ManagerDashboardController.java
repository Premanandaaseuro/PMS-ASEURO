package com.aseuro.pms.manager.controller;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;

import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.User;
import com.aseuro.pms.manager.dto.DashboardMetricsDto;
import com.aseuro.pms.manager.exception.ResourceNotFoundException;
import com.aseuro.pms.manager.service.ManagerDashboardService;
import com.aseuro.pms.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager/dashboard")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerDashboardController {

    private final ManagerDashboardService dashboardService;
    private final EmployeeRepository employeeRepository;

    public ManagerDashboardController(ManagerDashboardService dashboardService, EmployeeRepository employeeRepository) {
        this.dashboardService = dashboardService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<DashboardMetricsDto> getDashboardMetrics(@AuthenticationPrincipal User user) {
        Employee employee = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager employee record not found for user: " + user.getEmail()));
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(employee.getId());
        return ResponseEntity.ok(metrics);
    }
}
