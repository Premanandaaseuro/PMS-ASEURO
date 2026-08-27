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

import com.aseuro.pms.manager.entity.PmsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PmsHistoryRepository extends JpaRepository<PmsHistory, Long> {

    @Query("SELECT ph FROM PmsHistory ph " +
           "JOIN FETCH ph.employee e " +
           "JOIN FETCH ph.manager m " +
           "JOIN FETCH ph.pmsCycle pc " +
           "JOIN FETCH ph.finalResult fr " +
           "WHERE ph.employee.id = :employeeId " +
           "ORDER BY pc.year DESC, pc.month DESC")
    List<PmsHistory> findByEmployeeIdOrderByCycleDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT ph FROM PmsHistory ph " +
           "JOIN FETCH ph.employee e " +
           "JOIN FETCH ph.manager m " +
           "JOIN FETCH ph.pmsCycle pc " +
           "JOIN FETCH ph.finalResult fr " +
           "WHERE ph.manager.id = :managerId " +
           "ORDER BY pc.year DESC, pc.month DESC")
    List<PmsHistory> findByManagerIdOrderByCycleDesc(@Param("managerId") Long managerId);
}

