package com.aseuro.pms.manager.entity;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "hr_kpi_ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"hr_review_id", "pms_kpi_id"})
})
public class HrKpiRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_review_id", nullable = false)
    private HrReview hrReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_kpi_id", nullable = false)
    private PmsKpi pmsKpi;

    @Column(name = "hr_rating", precision = 5, scale = 2)
    private BigDecimal hrRating;

    @Column(name = "final_weighted_score", precision = 10, scale = 4)
    private BigDecimal finalWeightedScore;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public HrKpiRating() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public HrReview getHrReview() { return hrReview; }
    public void setHrReview(HrReview hrReview) { this.hrReview = hrReview; }

    public PmsKpi getPmsKpi() { return pmsKpi; }
    public void setPmsKpi(PmsKpi pmsKpi) { this.pmsKpi = pmsKpi; }

    public BigDecimal getHrRating() { return hrRating; }
    public void setHrRating(BigDecimal hrRating) { this.hrRating = hrRating; }

    public BigDecimal getFinalWeightedScore() { return finalWeightedScore; }
    public void setFinalWeightedScore(BigDecimal finalWeightedScore) { this.finalWeightedScore = finalWeightedScore; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}

