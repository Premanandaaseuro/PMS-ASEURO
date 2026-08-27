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
import com.aseuro.pms.manager.dto.EmployeeDropdownDto;
import com.aseuro.pms.manager.dto.MonthlyPmsStatusReportDto;
import com.aseuro.pms.manager.dto.RatingHistoryItemDto;
import com.aseuro.pms.manager.exception.ResourceNotFoundException;
import com.aseuro.pms.manager.service.ManagerReportsService;
import com.aseuro.pms.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/reports")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerReportsController {

    private final ManagerReportsService reportsService;
    private final EmployeeRepository employeeRepository;

    public ManagerReportsController(ManagerReportsService reportsService, EmployeeRepository employeeRepository) {
        this.reportsService = reportsService;
        this.employeeRepository = employeeRepository;
    }

    private Employee getManagerEmployee(User user) {
        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager employee record not found for user: " + user.getEmail()));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDropdownDto>> getReportingEmployees(
            @AuthenticationPrincipal User user) {
        Employee employee = getManagerEmployee(user);
        List<EmployeeDropdownDto> list = reportsService.getReportingEmployees(employee.getId());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/rating-history")
    public ResponseEntity<List<RatingHistoryItemDto>> getEmployeeRatingHistory(
            @AuthenticationPrincipal User user,
            @RequestParam Long employeeId) {
        Employee employee = getManagerEmployee(user);
        List<RatingHistoryItemDto> history = reportsService.getEmployeeRatingHistory(employee.getId(), employeeId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/monthly-status")
    public ResponseEntity<MonthlyPmsStatusReportDto> getMonthlyPmsStatus(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long cycleId) {
        Employee employee = getManagerEmployee(user);
        MonthlyPmsStatusReportDto report = reportsService.getMonthlyPmsStatus(employee.getId(), cycleId);
        return ResponseEntity.ok(report);
    }
}
