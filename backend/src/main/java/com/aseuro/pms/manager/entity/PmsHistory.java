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
@Table(name = "pms_history")
public class PmsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_assignment_id", nullable = false, unique = true)
    private PmsAssignment pmsAssignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Employee manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pms_cycle_id", nullable = false)
    private PmsCycle pmsCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "final_result_id", nullable = false)
    private FinalPmsResult finalResult;

    @Column(name = "finalized_at", nullable = false)
    private OffsetDateTime finalizedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public PmsHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PmsAssignment getPmsAssignment() { return pmsAssignment; }
    public void setPmsAssignment(PmsAssignment pmsAssignment) { this.pmsAssignment = pmsAssignment; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }

    public PmsCycle getPmsCycle() { return pmsCycle; }
    public void setPmsCycle(PmsCycle pmsCycle) { this.pmsCycle = pmsCycle; }

    public FinalPmsResult getFinalResult() { return finalResult; }
    public void setFinalResult(FinalPmsResult finalResult) { this.finalResult = finalResult; }

    public OffsetDateTime getFinalizedAt() { return finalizedAt; }
    public void setFinalizedAt(OffsetDateTime finalizedAt) { this.finalizedAt = finalizedAt; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

