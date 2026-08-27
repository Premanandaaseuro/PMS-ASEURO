-- ============================================================
-- PERFORMANCE MANAGEMENT SYSTEM (PMS)
-- SHARED DEVELOPMENT DATABASE SCHEMA
-- Roles: HR, MANAGER, EMPLOYEE
-- No ADMIN role
--
-- Purpose:
-- 1. One shared schema for all developers.
-- 2. UI attributes are represented directly in the database.
-- 3. Core workflow:
--    HR Setup -> Employee Self Review -> Manager Review
--    -> HR Review/Final Marks -> Publish Final Result -> History
-- 4. PostgreSQL
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ============================================================
-- ENUMS
-- ============================================================

DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('HR', 'MANAGER', 'EMPLOYEE');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    CREATE TYPE record_status AS ENUM ('ACTIVE', 'INACTIVE');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    CREATE TYPE pms_status AS ENUM (
        'PMS_NOT_STARTED',
        'PMS_STARTED',
        'SELF_ASSESSMENT_DRAFT',
        'SELF_ASSESSMENT_SUBMITTED',
        'MANAGER_REVIEW_PENDING',
        'MANAGER_REVIEW_SUBMITTED',
        'HR_REVIEW_PENDING',
        'HR_REVIEW_COMPLETED',
        'RATING_AND_POINTS_CALCULATED',
        'FINAL_ANALYSIS',
        'FINAL_RESULT_PUBLISHED',
        'COMPLETED'
    );
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- ============================================================
-- DEPARTMENTS
-- ============================================================

