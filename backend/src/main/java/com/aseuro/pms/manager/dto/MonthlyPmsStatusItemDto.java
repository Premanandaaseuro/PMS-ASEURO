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

import java.math.BigDecimal;

public class MonthlyPmsStatusItemDto {

    private Long employeeId;
    private String employeeCode;
    private String fullName;
    private String designation;
    private String team;
    private Long assignmentId;
    private String pmsStatus;
    private String selfRatingStatus;
    private String managerReviewStatus;
    private BigDecimal averageSelfRating;
    private BigDecimal averageManagerRating;
    private BigDecimal finalScore;
    private String ratingCategory;

    public MonthlyPmsStatusItemDto() {}

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public String getPmsStatus() { return pmsStatus; }
    public void setPmsStatus(String pmsStatus) { this.pmsStatus = pmsStatus; }

    public String getSelfRatingStatus() { return selfRatingStatus; }
    public void setSelfRatingStatus(String selfRatingStatus) { this.selfRatingStatus = selfRatingStatus; }

    public String getManagerReviewStatus() { return managerReviewStatus; }
    public void setManagerReviewStatus(String managerReviewStatus) { this.managerReviewStatus = managerReviewStatus; }

    public BigDecimal getAverageSelfRating() { return averageSelfRating; }
    public void setAverageSelfRating(BigDecimal averageSelfRating) { this.averageSelfRating = averageSelfRating; }

    public BigDecimal getAverageManagerRating() { return averageManagerRating; }
    public void setAverageManagerRating(BigDecimal averageManagerRating) { this.averageManagerRating = averageManagerRating; }

    public BigDecimal getFinalScore() { return finalScore; }
    public void setFinalScore(BigDecimal finalScore) { this.finalScore = finalScore; }

    public String getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(String ratingCategory) { this.ratingCategory = ratingCategory; }
}

