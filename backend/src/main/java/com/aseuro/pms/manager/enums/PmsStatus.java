package com.aseuro.pms.manager.enums;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;

public enum PmsStatus {
    PMS_NOT_STARTED,
    PMS_STARTED,
    SELF_ASSESSMENT_DRAFT,
    SELF_ASSESSMENT_SUBMITTED,
    MANAGER_REVIEW_PENDING,
    MANAGER_REVIEW_SUBMITTED,
    HR_REVIEW_PENDING,
    HR_REVIEW_COMPLETED,
    RATING_AND_POINTS_CALCULATED,
    FINAL_ANALYSIS,
    FINAL_RESULT_PUBLISHED,
    COMPLETED
}

