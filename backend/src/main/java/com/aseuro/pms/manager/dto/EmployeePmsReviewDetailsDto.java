package com.aseuro.pms.manager.dto;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmployeePmsReviewDetailsDto {

    private AssignedEmployeeSummaryDto employee;
    private Long assignmentId;
    private Long cycleId;
    private String cycleName;
    private Integer month;
    private Integer year;
    private String status;
    private boolean isEditableByManager;
    private boolean isManagerSubmitted;
    private OffsetDateTime managerSubmittedAt;
    private String employeeGeneralComments;
    private String managerGeneralComments;
    private List<EmployeeKpiReviewItemDto> kpis = new ArrayList<>();

    public EmployeePmsReviewDetailsDto() {}

    public AssignedEmployeeSummaryDto getEmployee() { return employee; }
    public void setEmployee(AssignedEmployeeSummaryDto employee) { this.employee = employee; }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getCycleId() { return cycleId; }
    public void setCycleId(Long cycleId) { this.cycleId = cycleId; }

    public String getCycleName() { return cycleName; }
    public void setCycleName(String cycleName) { this.cycleName = cycleName; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isEditableByManager() { return isEditableByManager; }
    public void setEditableByManager(boolean editableByManager) { isEditableByManager = editableByManager; }

    public boolean isManagerSubmitted() { return isManagerSubmitted; }
    public void setManagerSubmitted(boolean managerSubmitted) { isManagerSubmitted = managerSubmitted; }

    public OffsetDateTime getManagerSubmittedAt() { return managerSubmittedAt; }
    public void setManagerSubmittedAt(OffsetDateTime managerSubmittedAt) { this.managerSubmittedAt = managerSubmittedAt; }

    public String getEmployeeGeneralComments() { return employeeGeneralComments; }
    public void setEmployeeGeneralComments(String employeeGeneralComments) { this.employeeGeneralComments = employeeGeneralComments; }

    public String getManagerGeneralComments() { return managerGeneralComments; }
    public void setManagerGeneralComments(String managerGeneralComments) { this.managerGeneralComments = managerGeneralComments; }

    public List<EmployeeKpiReviewItemDto> getKpis() { return kpis; }
    public void setKpis(List<EmployeeKpiReviewItemDto> kpis) { this.kpis = kpis; }
}

