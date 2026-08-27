package com.aseuro.pms.controller;

import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.User;
import com.aseuro.pms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final EmployeeRepository employeeRepository;

    @GetMapping("/hr/dashboard")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Map<String, Object>> hrDashboard(@AuthenticationPrincipal User principal) {
        long totalEmployees = employeeRepository.count();
        return ResponseEntity.ok(Map.of(
                "role", "HR",
                "email", principal.getEmail(),
                "title", "HR Setup & Management Workspace",
                "message", "Login verified from the database. Use this workspace to provision managers and employees.",
                "totalEmployees", totalEmployees
        ));
    }

    @GetMapping("/employee/dashboard")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> employeeDashboard(@AuthenticationPrincipal User principal) {
        Employee employee = employeeRepository.findByUserId(principal.getId()).orElse(null);

        return ResponseEntity.ok(Map.of(
                "role", "EMPLOYEE",
                "email", principal.getEmail(),
                "employeeName", employee != null ? employee.getFullName() : "Employee",
                "employeeCode", employee != null ? employee.getEmployeeCode() : "-",
                "title", "Employee Self-Review & KPI Workspace",
                "message", "Login verified from the database. Complete your monthly self-assessment and view ratings.",
                "currentCycle", "August 2026",
                "pmsStatus", "SELF_ASSESSMENT_DRAFT"
        ));
    }
}
