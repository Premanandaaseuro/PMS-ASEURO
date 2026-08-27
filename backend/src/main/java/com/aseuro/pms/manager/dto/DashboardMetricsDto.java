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

import java.time.LocalDate;

public class DashboardMetricsDto {

    private String managerName;
    private Long activeCycleId;
    private String activeCycleName;
    private Integer activeCycleMonth;
    private Integer activeCycleYear;
    private LocalDate activeCycleStartDate;
    private LocalDate activeCycleEndDate;

    private int assignedEmployeesCount;
    private int pendingReviewsCount;
    private int completedReviewsCount;

    private Long selfPmsAssignmentId;
    private String selfPmsStatus;
    private boolean selfPmsSubmitted;
    private boolean selfPmsFinalized;

    public DashboardMetricsDto() {}

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public Long getActiveCycleId() { return activeCycleId; }
    public void setActiveCycleId(Long activeCycleId) { this.activeCycleId = activeCycleId; }

    public String getActiveCycleName() { return activeCycleName; }
    public void setActiveCycleName(String activeCycleName) { this.activeCycleName = activeCycleName; }

    public Integer getActiveCycleMonth() { return activeCycleMonth; }
    public void setActiveCycleMonth(Integer activeCycleMonth) { this.activeCycleMonth = activeCycleMonth; }

    public Integer getActiveCycleYear() { return activeCycleYear; }
    public void setActiveCycleYear(Integer activeCycleYear) { this.activeCycleYear = activeCycleYear; }

    public LocalDate getActiveCycleStartDate() { return activeCycleStartDate; }
    public void setActiveCycleStartDate(LocalDate activeCycleStartDate) { this.activeCycleStartDate = activeCycleStartDate; }

    public LocalDate getActiveCycleEndDate() { return activeCycleEndDate; }
    public void setActiveCycleEndDate(LocalDate activeCycleEndDate) { this.activeCycleEndDate = activeCycleEndDate; }

    public int getAssignedEmployeesCount() { return assignedEmployeesCount; }
    public void setAssignedEmployeesCount(int assignedEmployeesCount) { this.assignedEmployeesCount = assignedEmployeesCount; }

    public int getPendingReviewsCount() { return pendingReviewsCount; }
    public void setPendingReviewsCount(int pendingReviewsCount) { this.pendingReviewsCount = pendingReviewsCount; }

    public int getCompletedReviewsCount() { return completedReviewsCount; }
    public void setCompletedReviewsCount(int completedReviewsCount) { this.completedReviewsCount = completedReviewsCount; }

    public Long getSelfPmsAssignmentId() { return selfPmsAssignmentId; }
    public void setSelfPmsAssignmentId(Long selfPmsAssignmentId) { this.selfPmsAssignmentId = selfPmsAssignmentId; }

    public String getSelfPmsStatus() { return selfPmsStatus; }
    public void setSelfPmsStatus(String selfPmsStatus) { this.selfPmsStatus = selfPmsStatus; }

    public boolean isSelfPmsSubmitted() { return selfPmsSubmitted; }
    public void setSelfPmsSubmitted(boolean selfPmsSubmitted) { this.selfPmsSubmitted = selfPmsSubmitted; }

    public boolean isSelfPmsFinalized() { return selfPmsFinalized; }
    public void setSelfPmsFinalized(boolean selfPmsFinalized) { this.selfPmsFinalized = selfPmsFinalized; }
}

