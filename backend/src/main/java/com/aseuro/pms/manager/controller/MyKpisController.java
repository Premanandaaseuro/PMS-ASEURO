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
import com.aseuro.pms.manager.dto.MyKpisResponseDto;
import com.aseuro.pms.manager.dto.SaveSelfRatingRequestDto;
import com.aseuro.pms.manager.exception.ResourceNotFoundException;
import com.aseuro.pms.manager.service.MyKpisService;
import com.aseuro.pms.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/my-kpis")
@PreAuthorize("hasRole('MANAGER')")
public class MyKpisController {

    private final MyKpisService myKpisService;
    private final EmployeeRepository employeeRepository;

    public MyKpisController(MyKpisService myKpisService, EmployeeRepository employeeRepository) {
        this.myKpisService = myKpisService;
        this.employeeRepository = employeeRepository;
    }

    private Employee getManagerEmployee(User user) {
        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager employee record not found for user: " + user.getEmail()));
    }

    @GetMapping
    public ResponseEntity<MyKpisResponseDto> getMyActiveKpis(@AuthenticationPrincipal User user) {
        Employee employee = getManagerEmployee(user);
        MyKpisResponseDto response = myKpisService.getMyActiveKpis(employee.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<MyKpisResponseDto>> getMyKpisHistory(@AuthenticationPrincipal User user) {
        Employee employee = getManagerEmployee(user);
        List<MyKpisResponseDto> response = myKpisService.getMyKpisHistory(employee.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<MyKpisResponseDto> getMyKpisByAssignmentId(
            @AuthenticationPrincipal User user,
            @PathVariable Long assignmentId) {
        Employee employee = getManagerEmployee(user);
        MyKpisResponseDto response = myKpisService.getMyKpisByAssignmentId(employee.getId(), assignmentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{assignmentId}/self-rating")
    public ResponseEntity<MyKpisResponseDto> saveDraftSelfRating(
            @AuthenticationPrincipal User user,
            @PathVariable Long assignmentId,
            @Valid @RequestBody SaveSelfRatingRequestDto request) {
        Employee employee = getManagerEmployee(user);
        MyKpisResponseDto response = myKpisService.saveDraftSelfRating(employee.getId(), assignmentId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<MyKpisResponseDto> submitSelfRating(
            @AuthenticationPrincipal User user,
            @PathVariable Long assignmentId,
            @Valid @RequestBody SaveSelfRatingRequestDto request) {
        Employee employee = getManagerEmployee(user);
        MyKpisResponseDto response = myKpisService.submitSelfRating(employee.getId(), assignmentId, request);
        return ResponseEntity.ok(response);
    }
}
