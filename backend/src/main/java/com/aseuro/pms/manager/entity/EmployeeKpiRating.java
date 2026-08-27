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
@Table(name = "employee_kpi_ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_review_id", "pms_kpi_id"})
})
public class EmployeeKpiRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_review_id", nullable = false)
    private EmployeeReview employeeReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_kpi_id", nullable = false)
    private PmsKpi pmsKpi;

    @Column(name = "self_rating", precision = 5, scale = 2)
    private BigDecimal selfRating;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public EmployeeKpiRating() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EmployeeReview getEmployeeReview() { return employeeReview; }
    public void setEmployeeReview(EmployeeReview employeeReview) { this.employeeReview = employeeReview; }

    public PmsKpi getPmsKpi() { return pmsKpi; }
    public void setPmsKpi(PmsKpi pmsKpi) { this.pmsKpi = pmsKpi; }

    public BigDecimal getSelfRating() { return selfRating; }
    public void setSelfRating(BigDecimal selfRating) { this.selfRating = selfRating; }

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

