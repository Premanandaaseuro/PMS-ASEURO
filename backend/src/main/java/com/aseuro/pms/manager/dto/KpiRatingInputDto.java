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

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class KpiRatingInputDto {

    @NotNull(message = "PMS KPI ID is required")
    private Long pmsKpiId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.00", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.00", message = "Rating cannot exceed 5.0")
    private BigDecimal rating;

    private String comments;

    public KpiRatingInputDto() {}

    public KpiRatingInputDto(Long pmsKpiId, BigDecimal rating, String comments) {
        this.pmsKpiId = pmsKpiId;
        this.rating = rating;
        this.comments = comments;
    }

    public Long getPmsKpiId() { return pmsKpiId; }
    public void setPmsKpiId(Long pmsKpiId) { this.pmsKpiId = pmsKpiId; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}

