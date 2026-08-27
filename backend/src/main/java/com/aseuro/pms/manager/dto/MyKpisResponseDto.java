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

public class MyKpisResponseDto {

    private Long assignmentId;
    private Long cycleId;
    private String cycleName;
    private Integer month;
    private Integer year;
    private String designation;
    private String reportingManagerName;
    private String status;
    private String reviewStatus;
    private boolean isEditable;
    private boolean isSubmitted;
    private boolean isFinalized;
    private OffsetDateTime submittedAt;
    private String generalComments;
    private BigDecimal finalScore;
    private String ratingCategory;
    private String hrComments;
    private OffsetDateTime publishedAt;
    private List<KpiItemDto> kpis = new ArrayList<>();

    public MyKpisResponseDto() {}

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

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getReportingManagerName() { return reportingManagerName; }
    public void setReportingManagerName(String reportingManagerName) { this.reportingManagerName = reportingManagerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }

    public boolean isEditable() { return isEditable; }
    public void setEditable(boolean editable) { isEditable = editable; }

    public boolean isSubmitted() { return isSubmitted; }
    public void setSubmitted(boolean submitted) { isSubmitted = submitted; }

    public boolean isFinalized() { return isFinalized; }
    public void setFinalized(boolean finalized) { isFinalized = finalized; }

    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getGeneralComments() { return generalComments; }
    public void setGeneralComments(String generalComments) { this.generalComments = generalComments; }

    public BigDecimal getFinalScore() { return finalScore; }
    public void setFinalScore(BigDecimal finalScore) { this.finalScore = finalScore; }

    public String getRatingCategory() { return ratingCategory; }
    public void setRatingCategory(String ratingCategory) { this.ratingCategory = ratingCategory; }

    public String getHrComments() { return hrComments; }
    public void setHrComments(String hrComments) { this.hrComments = hrComments; }

    public OffsetDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(OffsetDateTime publishedAt) { this.publishedAt = publishedAt; }

    public List<KpiItemDto> getKpis() { return kpis; }
    public void setKpis(List<KpiItemDto> kpis) { this.kpis = kpis; }
}

