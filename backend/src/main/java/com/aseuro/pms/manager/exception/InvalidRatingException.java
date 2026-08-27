package com.aseuro.pms.manager.exception;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;

import org.springframework.http.HttpStatus;

public class InvalidRatingException extends PmsException {
    public InvalidRatingException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

