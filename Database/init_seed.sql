-- ============================================================
-- PMS MANAGER MODULE TEST SEED DATA
-- Strictly uses existing tables, columns, constraints and enums
-- Password for all test users: Password@123
-- ============================================================

-- 1. DEPARTMENTS
INSERT INTO departments (id, name, description, status) VALUES
(1, 'Engineering', 'Core software development and technical operations', 'ACTIVE'),
(2, 'Product Management', 'Product strategy, roadmap and user experience', 'ACTIVE'),
(3, 'Human Resources', 'Talent acquisition, employee success and performance management', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 2. TEAMS
INSERT INTO teams (id, department_id, name, description, status) VALUES
(1, 1, 'Core Platform', 'Backend services, APIs and platform infrastructure', 'ACTIVE'),
(2, 1, 'Web Frontend', 'Web user interface, design system and client apps', 'ACTIVE'),
(3, 2, 'Product Operations', 'Feature analysis and customer engagement', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 3. DESIGNATIONS
INSERT INTO designations (id, name, description, status) VALUES
(1, 'Software Engineer', 'Individual contributor focused on feature delivery', 'ACTIVE'),
(2, 'Senior Software Engineer', 'Senior technical engineer driving architecture and mentoring', 'ACTIVE'),
(3, 'Engineering Manager', 'People and technical manager leading engineering teams', 'ACTIVE'),
(4, 'HR Specialist', 'HR professional handling organizational performance', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 4. USERS (Pass: Password@123)
INSERT INTO users (id, username, email, password_hash, role, status) VALUES
(1, 'sarah.connor', 'manager1@company.com', crypt('Password@123', gen_salt('bf', 10)), 'MANAGER', 'ACTIVE'),
(2, 'john.doe', 'manager2@company.com', crypt('Password@123', gen_salt('bf', 10)), 'MANAGER', 'ACTIVE'),
(3, 'alice.smith', 'employee1@company.com', crypt('Password@123', gen_salt('bf', 10)), 'EMPLOYEE', 'ACTIVE'),
(4, 'bob.jones', 'employee2@company.com', crypt('Password@123', gen_salt('bf', 10)), 'EMPLOYEE', 'ACTIVE'),
(5, 'charlie.brown', 'employee3@company.com', crypt('Password@123', gen_salt('bf', 10)), 'EMPLOYEE', 'ACTIVE'),
(6, 'emma.watson', 'hr1@company.com', crypt('Password@123', gen_salt('bf', 10)), 'HR', 'ACTIVE'),
(7, 'inactive.manager', 'inactive_mgr@company.com', crypt('Password@123', gen_salt('bf', 10)), 'MANAGER', 'INACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 5. EMPLOYEES
-- Managers first (so employees can reference them as manager_id)
INSERT INTO employees (id, user_id, employee_code, full_name, email, department_id, team_id, designation_id, manager_id, joining_date, status) VALUES
(1, 1, 'MGR-001', 'Sarah Connor', 'manager1@company.com', 1, 1, 3, NULL, '2022-01-15', 'ACTIVE'),
(2, 2, 'MGR-002', 'John Doe', 'manager2@company.com', 1, 2, 3, NULL, '2022-03-01', 'ACTIVE'),
(6, 6, 'HR-001', 'Emma Watson', 'hr1@company.com', 3, NULL, 4, NULL, '2021-06-01', 'ACTIVE'),
(7, 7, 'MGR-003', 'David Banner', 'inactive_mgr@company.com', 1, 1, 3, NULL, '2023-01-10', 'INACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Regular employees referencing their respective managers
INSERT INTO employees (id, user_id, employee_code, full_name, email, department_id, team_id, designation_id, manager_id, joining_date, status) VALUES
(3, 3, 'EMP-001', 'Alice Smith', 'employee1@company.com', 1, 1, 1, 1, '2023-02-01', 'ACTIVE'),
(4, 4, 'EMP-002', 'Bob Jones', 'employee2@company.com', 1, 2, 2, 1, '2022-08-15', 'ACTIVE'),
(5, 5, 'EMP-003', 'Charlie Brown', 'employee3@company.com', 1, 1, 1, 2, '2023-05-10', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Set Sarah's manager to John Doe (MGR-002) for Manager-as-Employee PMS hierarchy
UPDATE employees SET manager_id = 2 WHERE id = 1;

-- 6. KPIS
INSERT INTO kpis (id, name, description, measurement_criteria, status) VALUES
(1, 'Code Quality & Maintainability', 'Adherence to architectural patterns, clean code, and zero regressions', 'PR reviews, static analysis ratings, automated test coverage > 85%', 'ACTIVE'),
(2, 'Sprint Delivery & Velocity', 'Timely completion of committed sprint backlog and milestone goals', 'Sprint burndown, on-time story delivery percentage >= 90%', 'ACTIVE'),
(3, 'Collaboration & Knowledge Sharing', 'Effective cross-team communication, mentorship, and documentation', 'Peer feedback, architecture review participation, documentation accuracy', 'ACTIVE'),
(4, 'System Reliability & Performance', 'Uptime, latency optimization, and rapid incident resolution', 'Production SLO adherence, MTTR < 30 mins for P1/P2 incidents', 'ACTIVE'),
(5, 'Team Leadership & Mentorship', 'Fostering engineering talent, conducting 1:1s, career pathing', 'Team retention, quarterly growth metrics, 360 feedback', 'ACTIVE'),
(6, 'Strategic Alignment & Roadmapping', 'Aligning technical roadmaps with business objectives', 'Quarterly roadmap completion and stakeholder satisfaction score', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 7. DESIGNATION KPIS (Total weightage per designation = 100%)
-- Software Engineer (Designation ID 1) -> 35% + 35% + 30% = 100%
INSERT INTO designation_kpis (id, designation_id, kpi_id, weightage, status) VALUES
(1, 1, 1, 35.00, 'ACTIVE'),
(2, 1, 2, 35.00, 'ACTIVE'),
(3, 1, 3, 30.00, 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Senior Software Engineer (Designation ID 2) -> 30% + 30% + 20% + 20% = 100%
INSERT INTO designation_kpis (id, designation_id, kpi_id, weightage, status) VALUES
(4, 2, 1, 30.00, 'ACTIVE'),
(5, 2, 2, 30.00, 'ACTIVE'),
(6, 2, 3, 20.00, 'ACTIVE'),
(7, 2, 4, 20.00, 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Engineering Manager (Designation ID 3) -> 30% + 30% + 20% + 20% = 100%
INSERT INTO designation_kpis (id, designation_id, kpi_id, weightage, status) VALUES
(8, 3, 3, 30.00, 'ACTIVE'),
(9, 3, 4, 20.00, 'ACTIVE'),
(10, 3, 5, 30.00, 'ACTIVE'),
(11, 3, 6, 20.00, 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 8. PMS CYCLES
INSERT INTO pms_cycles (id, name, month, year, start_date, end_date, status) VALUES
(1, 'July 2026 PMS Cycle', 7, 2026, '2026-07-01', '2026-07-31', 'ACTIVE'),
(2, 'August 2026 PMS Cycle', 8, 2026, '2026-08-01', '2026-08-31', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 9. PMS ASSIGNMENTS (Connecting Employee + Manager + Cycle + Designation)
-- ============================================================
-- Cycle 2 (August 2026 - Active Cycle)
-- ============================================================
-- Assignment 1: Sarah Connor (Manager 1) as Employee, reporting to John Doe (Manager 2)
INSERT INTO pms_assignments (id, employee_id, manager_id, pms_cycle_id, designation_id, status) VALUES
(1, 1, 2, 2, 3, 'SELF_ASSESSMENT_DRAFT')
ON CONFLICT (id) DO NOTHING;

-- Assignment 2: Alice Smith (EMP-001), reporting to Sarah Connor (Manager 1)
-- State: Self assessment submitted, ready for Sarah's Manager review
INSERT INTO pms_assignments (id, employee_id, manager_id, pms_cycle_id, designation_id, status) VALUES
(2, 3, 1, 2, 1, 'SELF_ASSESSMENT_SUBMITTED')
ON CONFLICT (id) DO NOTHING;

-- Assignment 3: Bob Jones (EMP-002), reporting to Sarah Connor (Manager 1)
-- State: Manager review submitted and locked
INSERT INTO pms_assignments (id, employee_id, manager_id, pms_cycle_id, designation_id, status) VALUES
(3, 4, 1, 2, 2, 'MANAGER_REVIEW_SUBMITTED')
ON CONFLICT (id) DO NOTHING;

-- Assignment 4: Charlie Brown (EMP-003), reporting to John Doe (Manager 2)
-- For cross-manager security testing
INSERT INTO pms_assignments (id, employee_id, manager_id, pms_cycle_id, designation_id, status) VALUES
(4, 5, 2, 2, 1, 'SELF_ASSESSMENT_SUBMITTED')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- Cycle 1 (July 2026 - Past Finalized Cycle for Reports & History)
-- ============================================================
-- Assignment 5: Sarah Connor (Manager 1) in July 2026 (Finalized)
INSERT INTO pms_assignments (id, employee_id, manager_id, pms_cycle_id, designation_id, status) VALUES
(5, 1, 2, 1, 3, 'FINAL_RESULT_PUBLISHED')
ON CONFLICT (id) DO NOTHING;

-- Assignment 6: Alice Smith in July 2026 (Finalized)
INSERT INTO pms_assignments (id, employee_id, manager_id, pms_cycle_id, designation_id, status) VALUES
(6, 3, 1, 1, 1, 'FINAL_RESULT_PUBLISHED')
ON CONFLICT (id) DO NOTHING;

-- Assignment 7: Bob Jones in July 2026 (Finalized)
INSERT INTO pms_assignments (id, employee_id, manager_id, pms_cycle_id, designation_id, status) VALUES
(7, 4, 1, 1, 2, 'FINAL_RESULT_PUBLISHED')
ON CONFLICT (id) DO NOTHING;

-- 10. PMS KPIS (Snapshots per assignment)
-- For Assignment 1 (Sarah Aug 2026 - Manager role KPIs)
INSERT INTO pms_kpis (id, pms_assignment_id, kpi_id, kpi_name, measurement_criteria, weightage) VALUES
(1, 1, 3, 'Collaboration & Knowledge Sharing', 'Peer feedback, architecture review participation', 30.00),
(2, 1, 4, 'System Reliability & Performance', 'Production SLO adherence, MTTR < 30 mins', 20.00),
(3, 1, 5, 'Team Leadership & Mentorship', 'Team retention, quarterly growth metrics, 360 feedback', 30.00),
(4, 1, 6, 'Strategic Alignment & Roadmapping', 'Quarterly roadmap completion and stakeholder score', 20.00)
ON CONFLICT (id) DO NOTHING;

-- For Assignment 2 (Alice Aug 2026 - Software Engineer KPIs)
INSERT INTO pms_kpis (id, pms_assignment_id, kpi_id, kpi_name, measurement_criteria, weightage) VALUES
(5, 2, 1, 'Code Quality & Maintainability', 'PR reviews, static analysis ratings, test coverage > 85%', 35.00),
(6, 2, 2, 'Sprint Delivery & Velocity', 'Sprint burndown, on-time story delivery percentage >= 90%', 35.00),
(7, 2, 3, 'Collaboration & Knowledge Sharing', 'Peer feedback, documentation accuracy', 30.00)
ON CONFLICT (id) DO NOTHING;

-- For Assignment 3 (Bob Aug 2026 - Senior Software Engineer KPIs)
INSERT INTO pms_kpis (id, pms_assignment_id, kpi_id, kpi_name, measurement_criteria, weightage) VALUES
(8, 3, 1, 'Code Quality & Maintainability', 'PR reviews, static analysis ratings, test coverage > 85%', 30.00),
(9, 3, 2, 'Sprint Delivery & Velocity', 'Sprint burndown, on-time story delivery >= 90%', 30.00),
(10, 3, 3, 'Collaboration & Knowledge Sharing', 'Peer feedback, mentorship documentation', 20.00),
(11, 3, 4, 'System Reliability & Performance', 'Production SLO adherence, zero critical incidents', 20.00)
ON CONFLICT (id) DO NOTHING;

-- For Assignment 4 (Charlie Aug 2026 - Manager 2 employee)
INSERT INTO pms_kpis (id, pms_assignment_id, kpi_id, kpi_name, measurement_criteria, weightage) VALUES
(12, 4, 1, 'Code Quality & Maintainability', 'PR reviews, static analysis ratings', 35.00),
(13, 4, 2, 'Sprint Delivery & Velocity', 'On-time delivery >= 90%', 35.00),
(14, 4, 3, 'Collaboration & Knowledge Sharing', 'Documentation accuracy', 30.00)
ON CONFLICT (id) DO NOTHING;

-- For Assignment 5 (Sarah July 2026 - Finalized)
INSERT INTO pms_kpis (id, pms_assignment_id, kpi_id, kpi_name, measurement_criteria, weightage) VALUES
(15, 5, 3, 'Collaboration & Knowledge Sharing', 'Peer feedback', 30.00),
(16, 5, 4, 'System Reliability & Performance', 'Production SLO adherence', 20.00),
(17, 5, 5, 'Team Leadership & Mentorship', 'Quarterly growth metrics', 30.00),
(18, 5, 6, 'Strategic Alignment & Roadmapping', 'Quarterly roadmap completion', 20.00)
ON CONFLICT (id) DO NOTHING;

-- For Assignment 6 (Alice July 2026 - Finalized)
INSERT INTO pms_kpis (id, pms_assignment_id, kpi_id, kpi_name, measurement_criteria, weightage) VALUES
(19, 6, 1, 'Code Quality & Maintainability', 'PR reviews, coverage > 85%', 35.00),
(20, 6, 2, 'Sprint Delivery & Velocity', 'On-time story delivery >= 90%', 35.00),
(21, 6, 3, 'Collaboration & Knowledge Sharing', 'Peer feedback', 30.00)
ON CONFLICT (id) DO NOTHING;

-- For Assignment 7 (Bob July 2026 - Finalized)
INSERT INTO pms_kpis (id, pms_assignment_id, kpi_id, kpi_name, measurement_criteria, weightage) VALUES
(22, 7, 1, 'Code Quality & Maintainability', 'PR reviews, coverage > 85%', 30.00),
(23, 7, 2, 'Sprint Delivery & Velocity', 'On-time story delivery >= 90%', 30.00),
(24, 7, 3, 'Collaboration & Knowledge Sharing', 'Mentorship and documentation', 20.00),
(25, 7, 4, 'System Reliability & Performance', 'Production SLO adherence', 20.00)
ON CONFLICT (id) DO NOTHING;

-- 11. EMPLOYEE REVIEWS & KPI RATINGS
-- Assignment 1 (Sarah draft self-review)
INSERT INTO employee_reviews (id, pms_assignment_id, employee_id, status, comments, submitted_at) VALUES
(1, 1, 1, 'DRAFT', 'Ongoing personal progress for August cycle', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO employee_kpi_ratings (id, employee_review_id, pms_kpi_id, self_rating, comments) VALUES
(1, 1, 1, 4.50, 'Organized 4 architecture alignment workshops with great engagement'),
(2, 1, 2, 4.00, 'Maintained 99.98% platform SLA uptime'),
(3, 1, 3, 4.50, 'Mentored 2 junior engineers who were promoted'),
(4, 1, 4, 4.00, 'Q3 roadmap items on track for delivery')
ON CONFLICT (id) DO NOTHING;

-- Assignment 2 (Alice submitted self-review)
INSERT INTO employee_reviews (id, pms_assignment_id, employee_id, status, comments, submitted_at) VALUES
(2, 2, 3, 'SUBMITTED', 'Achieved all sprint deliverables and expanded test coverage to 92%', '2026-08-20 10:30:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO employee_kpi_ratings (id, employee_review_id, pms_kpi_id, self_rating, comments) VALUES
(5, 2, 5, 4.50, 'Refactored auth module and reached 92% unit test coverage'),
(6, 2, 6, 4.00, 'Completed all 14 assigned sprint user stories on schedule'),
(7, 2, 7, 4.00, 'Authored comprehensive onboarding documentation for new team members')
ON CONFLICT (id) DO NOTHING;

-- Assignment 3 (Bob submitted self-review and manager review)
INSERT INTO employee_reviews (id, pms_assignment_id, employee_id, status, comments, submitted_at) VALUES
(3, 3, 4, 'SUBMITTED', 'Led major performance initiative reducing API latency by 45%', '2026-08-18 14:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO employee_kpi_ratings (id, employee_review_id, pms_kpi_id, self_rating, comments) VALUES
(8, 3, 8, 4.50, 'Zero production defects across 3 major deployments'),
(9, 3, 9, 4.50, 'Consistently delivered sprint backlog ahead of schedule'),
(10, 3, 10, 4.00, 'Conducted 12 PR reviews per week with constructive feedback'),
(11, 3, 11, 5.00, 'Optimized SQL queries and cache strategy reducing p99 latency by 45%')
ON CONFLICT (id) DO NOTHING;

-- Assignment 3 Manager Review (Sarah reviewed Bob)
INSERT INTO manager_reviews (id, pms_assignment_id, manager_id, status, comments, submitted_at) VALUES
(1, 3, 1, 'SUBMITTED', 'Exceptional performance this month. Bob consistently exceeds expectations and drives technical excellence across the frontend team.', '2026-08-22 16:30:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO manager_kpi_ratings (id, manager_review_id, pms_kpi_id, manager_rating, comments) VALUES
(1, 1, 8, 4.50, 'Code quality is outstanding and sets the benchmark for the department'),
(2, 1, 9, 4.50, 'Always dependable and highly disciplined with sprint commitments'),
(3, 1, 10, 4.00, 'Great team player and active participant in design reviews'),
(4, 1, 11, 5.00, 'Remarkable performance engineering results on latency reduction')
ON CONFLICT (id) DO NOTHING;

-- Assignment 5 (Sarah July 2026 Finalized Records)
INSERT INTO employee_reviews (id, pms_assignment_id, employee_id, status, comments, submitted_at) VALUES
(4, 5, 1, 'SUBMITTED', 'Successfully closed Q2 initiatives', '2026-07-25 12:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO employee_kpi_ratings (id, employee_review_id, pms_kpi_id, self_rating, comments) VALUES
(12, 4, 15, 4.00, 'Cross-team synergy meetings completed'),
(13, 4, 16, 4.50, 'System SLA 99.95%'),
(14, 4, 17, 4.50, 'All direct reports completed performance goals'),
(15, 4, 18, 4.00, 'Q2 roadmap delivered 100%')
ON CONFLICT (id) DO NOTHING;

INSERT INTO manager_reviews (id, pms_assignment_id, manager_id, status, comments, submitted_at) VALUES
(2, 5, 2, 'SUBMITTED', 'Strong leadership throughout July cycle', '2026-07-28 15:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO manager_kpi_ratings (id, manager_review_id, pms_kpi_id, manager_rating, comments) VALUES
(5, 2, 15, 4.00, 'Excellent cross-team alignment'),
(6, 2, 16, 4.50, 'Solid stability'),
(7, 2, 17, 4.50, 'Great team growth'),
(8, 2, 18, 4.00, 'Strategic milestones achieved')
ON CONFLICT (id) DO NOTHING;

INSERT INTO hr_reviews (id, pms_assignment_id, hr_user_id, status, review_comments, reviewed_at) VALUES
(1, 5, 6, 'SUBMITTED', 'HR review completed and approved', '2026-07-30 11:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO hr_kpi_ratings (id, hr_review_id, pms_kpi_id, hr_rating, final_weighted_score, comments) VALUES
(1, 1, 15, 4.00, 1.2000, 'Approved'),
(2, 1, 16, 4.50, 0.9000, 'Approved'),
(3, 1, 17, 4.50, 1.3500, 'Approved'),
(4, 1, 18, 4.00, 0.8000, 'Approved')
ON CONFLICT (id) DO NOTHING;

INSERT INTO final_pms_results (id, pms_assignment_id, overall_score, rating_category, hr_comments, published_by, published_at, status) VALUES
(1, 5, 4.2500, 'Exceeds Expectations', 'Final results published for July 2026 cycle', 6, '2026-07-31 16:00:00+00', 'PUBLISHED')
ON CONFLICT (id) DO NOTHING;

INSERT INTO pms_history (id, pms_assignment_id, employee_id, manager_id, pms_cycle_id, final_result_id, finalized_at) VALUES
(1, 5, 1, 2, 1, 1, '2026-07-31 16:00:00+00')
ON CONFLICT (id) DO NOTHING;

-- Assignment 6 (Alice July 2026 Finalized Records)
INSERT INTO employee_reviews (id, pms_assignment_id, employee_id, status, comments, submitted_at) VALUES
(5, 6, 3, 'SUBMITTED', 'July sprint goals accomplished', '2026-07-24 10:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO employee_kpi_ratings (id, employee_review_id, pms_kpi_id, self_rating, comments) VALUES
(16, 5, 19, 4.00, 'Refactored backend services'),
(17, 5, 20, 4.00, 'All stories shipped on time'),
(18, 5, 21, 4.50, 'Helped team members with code reviews')
ON CONFLICT (id) DO NOTHING;

INSERT INTO manager_reviews (id, pms_assignment_id, manager_id, status, comments, submitted_at) VALUES
(3, 6, 1, 'SUBMITTED', 'Great productivity and quality in July', '2026-07-27 14:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO manager_kpi_ratings (id, manager_review_id, pms_kpi_id, manager_rating, comments) VALUES
(9, 3, 19, 4.00, 'High quality code'),
(10, 3, 20, 4.00, 'Consistent delivery'),
(11, 3, 21, 4.50, 'Helpful peer')
ON CONFLICT (id) DO NOTHING;

INSERT INTO hr_reviews (id, pms_assignment_id, hr_user_id, status, review_comments, reviewed_at) VALUES
(2, 6, 6, 'SUBMITTED', 'HR review completed', '2026-07-30 11:30:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO hr_kpi_ratings (id, hr_review_id, pms_kpi_id, hr_rating, final_weighted_score, comments) VALUES
(5, 2, 19, 4.00, 1.4000, 'Approved'),
(6, 2, 20, 4.00, 1.4000, 'Approved'),
(7, 2, 21, 4.50, 1.3500, 'Approved')
ON CONFLICT (id) DO NOTHING;

INSERT INTO final_pms_results (id, pms_assignment_id, overall_score, rating_category, hr_comments, published_by, published_at, status) VALUES
(2, 6, 4.1500, 'Exceeds Expectations', 'July performance result', 6, '2026-07-31 16:30:00+00', 'PUBLISHED')
ON CONFLICT (id) DO NOTHING;

INSERT INTO pms_history (id, pms_assignment_id, employee_id, manager_id, pms_cycle_id, final_result_id, finalized_at) VALUES
(2, 6, 3, 1, 1, 2, '2026-07-31 16:30:00+00')
ON CONFLICT (id) DO NOTHING;

-- Assignment 7 (Bob July 2026 Finalized Records)
INSERT INTO employee_reviews (id, pms_assignment_id, employee_id, status, comments, submitted_at) VALUES
(6, 7, 4, 'SUBMITTED', 'Completed database query optimization', '2026-07-24 11:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO employee_kpi_ratings (id, employee_review_id, pms_kpi_id, self_rating, comments) VALUES
(19, 6, 22, 4.50, 'Clean architecture'),
(20, 6, 23, 4.50, 'Sprint velocity maintained'),
(21, 6, 24, 4.00, 'Knowledge sharing sessions held'),
(22, 6, 25, 4.50, 'Zero latency regressions')
ON CONFLICT (id) DO NOTHING;

INSERT INTO manager_reviews (id, pms_assignment_id, manager_id, status, comments, submitted_at) VALUES
(4, 7, 1, 'SUBMITTED', 'Superb performance and proactive problem solving in July', '2026-07-27 15:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO manager_kpi_ratings (id, manager_review_id, pms_kpi_id, manager_rating, comments) VALUES
(12, 4, 22, 4.50, 'Exemplary code quality'),
(13, 4, 23, 4.50, 'High sprint velocity'),
(14, 4, 24, 4.00, 'Great team mentoring'),
(15, 4, 25, 4.50, 'Reliability champion')
ON CONFLICT (id) DO NOTHING;

INSERT INTO hr_reviews (id, pms_assignment_id, hr_user_id, status, review_comments, reviewed_at) VALUES
(3, 7, 6, 'SUBMITTED', 'HR review completed', '2026-07-30 12:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO hr_kpi_ratings (id, hr_review_id, pms_kpi_id, hr_rating, final_weighted_score, comments) VALUES
(8, 3, 22, 4.50, 1.3500, 'Approved'),
(9, 3, 23, 4.50, 1.3500, 'Approved'),
(10, 3, 24, 4.00, 0.8000, 'Approved'),
(11, 3, 25, 4.50, 0.9000, 'Approved')
ON CONFLICT (id) DO NOTHING;

INSERT INTO final_pms_results (id, pms_assignment_id, overall_score, rating_category, hr_comments, published_by, published_at, status) VALUES
(3, 7, 4.4000, 'Outstanding', 'Outstanding performance throughout July cycle', 6, '2026-07-31 17:00:00+00', 'PUBLISHED')
ON CONFLICT (id) DO NOTHING;

INSERT INTO pms_history (id, pms_assignment_id, employee_id, manager_id, pms_cycle_id, final_result_id, finalized_at) VALUES
(3, 7, 4, 1, 1, 3, '2026-07-31 17:00:00+00')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence values to max IDs to allow subsequent auto-increments
SELECT setval('departments_id_seq', (SELECT COALESCE(MAX(id), 1) FROM departments));
SELECT setval('teams_id_seq', (SELECT COALESCE(MAX(id), 1) FROM teams));
SELECT setval('designations_id_seq', (SELECT COALESCE(MAX(id), 1) FROM designations));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('employees_id_seq', (SELECT COALESCE(MAX(id), 1) FROM employees));
SELECT setval('kpis_id_seq', (SELECT COALESCE(MAX(id), 1) FROM kpis));
SELECT setval('designation_kpis_id_seq', (SELECT COALESCE(MAX(id), 1) FROM designation_kpis));
SELECT setval('pms_cycles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pms_cycles));
SELECT setval('pms_assignments_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pms_assignments));
SELECT setval('pms_kpis_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pms_kpis));
SELECT setval('employee_reviews_id_seq', (SELECT COALESCE(MAX(id), 1) FROM employee_reviews));
SELECT setval('employee_kpi_ratings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM employee_kpi_ratings));
SELECT setval('manager_reviews_id_seq', (SELECT COALESCE(MAX(id), 1) FROM manager_reviews));
SELECT setval('manager_kpi_ratings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM manager_kpi_ratings));
SELECT setval('hr_reviews_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hr_reviews));
SELECT setval('hr_kpi_ratings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM hr_kpi_ratings));
SELECT setval('final_pms_results_id_seq', (SELECT COALESCE(MAX(id), 1) FROM final_pms_results));
SELECT setval('pms_history_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pms_history));
