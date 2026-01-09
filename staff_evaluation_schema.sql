-- ============================================
-- Staff Evaluation System Database Schema (Final)
-- ============================================

-- Drop tables if exist (in reverse order of dependencies)
DROP TABLE IF EXISTS ase_assign_scores;
DROP TABLE IF EXISTS assign_staff_evaluation_lists;
DROP TABLE IF EXISTS assign_staff_evaluations;
DROP TABLE IF EXISTS evaluation_points;
DROP TABLE IF EXISTS staffs;
DROP TABLE IF EXISTS periods;
DROP TABLE IF EXISTS positions;
DROP TABLE IF EXISTS offices;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS companies;

-- 1. Companies
CREATE TABLE companies (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(100),
    status VARCHAR(3) CHECK (status IN ('YES','NO')) DEFAULT 'YES',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 2. Users
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id BIGINT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    description TEXT,
    user_group VARCHAR(10) CHECK (user_group IN ('ADMIN','STAFF')) NOT NULL,
    status VARCHAR(3) CHECK (status IN ('YES','NO')) DEFAULT 'YES',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- 3. Departments
CREATE TABLE departments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 4. Offices
CREATE TABLE offices (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 5. Positions
CREATE TABLE positions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 6. Periods
CREATE TABLE periods (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id BIGINT NOT NULL, -- Added for consistency
    code VARCHAR(50) NOT NULL UNIQUE,
    from_date TIMESTAMPTZ NOT NULL,
    to_date TIMESTAMPTZ NOT NULL,
    created_by BIGINT, -- Renamed from created_by_user_id to match your Insert
    status VARCHAR(3) CHECK (status IN ('YES','NO')) DEFAULT 'YES',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 7. Staffs
CREATE TABLE staffs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id BIGINT NOT NULL, -- Added
    name VARCHAR(255) NOT NULL,
    sex VARCHAR(10),
    date_of_birth DATE,
    place_of_birth VARCHAR(255),
    current_address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(100),
    leader_id BIGINT,
    department_id BIGINT NOT NULL,
    office_id BIGINT NOT NULL,
    position_id BIGINT NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(3) CHECK (status IN ('YES','NO')) DEFAULT 'YES',
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (leader_id) REFERENCES staffs(id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    FOREIGN KEY (office_id) REFERENCES offices(id) ON DELETE CASCADE,
    FOREIGN KEY (position_id) REFERENCES positions(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 8. Evaluation Points
CREATE TABLE evaluation_points (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_id BIGINT NOT NULL, -- Added
    name VARCHAR(255) NOT NULL,
    score_range_from NUMERIC NOT NULL,
    score_range_to NUMERIC NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 9. Assign Staff Evaluations
CREATE TABLE assign_staff_evaluations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    period_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    assign_by_staff_id BIGINT NOT NULL,
    for_staff_id BIGINT NOT NULL,
    assign_date TIMESTAMPTZ NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (period_id) REFERENCES periods(id) ON DELETE CASCADE,
    FOREIGN KEY (assign_by_staff_id) REFERENCES staffs(id) ON DELETE CASCADE,
    FOREIGN KEY (for_staff_id) REFERENCES staffs(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 10. Assign Staff Evaluation Lists
CREATE TABLE assign_staff_evaluation_lists (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    assign_staff_evaluation_id BIGINT NOT NULL,
    evaluation_staff_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assign_staff_evaluation_id) REFERENCES assign_staff_evaluations(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluation_staff_id) REFERENCES staffs(id) ON DELETE CASCADE,
    UNIQUE (assign_staff_evaluation_id, evaluation_staff_id)
);

-- 11. ASE Assign Scores
CREATE TABLE ase_assign_scores (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    assign_staff_evaluation_list_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    score NUMERIC NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assign_staff_evaluation_list_id) REFERENCES assign_staff_evaluation_lists(id) ON DELETE CASCADE,
    UNIQUE (assign_staff_evaluation_list_id)
);

-- ============================================
-- Corrected Sample Data
-- ============================================

INSERT INTO companies (name, address, phone, email, status)
VALUES (
  'Agent 404',
  'Toul Kork, Phnom Penh',
  '099876543',
  'agent404@gmail.com',
  'YES'
);


-- Admin user
INSERT INTO users (company_id, username, password, description, user_group, status)
VALUES (1, 'admin', 'admin123', 'System Administrator', 'ADMIN', 'YES');

-- Staff users
INSERT INTO users (company_id, username, password, description, user_group, status)
VALUES
(1, 'alice', 'password', 'IT Staff', 'STAFF', 'YES'),
(1, 'bob', 'password', 'HR Staff', 'STAFF', 'YES'),
(1, 'charlie', 'password', 'Sales Staff', 'STAFF', 'YES');


INSERT INTO departments (company_id, name, created_by)
VALUES
(1, 'IT Department', 1),
(1, 'HR Department', 1),
(1, 'Sales Department', 1);


INSERT INTO offices (company_id, name, created_by)
VALUES
(1, 'Head Office', 1),
(1, 'Branch Office 1', 1);


INSERT INTO positions (company_id, name, created_by)
VALUES
(1, 'Manager', 1),
(1, 'Team Leader', 1),
(1, 'Staff', 1);


INSERT INTO periods (
  company_id, code, from_date, to_date, created_by, status
)
VALUES (
  1,
  'PER-2024-12',
  '2024-12-01',
  '2024-12-31',
  1,
  'YES'
);

INSERT INTO staffs (
  company_id, name, sex, date_of_birth, phone, email,
  department_id, office_id, position_id, created_by
)
VALUES
-- Manager
(1, 'Alice Smith', 'F', '1990-05-12', '010111111', 'alice@agent404.com', 1, 1, 1, 1),

-- Team Leader
(1, 'Bob Johnson', 'M', '1988-03-20', '010222222', 'bob@agent404.com', 2, 1, 2, 1),

-- Staff
(1, 'Charlie Brown', 'M', '1995-08-15', '010333333', 'charlie@agent404.com', 3, 2, 3, 1);


INSERT INTO evaluation_points (
  company_id, name, score_range_from, score_range_to, created_by
)
VALUES
(1, 'Work Quality', 1, 5, 1),
(1, 'Teamwork', 1, 5, 1),
(1, 'Discipline', 1, 5, 1),
(1, 'Communication', 1, 5, 1);

INSERT INTO assign_staff_evaluations (
  company_id,
  period_id,
  assign_by_staff_id,
  for_staff_id,
  assign_date,
  description,
  created_by
)
VALUES (
  1,      -- company_id
  1,      -- period_id
  1,      -- assigned by staff
  1,      -- evaluated staff
  NOW(),
  'Monthly evaluation',
  1
);


INSERT INTO assign_staff_evaluation_lists (
  company_id,
  assign_staff_evaluation_id,
  evaluation_staff_id
)
VALUES
(1, 1, 2),  -- Bob evaluates Alice
(1, 1, 3);  -- Charlie evaluates Alice



INSERT INTO ase_assign_scores (
  company_id,
  assign_staff_evaluation_list_id,
  score
)
VALUES (
  1,
  1,
  4.5
);

