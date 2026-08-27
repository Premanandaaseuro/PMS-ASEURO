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
import java.time.OffsetDateTime;

@Entity
@Table(name = "hr_reviews")
public class HrReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_assignment_id", nullable = false, unique = true)
    private PmsAssignment pmsAssignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_user_id", nullable = false)
    private User hrUser;

    @Column(nullable = false, length = 40)
    private String status = "DRAFT";

    @Column(name = "review_comments", columnDefinition = "TEXT")
    private String reviewComments;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public HrReview() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PmsAssignment getPmsAssignment() { return pmsAssignment; }
    public void setPmsAssignment(PmsAssignment pmsAssignment) { this.pmsAssignment = pmsAssignment; }

    public User getHrUser() { return hrUser; }
    public void setHrUser(User hrUser) { this.hrUser = hrUser; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewComments() { return reviewComments; }
    public void setReviewComments(String reviewComments) { this.reviewComments = reviewComments; }

    public OffsetDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(OffsetDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}

