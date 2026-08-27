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

import com.aseuro.pms.manager.entity.PmsAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PmsAssignmentRepository extends JpaRepository<PmsAssignment, Long> {

    @Query("SELECT pa FROM PmsAssignment pa " +
           "JOIN FETCH pa.employee e " +
           "JOIN FETCH pa.manager m " +
           "JOIN FETCH pa.pmsCycle pc " +
           "JOIN FETCH pa.designation des " +
           "WHERE pa.employee.id = :employeeId AND pa.pmsCycle.id = :cycleId")
    Optional<PmsAssignment> findByEmployeeIdAndPmsCycleId(@Param("employeeId") Long employeeId, @Param("cycleId") Long cycleId);

    @Query("SELECT pa FROM PmsAssignment pa " +
           "JOIN FETCH pa.employee e " +
           "JOIN FETCH pa.manager m " +
           "JOIN FETCH pa.pmsCycle pc " +
           "JOIN FETCH pa.designation des " +
           "WHERE pa.employee.id = :employeeId " +
           "ORDER BY pc.year DESC, pc.month DESC")
    List<PmsAssignment> findByEmployeeIdOrderByCycleDesc(@Param("employeeId") Long employeeId);

    @Query("SELECT pa FROM PmsAssignment pa " +
           "JOIN FETCH pa.employee e " +
           "JOIN FETCH pa.manager m " +
           "JOIN FETCH pa.pmsCycle pc " +
           "JOIN FETCH pa.designation des " +
           "WHERE pa.manager.id = :managerId AND pa.pmsCycle.id = :cycleId " +
           "ORDER BY e.fullName ASC")
    List<PmsAssignment> findByManagerIdAndPmsCycleId(@Param("managerId") Long managerId, @Param("cycleId") Long cycleId);

    @Query("SELECT pa FROM PmsAssignment pa " +
           "JOIN FETCH pa.employee e " +
           "JOIN FETCH pa.manager m " +
           "JOIN FETCH pa.pmsCycle pc " +
           "JOIN FETCH pa.designation des " +
           "WHERE pa.id = :id AND pa.manager.id = :managerId")
    Optional<PmsAssignment> findByIdAndManagerId(@Param("id") Long id, @Param("managerId") Long managerId);

    @Query("SELECT pa FROM PmsAssignment pa " +
           "JOIN FETCH pa.employee e " +
           "JOIN FETCH pa.manager m " +
           "JOIN FETCH pa.pmsCycle pc " +
           "JOIN FETCH pa.designation des " +
           "WHERE pa.id = :id AND pa.employee.id = :employeeId")
    Optional<PmsAssignment> findByIdAndEmployeeId(@Param("id") Long id, @Param("employeeId") Long employeeId);
}

