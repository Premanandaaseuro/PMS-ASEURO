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
import com.aseuro.pms.manager.dto.AssignedEmployeeSummaryDto;
import com.aseuro.pms.manager.dto.EmployeePmsReviewDetailsDto;
import com.aseuro.pms.manager.dto.SaveManagerRatingRequestDto;
import com.aseuro.pms.manager.exception.ResourceNotFoundException;
import com.aseuro.pms.manager.service.AssignedEmployeesService;
import com.aseuro.pms.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/employees")
@PreAuthorize("hasRole('MANAGER')")
public class AssignedEmployeesController {

    private final AssignedEmployeesService assignedEmployeesService;
    private final EmployeeRepository employeeRepository;

    public AssignedEmployeesController(AssignedEmployeesService assignedEmployeesService, EmployeeRepository employeeRepository) {
        this.assignedEmployeesService = assignedEmployeesService;
        this.employeeRepository = employeeRepository;
    }

    private Employee getManagerEmployee(User user) {
        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager employee record not found for user: " + user.getEmail()));
    }

    @GetMapping
    public ResponseEntity<List<AssignedEmployeeSummaryDto>> getAssignedEmployees(
            @AuthenticationPrincipal User user) {
        Employee employee = getManagerEmployee(user);
        List<AssignedEmployeeSummaryDto> list = assignedEmployeesService.getAssignedEmployees(employee.getId());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{employeeId}/pms")
    public ResponseEntity<EmployeePmsReviewDetailsDto> getEmployeePmsDetails(
            @AuthenticationPrincipal User user,
            @PathVariable Long employeeId) {
        Employee employee = getManagerEmployee(user);
        EmployeePmsReviewDetailsDto details = assignedEmployeesService.getEmployeePmsDetails(employee.getId(), employeeId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{employeeId}/pms/{assignmentId}")
    public ResponseEntity<EmployeePmsReviewDetailsDto> getEmployeePmsDetailsByAssignmentId(
            @AuthenticationPrincipal User user,
            @PathVariable Long employeeId,
            @PathVariable Long assignmentId) {
        Employee employee = getManagerEmployee(user);
        EmployeePmsReviewDetailsDto details = assignedEmployeesService.getEmployeePmsDetailsByAssignmentId(employee.getId(), employeeId, assignmentId);
        return ResponseEntity.ok(details);
    }

    @PutMapping("/{employeeId}/pms/{assignmentId}/ratings")
    public ResponseEntity<EmployeePmsReviewDetailsDto> saveDraftManagerRating(
            @AuthenticationPrincipal User user,
            @PathVariable Long employeeId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody SaveManagerRatingRequestDto request) {
        Employee employee = getManagerEmployee(user);
        EmployeePmsReviewDetailsDto details = assignedEmployeesService.saveDraftManagerRating(
                employee.getId(), employeeId, assignmentId, request);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/{employeeId}/pms/{assignmentId}/submit")
    public ResponseEntity<EmployeePmsReviewDetailsDto> submitManagerRating(
            @AuthenticationPrincipal User user,
            @PathVariable Long employeeId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody SaveManagerRatingRequestDto request) {
        Employee employee = getManagerEmployee(user);
        EmployeePmsReviewDetailsDto details = assignedEmployeesService.submitManagerRating(
                employee.getId(), employeeId, assignmentId, request);
        return ResponseEntity.ok(details);
    }
}