CREATE TABLE IF NOT EXISTS departments (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL UNIQUE,
    description     TEXT,
    status          record_status NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TEAMS
-- ============================================================

CREATE TABLE IF NOT EXISTS teams (
    id              BIGSERIAL PRIMARY KEY,
    department_id   BIGINT NOT NULL REFERENCES departments(id),
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    status          record_status NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (department_id, name)
);

-- ============================================================
-- DESIGNATIONS
-- ============================================================

CREATE TABLE IF NOT EXISTS designations (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL UNIQUE,
    description     TEXT,
    status          record_status NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- USERS
-- Shared login table.
-- Only HR / MANAGER / EMPLOYEE roles.
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id                  BIGSERIAL PRIMARY KEY,
    username            VARCHAR(100) UNIQUE,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    role                user_role NOT NULL,
    status              record_status NOT NULL DEFAULT 'ACTIVE',
    failed_attempts     INTEGER NOT NULL DEFAULT 0,
    locked_until        TIMESTAMPTZ,
    last_login_at       TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- EMPLOYEES
--
-- UI fields:
-- Employee Name
-- Employee ID
-- Email
-- Department
-- Team
-- Designation
-- Reporting Manager
-- Joining Date
-- Status
-- ============================================================

CREATE TABLE IF NOT EXISTS employees (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT UNIQUE REFERENCES users(id),
    employee_code       VARCHAR(50) NOT NULL UNIQUE,
    full_name           VARCHAR(200) NOT NULL,
    email               VARCHAR(255) NOT NULL UNIQUE,
    department_id       BIGINT NOT NULL REFERENCES departments(id),
    team_id             BIGINT REFERENCES teams(id),
    designation_id      BIGINT NOT NULL REFERENCES designations(id),
    manager_id          BIGINT REFERENCES employees(id),
    joining_date        DATE NOT NULL,
    status              record_status NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- A manager is represented by an employee record with users.role = MANAGER.
-- This avoids duplicate manager/employee master data.

-- ============================================================
-- MANAGER VALIDATION
-- ============================================================

CREATE OR REPLACE FUNCTION validate_employee_manager()
RETURNS TRIGGER AS $$
DECLARE
    manager_role user_role;
BEGIN
    IF NEW.manager_id IS NOT NULL THEN
        SELECT u.role
        INTO manager_role
        FROM employees e
        JOIN users u ON u.id = e.user_id
        WHERE e.id = NEW.manager_id;

        IF manager_role IS DISTINCT FROM 'MANAGER' THEN
            RAISE EXCEPTION 'Reporting Manager must be a user with MANAGER role';
        END IF;
    END IF;

    IF NEW.manager_id = NEW.id THEN
        RAISE EXCEPTION 'Employee cannot report to self';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_validate_employee_manager ON employees;

CREATE TRIGGER trg_validate_employee_manager
BEFORE INSERT OR UPDATE OF manager_id, user_id ON employees
FOR EACH ROW EXECUTE FUNCTION validate_employee_manager();

-- ============================================================
-- KPI MASTER
--
-- UI:
-- KPI Name
-- Measurement Criteria
-- Self Rating
-- Manager Rating
-- Weightage
-- Status
-- ============================================================

CREATE TABLE IF NOT EXISTS kpis (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL,
    description             TEXT,
    measurement_criteria    TEXT NOT NULL,
    status                  record_status NOT NULL DEFAULT 'ACTIVE',
    created_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name)
);

-- ============================================================
-- DESIGNATION -> KPI MAPPING
--
-- Allows N KPIs.
-- Total weightage for one designation must never exceed 100%.
-- ============================================================

CREATE TABLE IF NOT EXISTS designation_kpis (
    id                  BIGSERIAL PRIMARY KEY,
    designation_id      BIGINT NOT NULL REFERENCES designations(id) ON DELETE CASCADE,
    kpi_id              BIGINT NOT NULL REFERENCES kpis(id) ON DELETE RESTRICT,
    weightage           NUMERIC(5,2) NOT NULL CHECK (weightage > 0 AND weightage <= 100),
    status              record_status NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (designation_id, kpi_id)
);

CREATE OR REPLACE FUNCTION validate_kpi_total_weightage()
RETURNS TRIGGER AS $$
DECLARE
    total_weightage NUMERIC(7,2);
BEGIN
    SELECT COALESCE(SUM(weightage), 0)
    INTO total_weightage
    FROM designation_kpis
    WHERE designation_id = NEW.designation_id
      AND id <> COALESCE(NEW.id, -1)
      AND status = 'ACTIVE';

    total_weightage := total_weightage + NEW.weightage;

    IF total_weightage > 100 THEN
        RAISE EXCEPTION
            'Total KPI weightage for designation % cannot exceed 100%%. Current total: %',
            NEW.designation_id, total_weightage;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_validate_kpi_total_weightage ON designation_kpis;

CREATE TRIGGER trg_validate_kpi_total_weightage
BEFORE INSERT OR UPDATE OF designation_id, weightage, status
ON designation_kpis
FOR EACH ROW EXECUTE FUNCTION validate_kpi_total_weightage();

-- ============================================================
-- PMS CYCLES
--
-- UI:
-- Month
-- Year
-- Start Date
-- End Date
-- Status
-- ============================================================

CREATE TABLE IF NOT EXISTS pms_cycles (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    month           INTEGER NOT NULL CHECK (month BETWEEN 1 AND 12),
    year            INTEGER NOT NULL CHECK (year BETWEEN 2000 AND 2100),
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          record_status NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (month, year),
    CHECK (end_date >= start_date)
);

-- ============================================================
-- PMS ASSIGNMENT
--
-- Central table connecting:
-- Employee + Manager + PMS Month + Designation
-- ============================================================

CREATE TABLE IF NOT EXISTS pms_assignments (
    id                  BIGSERIAL PRIMARY KEY,
    employee_id         BIGINT NOT NULL REFERENCES employees(id),
    manager_id          BIGINT NOT NULL REFERENCES employees(id),
    pms_cycle_id        BIGINT NOT NULL REFERENCES pms_cycles(id),
    designation_id      BIGINT NOT NULL REFERENCES designations(id),
    status              pms_status NOT NULL DEFAULT 'PMS_NOT_STARTED',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_id, pms_cycle_id)
);

-- ============================================================
-- PMS KPI SNAPSHOT
--
-- IMPORTANT:
-- Copy KPI name, measurement and weightage into the PMS cycle.
-- Historical/finalized PMS must not change if the KPI master changes.
-- ============================================================

CREATE TABLE IF NOT EXISTS pms_kpis (
    id                      BIGSERIAL PRIMARY KEY,
    pms_assignment_id       BIGINT NOT NULL REFERENCES pms_assignments(id) ON DELETE CASCADE,
    kpi_id                  BIGINT NOT NULL REFERENCES kpis(id),
    kpi_name                VARCHAR(255) NOT NULL,
    measurement_criteria    TEXT NOT NULL,
    weightage               NUMERIC(5,2) NOT NULL CHECK (weightage > 0 AND weightage <= 100),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (pms_assignment_id, kpi_id)
);

-- ============================================================
-- EMPLOYEE SELF REVIEW
-- ============================================================

CREATE TABLE IF NOT EXISTS employee_reviews (
    id                  BIGSERIAL PRIMARY KEY,
    pms_assignment_id   BIGINT NOT NULL UNIQUE REFERENCES pms_assignments(id),
    employee_id         BIGINT NOT NULL REFERENCES employees(id),
    status              VARCHAR(40) NOT NULL DEFAULT 'DRAFT',
    comments            TEXT,
    submitted_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- EMPLOYEE KPI RATINGS
--
-- UI:
-- Self Rating
-- Employee Comments
-- ============================================================

CREATE TABLE IF NOT EXISTS employee_kpi_ratings (
    id                  BIGSERIAL PRIMARY KEY,
    employee_review_id  BIGINT NOT NULL REFERENCES employee_reviews(id) ON DELETE CASCADE,
    pms_kpi_id          BIGINT NOT NULL REFERENCES pms_kpis(id),
    self_rating         NUMERIC(5,2) CHECK (self_rating >= 0 AND self_rating <= 5),
    comments            TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_review_id, pms_kpi_id)
);

-- ============================================================
-- MANAGER REVIEW
-- ============================================================

CREATE TABLE IF NOT EXISTS manager_reviews (
    id                  BIGSERIAL PRIMARY KEY,
    pms_assignment_id   BIGINT NOT NULL UNIQUE REFERENCES pms_assignments(id),
    manager_id          BIGINT NOT NULL REFERENCES employees(id),
    status              VARCHAR(40) NOT NULL DEFAULT 'DRAFT',
    comments            TEXT,
    submitted_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- MANAGER KPI RATINGS
--
-- UI:
-- Employee Self Rating -> READ ONLY
-- Manager Rating       -> EDITABLE BY MANAGER
-- Manager Comments
-- ============================================================

CREATE TABLE IF NOT EXISTS manager_kpi_ratings (
    id                  BIGSERIAL PRIMARY KEY,
    manager_review_id   BIGINT NOT NULL REFERENCES manager_reviews(id) ON DELETE CASCADE,
    pms_kpi_id          BIGINT NOT NULL REFERENCES pms_kpis(id),
    manager_rating      NUMERIC(5,2) CHECK (manager_rating >= 0 AND manager_rating <= 5),
    comments            TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (manager_review_id, pms_kpi_id)
);

-- ============================================================
-- HR REVIEW
--
-- UI:
-- Employee Search
-- Employee Dropdown
-- Employee details
-- KPI
-- Self Rating (READ ONLY)
-- Manager Rating (READ ONLY)
-- HR Rating / Final Marks (EDITABLE)
-- Comments
-- Publish Final Result
-- ============================================================

CREATE TABLE IF NOT EXISTS hr_reviews (
    id                  BIGSERIAL PRIMARY KEY,
    pms_assignment_id   BIGINT NOT NULL UNIQUE REFERENCES pms_assignments(id),
    hr_user_id          BIGINT NOT NULL REFERENCES users(id),
    status              VARCHAR(40) NOT NULL DEFAULT 'DRAFT',
    review_comments     TEXT,
    reviewed_at         TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- HR KPI RATINGS / FINAL MARKS
-- ============================================================

CREATE TABLE IF NOT EXISTS hr_kpi_ratings (
    id                      BIGSERIAL PRIMARY KEY,
    hr_review_id            BIGINT NOT NULL REFERENCES hr_reviews(id) ON DELETE CASCADE,
    pms_kpi_id              BIGINT NOT NULL REFERENCES pms_kpis(id),
    hr_rating               NUMERIC(5,2) CHECK (hr_rating >= 0 AND hr_rating <= 5),
    final_weighted_score    NUMERIC(10,4),
    comments                TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (hr_review_id, pms_kpi_id)
);

-- ============================================================
-- FINAL PMS RESULT
--
-- Created only when HR publishes the final result.
-- ============================================================

CREATE TABLE IF NOT EXISTS final_pms_results (
    id                  BIGSERIAL PRIMARY KEY,
    pms_assignment_id   BIGINT NOT NULL UNIQUE REFERENCES pms_assignments(id),
    overall_score       NUMERIC(10,4) NOT NULL,
    rating_category     VARCHAR(100),
    hr_comments         TEXT,
    published_by        BIGINT NOT NULL REFERENCES users(id),
    published_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status              VARCHAR(30) NOT NULL DEFAULT 'PUBLISHED',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- PMS HISTORY
--
-- Finalized results remain available after logout/restart.
-- ============================================================

CREATE TABLE IF NOT EXISTS pms_history (
    id                  BIGSERIAL PRIMARY KEY,
    pms_assignment_id   BIGINT NOT NULL REFERENCES pms_assignments(id),
    employee_id         BIGINT NOT NULL REFERENCES employees(id),
    manager_id          BIGINT NOT NULL REFERENCES employees(id),
    pms_cycle_id        BIGINT NOT NULL REFERENCES pms_cycles(id),
    final_result_id     BIGINT NOT NULL REFERENCES final_pms_results(id),
    finalized_at        TIMESTAMPTZ NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (pms_assignment_id)
);

-- ============================================================
-- AUDIT LOG
-- ============================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT REFERENCES users(id),
    action              VARCHAR(100) NOT NULL,
    entity_type         VARCHAR(100) NOT NULL,
    entity_id           BIGINT,
    old_value           JSONB,
    new_value           JSONB,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- REPORTING VIEWS
-- These views are shared by HR Individual / Manager / Team reports.
-- ============================================================

CREATE OR REPLACE VIEW v_pms_report_detail AS
SELECT
    pa.id AS pms_assignment_id,
    e.employee_code,
    e.full_name AS employee_name,
    e.email AS employee_email,
    d.name AS department,
    t.name AS team,
    des.name AS designation,
    m.full_name AS manager_name,
    pc.name AS pms_cycle,
    pc.month,
    pc.year,
    pk.kpi_name,
    pk.measurement_criteria,
    pk.weightage,
    ekr.self_rating,
    mkr.manager_rating,
    hkr.hr_rating,
    hkr.final_weighted_score,
    fpr.overall_score,
    fpr.rating_category,
    pa.status AS pms_status,
    fpr.published_at
FROM pms_assignments pa
JOIN employees e ON e.id = pa.employee_id
JOIN employees m ON m.id = pa.manager_id
JOIN departments d ON d.id = e.department_id
LEFT JOIN teams t ON t.id = e.team_id
JOIN designations des ON des.id = pa.designation_id
JOIN pms_cycles pc ON pc.id = pa.pms_cycle_id
JOIN pms_kpis pk ON pk.pms_assignment_id = pa.id
LEFT JOIN employee_reviews er ON er.pms_assignment_id = pa.id
LEFT JOIN employee_kpi_ratings ekr
    ON ekr.employee_review_id = er.id
   AND ekr.pms_kpi_id = pk.id
LEFT JOIN manager_reviews mr ON mr.pms_assignment_id = pa.id
LEFT JOIN manager_kpi_ratings mkr
    ON mkr.manager_review_id = mr.id
   AND mkr.pms_kpi_id = pk.id
LEFT JOIN hr_reviews hr ON hr.pms_assignment_id = pa.id
LEFT JOIN hr_kpi_ratings hkr
    ON hkr.hr_review_id = hr.id
   AND hkr.pms_kpi_id = pk.id
LEFT JOIN final_pms_results fpr
    ON fpr.pms_assignment_id = pa.id;

-- ============================================================
-- HR REPORT VIEWS
-- ============================================================

CREATE OR REPLACE VIEW v_hr_individual_report AS
SELECT *
FROM v_pms_report_detail;

CREATE OR REPLACE VIEW v_hr_manager_report AS
SELECT *
FROM v_pms_report_detail;

CREATE OR REPLACE VIEW v_hr_team_report AS
SELECT *
FROM v_pms_report_detail;

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_employees_manager
    ON employees(manager_id);

CREATE INDEX IF NOT EXISTS idx_employees_department
    ON employees(department_id);

CREATE INDEX IF NOT EXISTS idx_employees_team
    ON employees(team_id);

CREATE INDEX IF NOT EXISTS idx_employees_designation
    ON employees(designation_id);

CREATE INDEX IF NOT EXISTS idx_pms_assignment_employee
    ON pms_assignments(employee_id);

CREATE INDEX IF NOT EXISTS idx_pms_assignment_manager
    ON pms_assignments(manager_id);

CREATE INDEX IF NOT EXISTS idx_pms_assignment_cycle
    ON pms_assignments(pms_cycle_id);

CREATE INDEX IF NOT EXISTS idx_pms_status
    ON pms_assignments(status);

CREATE INDEX IF NOT EXISTS idx_pms_history_employee
    ON pms_history(employee_id);

-- ============================================================
-- UI -> SCHEMA ATTRIBUTE CHECK
--
-- LOGIN
-- email              -> users.email
-- password           -> users.password_hash
-- role               -> users.role
-- failed attempts    -> users.failed_attempts
-- lock               -> users.locked_until
--
-- HR ADD EMPLOYEE
-- Employee Name      -> employees.full_name
-- Employee ID        -> employees.employee_code
-- Email              -> employees.email
-- Department         -> employees.department_id
-- Team               -> employees.team_id
-- Designation        -> employees.designation_id
-- Reporting Manager  -> employees.manager_id
-- Joining Date       -> employees.joining_date
-- Status             -> employees.status
--
-- KPI
-- KPI                 -> kpis.name
-- Measurement         -> kpis.measurement_criteria
-- Weightage           -> designation_kpis.weightage
--
-- EMPLOYEE REVIEW
-- Self Rating         -> employee_kpi_ratings.self_rating
-- Comments            -> employee_kpi_ratings.comments
--
-- MANAGER REVIEW
-- Manager Rating      -> manager_kpi_ratings.manager_rating
-- Comments            -> manager_kpi_ratings.comments
--
-- HR REVIEW
-- HR Rating / Marks   -> hr_kpi_ratings.hr_rating
-- Final Score         -> final_pms_results.overall_score
-- Category            -> final_pms_results.rating_category
-- Publish Date        -> final_pms_results.published_at
--
-- REPORTS
-- Individual Employee -> v_hr_individual_report
-- Manager Report      -> v_hr_manager_report
-- Team Report         -> v_hr_team_report
--
-- ============================================================
