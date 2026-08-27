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
@Table(name = "manager_kpi_ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"manager_review_id", "pms_kpi_id"})
})
public class ManagerKpiRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_review_id", nullable = false)
    private ManagerReview managerReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_kpi_id", nullable = false)
    private PmsKpi pmsKpi;

    @Column(name = "manager_rating", precision = 5, scale = 2)
    private BigDecimal managerRating;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public ManagerKpiRating() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ManagerReview getManagerReview() { return managerReview; }
    public void setManagerReview(ManagerReview managerReview) { this.managerReview = managerReview; }

    public PmsKpi getPmsKpi() { return pmsKpi; }
    public void setPmsKpi(PmsKpi pmsKpi) { this.pmsKpi = pmsKpi; }

    public BigDecimal getManagerRating() { return managerRating; }
    public void setManagerRating(BigDecimal managerRating) { this.managerRating = managerRating; }

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

