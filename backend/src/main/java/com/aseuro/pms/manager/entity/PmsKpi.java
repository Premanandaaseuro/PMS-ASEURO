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
@Table(name = "pms_kpis", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"pms_assignment_id", "kpi_id"})
})
public class PmsKpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_assignment_id", nullable = false)
    private PmsAssignment pmsAssignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", nullable = false)
    private Kpi kpi;

    @Column(name = "kpi_name", nullable = false, length = 255)
    private String kpiName;

    @Column(name = "measurement_criteria", nullable = false, columnDefinition = "TEXT")
    private String measurementCriteria;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weightage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public PmsKpi() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PmsAssignment getPmsAssignment() { return pmsAssignment; }
    public void setPmsAssignment(PmsAssignment pmsAssignment) { this.pmsAssignment = pmsAssignment; }

    public Kpi getKpi() { return kpi; }
    public void setKpi(Kpi kpi) { this.kpi = kpi; }

    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public String getMeasurementCriteria() { return measurementCriteria; }
    public void setMeasurementCriteria(String measurementCriteria) { this.measurementCriteria = measurementCriteria; }

    public BigDecimal getWeightage() { return weightage; }
    public void setWeightage(BigDecimal weightage) { this.weightage = weightage; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

