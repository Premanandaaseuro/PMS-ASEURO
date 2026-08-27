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

public class KpiItemDto {

    private Long pmsKpiId;
    private Long kpiId;
    private String kpiName;
    private String measurementCriteria;
    private BigDecimal weightage;
    private BigDecimal selfRating;
    private String selfComments;
    private BigDecimal managerRating;
    private String managerComments;

    public KpiItemDto() {}

    public Long getPmsKpiId() { return pmsKpiId; }
    public void setPmsKpiId(Long pmsKpiId) { this.pmsKpiId = pmsKpiId; }

    public Long getKpiId() { return kpiId; }
    public void setKpiId(Long kpiId) { this.kpiId = kpiId; }

    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public String getMeasurementCriteria() { return measurementCriteria; }
    public void setMeasurementCriteria(String measurementCriteria) { this.measurementCriteria = measurementCriteria; }

    public BigDecimal getWeightage() { return weightage; }
    public void setWeightage(BigDecimal weightage) { this.weightage = weightage; }

    public BigDecimal getSelfRating() { return selfRating; }
    public void setSelfRating(BigDecimal selfRating) { this.selfRating = selfRating; }

    public String getSelfComments() { return selfComments; }
    public void setSelfComments(String selfComments) { this.selfComments = selfComments; }

    public BigDecimal getManagerRating() { return managerRating; }
    public void setManagerRating(BigDecimal managerRating) { this.managerRating = managerRating; }

    public String getManagerComments() { return managerComments; }
    public void setManagerComments(String managerComments) { this.managerComments = managerComments; }
}

