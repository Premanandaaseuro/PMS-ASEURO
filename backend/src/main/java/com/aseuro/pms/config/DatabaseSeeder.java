package com.aseuro.pms.config;

import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.DepartmentRepository;
import com.aseuro.pms.repository.DesignationRepository;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Checking and seeding initial PMS database records...");

        // 1. Seed Departments
        seedDepartments();

        // 2. Seed Designations
        seedDesignations();

        // 3. Seed Primary HR User (aishwarya.logaraj@aseuro.in)
        seedHrUser();

        // 4. Seed Initial Manager & Employee for testing login
        seedInitialManagerAndEmployee();

        log.info("Database seeding completed successfully.");
    }

    private void seedDepartments() {
        List<String> defaultDepts = List.of(
                "Engineering",
                "Human Resources",
                "Sales & Marketing",
                "Product & Design",
                "Finance & Operations"
        );
        for (String name : defaultDepts) {
            if (departmentRepository.findByNameIgnoreCase(name).isEmpty()) {
                Department dept = new Department(name, name + " Department");
                departmentRepository.save(dept);
                log.info("Seeded department: {}", name);
            }
        }
    }

    private void seedDesignations() {
        List<String> defaultDesignations = List.of(
                "Engineering Manager",
                "Senior Software Engineer",
                "Software Engineer",
                "Associate Software Engineer",
                "HR Lead",
                "HR Executive",
                "Product Manager",
                "UI/UX Designer",
                "QA Engineer"
        );
        for (String name : defaultDesignations) {
            if (designationRepository.findByNameIgnoreCase(name).isEmpty()) {
                Designation designation = new Designation(name, name + " Role");
                designationRepository.save(designation);
                log.info("Seeded designation: {}", name);
            }
        }
    }

    private void seedHrUser() {
        String hrEmail = "aishwarya.logaraj@aseuro.in";
        User savedUser = userRepository.findByEmailIgnoreCase(hrEmail).orElse(null);
        if (savedUser == null) {
            User hrUser = new User();
            hrUser.setUsername("aishwarya.logaraj");
            hrUser.setEmail(hrEmail);
            hrUser.setPasswordHash(passwordEncoder.encode("Aseuro@123"));
            hrUser.setRole(UserRole.HR);
            hrUser.setStatus(RecordStatus.ACTIVE);
            savedUser = userRepository.save(hrUser);
            log.info("Provisioned HR Administrator account: {}", hrEmail);
        }

        // Also create employee profile for HR if not present
        if (employeeRepository.findByEmailIgnoreCase(hrEmail).isEmpty() &&
            employeeRepository.findByEmployeeCodeIgnoreCase("HR-001").isEmpty()) {
            Department hrDept = departmentRepository.findByNameIgnoreCase("Human Resources").orElse(null);
            Designation hrRole = designationRepository.findByNameIgnoreCase("HR Lead").orElse(null);

            Employee hrEmp = new Employee();
            hrEmp.setUser(savedUser);
            hrEmp.setEmployeeCode("HR-001");
            hrEmp.setFullName("Aishwarya Logaraj");
            hrEmp.setEmail(hrEmail);
            if (hrDept != null) hrEmp.setDepartmentId(hrDept.getId());
            if (hrRole != null) hrEmp.setDesignationId(hrRole.getId());
            hrEmp.setJoiningDate(LocalDate.of(2023, 1, 15));
            hrEmp.setStatus(RecordStatus.ACTIVE);
            employeeRepository.save(hrEmp);
        }
    }

    private void seedInitialManagerAndEmployee() {
        Department engDept = departmentRepository.findByNameIgnoreCase("Engineering").orElse(null);
        Designation mgrDesig = designationRepository.findByNameIgnoreCase("Engineering Manager").orElse(null);
        Designation sdeDesig = designationRepository.findByNameIgnoreCase("Software Engineer").orElse(null);

        // 1. Seed Manager
        String mgrEmail = "manager@aseuro.in";
        User savedMgrUser = userRepository.findByEmailIgnoreCase(mgrEmail).orElse(null);
        if (savedMgrUser == null) {
            User mgrUser = new User();
            mgrUser.setUsername("rajesh.manager");
            mgrUser.setEmail(mgrEmail);
            mgrUser.setPasswordHash(passwordEncoder.encode("Manager@123"));
            mgrUser.setRole(UserRole.MANAGER);
            mgrUser.setStatus(RecordStatus.ACTIVE);
            savedMgrUser = userRepository.save(mgrUser);
            log.info("Provisioned Manager account: {}", mgrEmail);
        }

        Employee savedMgr = employeeRepository.findByEmailIgnoreCase(mgrEmail)
                .or(() -> employeeRepository.findByEmployeeCodeIgnoreCase("MGR-101"))
                .orElse(null);

        if (savedMgr == null) {
            Employee mgrEmp = new Employee();
            mgrEmp.setUser(savedMgrUser);
            mgrEmp.setEmployeeCode("MGR-101");
            mgrEmp.setFullName("Rajesh Sharma");
            mgrEmp.setEmail(mgrEmail);
            if (engDept != null) mgrEmp.setDepartmentId(engDept.getId());
            if (mgrDesig != null) mgrEmp.setDesignationId(mgrDesig.getId());
            mgrEmp.setJoiningDate(LocalDate.of(2022, 6, 1));
            mgrEmp.setStatus(RecordStatus.ACTIVE);
            savedMgr = employeeRepository.save(mgrEmp);
        }

        // 2. Seed Employee
        String empEmail = "employee@aseuro.in";
        User savedEmpUser = userRepository.findByEmailIgnoreCase(empEmail).orElse(null);
        if (savedEmpUser == null) {
            User empUser = new User();
            empUser.setUsername("kiran.employee");
            empUser.setEmail(empEmail);
            empUser.setPasswordHash(passwordEncoder.encode("Employee@123"));
            empUser.setRole(UserRole.EMPLOYEE);
            empUser.setStatus(RecordStatus.ACTIVE);
            savedEmpUser = userRepository.save(empUser);
            log.info("Provisioned Employee account: {}", empEmail);
        }

        if (employeeRepository.findByEmailIgnoreCase(empEmail).isEmpty() &&
            employeeRepository.findByEmployeeCodeIgnoreCase("EMP-201").isEmpty()) {
            Employee emp = new Employee();
            emp.setUser(savedEmpUser);
            emp.setEmployeeCode("EMP-201");
            emp.setFullName("Kiran Kumar");
            emp.setEmail(empEmail);
            if (engDept != null) emp.setDepartmentId(engDept.getId());
            if (sdeDesig != null) emp.setDesignationId(sdeDesig.getId());
            if (savedMgr != null) emp.setManagerId(savedMgr.getId());
            emp.setJoiningDate(LocalDate.of(2024, 2, 10));
            emp.setStatus(RecordStatus.ACTIVE);
            employeeRepository.save(emp);
        }
    }
}
