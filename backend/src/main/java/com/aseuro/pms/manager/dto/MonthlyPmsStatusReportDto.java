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

import java.util.ArrayList;
import java.util.List;

public class MonthlyPmsStatusReportDto {

    private Long cycleId;
    private String cycleName;
    private Integer month;
    private Integer year;
    private int totalAssigned;
    private int pendingSelfRatingCount;
    private int completedSelfRatingCount;
    private int pendingManagerReviewCount;
    private int completedManagerReviewCount;
    private int finalizedCount;
    private List<MonthlyPmsStatusItemDto> employees = new ArrayList<>();

    public MonthlyPmsStatusReportDto() {}

    public Long getCycleId() { return cycleId; }
    public void setCycleId(Long cycleId) { this.cycleId = cycleId; }

    public String getCycleName() { return cycleName; }
    public void setCycleName(String cycleName) { this.cycleName = cycleName; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public int getTotalAssigned() { return totalAssigned; }
    public void setTotalAssigned(int totalAssigned) { this.totalAssigned = totalAssigned; }

    public int getPendingSelfRatingCount() { return pendingSelfRatingCount; }
    public void setPendingSelfRatingCount(int pendingSelfRatingCount) { this.pendingSelfRatingCount = pendingSelfRatingCount; }

    public int getCompletedSelfRatingCount() { return completedSelfRatingCount; }
    public void setCompletedSelfRatingCount(int completedSelfRatingCount) { this.completedSelfRatingCount = completedSelfRatingCount; }

    public int getPendingManagerReviewCount() { return pendingManagerReviewCount; }
    public void setPendingManagerReviewCount(int pendingManagerReviewCount) { this.pendingManagerReviewCount = pendingManagerReviewCount; }

    public int getCompletedManagerReviewCount() { return completedManagerReviewCount; }
    public void setCompletedManagerReviewCount(int completedManagerReviewCount) { this.completedManagerReviewCount = completedManagerReviewCount; }

    public int getFinalizedCount() { return finalizedCount; }
    public void setFinalizedCount(int finalizedCount) { this.finalizedCount = finalizedCount; }

    public List<MonthlyPmsStatusItemDto> getEmployees() { return employees; }
    public void setEmployees(List<MonthlyPmsStatusItemDto> employees) { this.employees = employees; }
}

