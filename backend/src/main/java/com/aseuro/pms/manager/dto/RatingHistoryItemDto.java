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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class RatingHistoryItemDto {

    private Long assignmentId;
    private Long cycleId;
    private String cycleName;
    private Integer month;
    private Integer year;
    private String pmsStatus;
    private BigDecimal averageSelfRating;
    private BigDecimal averageManagerRating;
    private BigDecimal finalOverallScore;
    private String ratingCategory;
    private OffsetDateTime publishedAt;
    private List<EmployeeKpiReviewItemDto> kpis = new ArrayList<>();

    public RatingHistoryItemDto() {}

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

    public String getPmsStatus() { return pmsStatus; }
    public void setPmsStatus(String pmsStatus) { this.pmsStatus = pmsStatus; }

    public BigDecimal getAverageSelfRating() { return averageSelfRating; }
    public void setAverageSelfRating(BigDecimal averageSelfRating) { this.averageSelfRating = averageSelfRating; }

    public BigDecimal getAverageManagerRating() { return averageManagerRating; }
    public void setAverageManagerRating(BigDecimal averageManagerRating) { this.averageManagerRating = averageManagerRating; }

    public BigDecimal getFinalOverallScore() { return finalOverallScore; }
    public void setFinalOverallScore(BigDecimal finalOverallScore) { this.finalOverallScore = finalOverallScore; }

    public String getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(String ratingCategory) { this.ratingCategory = ratingCategory; }

    public OffsetDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(OffsetDateTime publishedAt) { this.publishedAt = publishedAt; }

    public List<EmployeeKpiReviewItemDto> getKpis() { return kpis; }
    public void setKpis(List<EmployeeKpiReviewItemDto> kpis) { this.kpis = kpis; }
}

