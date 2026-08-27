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

public class EmployeeDropdownDto {

    private Long employeeId;
    private String employeeCode;
    private String fullName;
    private String designation;
    private String team;

    public EmployeeDropdownDto() {}

    public EmployeeDropdownDto(Long employeeId, String employeeCode, String fullName, String designation, String team) {
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.designation = designation;
        this.team = team;
    }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
}

