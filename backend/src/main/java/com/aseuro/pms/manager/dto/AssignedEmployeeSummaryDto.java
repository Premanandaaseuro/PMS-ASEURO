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

public class AssignedEmployeeSummaryDto {

    private Long employeeId;
    private String employeeCode;
    private String fullName;
    private String email;
    private String designation;
    private String department;
    private String team;
    private Long assignmentId;
    private String pmsStatus;
    private String managerReviewStatus;
    private boolean selfAssessmentSubmitted;
    private boolean managerReviewSubmitted;

    public AssignedEmployeeSummaryDto() {}

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public String getPmsStatus() { return pmsStatus; }
    public void setPmsStatus(String pmsStatus) { this.pmsStatus = pmsStatus; }

    public String getManagerReviewStatus() { return managerReviewStatus; }
    public void setManagerReviewStatus(String managerReviewStatus) { this.managerReviewStatus = managerReviewStatus; }

    public boolean isSelfAssessmentSubmitted() { return selfAssessmentSubmitted; }
    public void setSelfAssessmentSubmitted(boolean selfAssessmentSubmitted) { this.selfAssessmentSubmitted = selfAssessmentSubmitted; }

    public boolean isManagerReviewSubmitted() { return managerReviewSubmitted; }
    public void setManagerReviewSubmitted(boolean managerReviewSubmitted) { this.managerReviewSubmitted = managerReviewSubmitted; }
}

