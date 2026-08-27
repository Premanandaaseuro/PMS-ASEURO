package com.aseuro.pms.manager.repository;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;

import com.aseuro.pms.manager.entity.ManagerKpiRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerKpiRatingRepository extends JpaRepository<ManagerKpiRating, Long> {
    List<ManagerKpiRating> findByManagerReviewId(Long managerReviewId);
    Optional<ManagerKpiRating> findByManagerReviewIdAndPmsKpiId(Long managerReviewId, Long pmsKpiId);
}

