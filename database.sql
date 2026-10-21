-- ============================================================
-- Database Schema for Employee Management System (EMS)
-- Course: Object Oriented Programming (24SJPBCST304)
-- ============================================================

CREATE DATABASE IF NOT EXISTS ems_db;
USE ems_db;

-- 1. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 2. Employees Table
CREATE TABLE IF NOT EXISTS employees (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    dept_id INT,
    designation VARCHAR(50) NOT NULL,
    basic_salary DOUBLE NOT NULL DEFAULT 0.0,
    join_date DATE NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE SET NULL
);

-- 3. Users Table (Authentication for Admin and Employee)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ADMIN' or 'EMPLOYEE'
    emp_id INT NULL,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE
);

-- 4. Attendance Table
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT NOT NULL,
    date DATE NOT NULL,
    status VARCHAR(20) NOT NULL, -- 'Present', 'Absent', 'Half-Day', 'On-Leave'
    notes VARCHAR(255),
    UNIQUE KEY uq_emp_date (emp_id, date),
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE
);

-- 5. Leave Requests Table
CREATE TABLE IF NOT EXISTS leave_requests (
    leave_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    leave_type VARCHAR(50) NOT NULL, -- 'Casual Leave', 'Sick Leave', 'Annual Leave'
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending', -- 'Pending', 'Approved', 'Rejected'
    applied_on DATE NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE
);

-- 6. Salaries Table
CREATE TABLE IF NOT EXISTS salaries (
    salary_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT NOT NULL,
    month VARCHAR(20) NOT NULL,
    year INT NOT NULL,
    basic_salary DOUBLE NOT NULL,
    hra DOUBLE NOT NULL DEFAULT 0.0,
    da DOUBLE NOT NULL DEFAULT 0.0,
    deductions DOUBLE NOT NULL DEFAULT 0.0,
    net_salary DOUBLE NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE
);

-- ============================================================
-- Sample Initial Data (Seed Data)
-- ============================================================

-- Insert Departments
INSERT IGNORE INTO departments (dept_id, dept_name, description) VALUES
(1, 'Information Technology', 'Software development and IT infrastructure'),
(2, 'Human Resources', 'Recruitment, employee relations, and policy'),
(3, 'Finance & Accounts', 'Financial planning, payroll, and bookkeeping'),
(4, 'Marketing & Sales', 'Brand outreach, advertising, and client acquisition');

-- Insert Employees
INSERT IGNORE INTO employees (emp_id, first_name, last_name, email, phone, dept_id, designation, basic_salary, join_date) VALUES
(1, 'John', 'Doe', 'john.doe@ems.com', '9876543210', 1, 'Senior Software Engineer', 65000.0, '2023-01-15'),
(2, 'Jane', 'Smith', 'jane.smith@ems.com', '9876543211', 2, 'HR Executive', 45000.0, '2023-03-01'),
(3, 'Robert', 'Brown', 'robert.brown@ems.com', '9876543212', 3, 'Financial Analyst', 52000.0, '2023-06-10');

-- Insert Users (Admins and Employees)
-- Admin Account: admin / admin123
-- Employee Accounts: john / john123, jane / jane123, robert / robert123
INSERT IGNORE INTO users (user_id, username, password, role, emp_id) VALUES
(1, 'admin', 'admin123', 'ADMIN', NULL),
(2, 'john', 'john123', 'EMPLOYEE', 1),
(3, 'jane', 'jane123', 'EMPLOYEE', 2),
(4, 'robert', 'robert123', 'EMPLOYEE', 3);

-- Insert Sample Attendance
INSERT IGNORE INTO attendance (attendance_id, emp_id, date, status, notes) VALUES
(1, 1, CURDATE(), 'Present', 'On time'),
(2, 2, CURDATE(), 'Present', 'On time'),
(3, 3, CURDATE(), 'Absent', 'Sick leave claimed');

-- Insert Sample Leave Requests
INSERT IGNORE INTO leave_requests (leave_id, emp_id, start_date, end_date, leave_type, reason, status, applied_on) VALUES
(1, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Casual Leave', 'Family function', 'Pending', CURDATE()),
(2, 2, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY), 'Sick Leave', 'Flu fever', 'Approved', DATE_SUB(CURDATE(), INTERVAL 12 DAY)),
(3, 3, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'Sick Leave', 'Migraine', 'Pending', CURDATE());

-- Insert Sample Salaries
INSERT IGNORE INTO salaries (salary_id, emp_id, month, year, basic_salary, hra, da, deductions, net_salary, payment_date) VALUES
(1, 1, 'September', 2026, 65000.0, 13000.0, 6500.0, 4500.0, 80000.0, '2026-09-30'),
(2, 2, 'September', 2026, 45000.0, 9000.0, 4500.0, 3000.0, 55500.0, '2026-09-30'),
(3, 3, 'September', 2026, 52000.0, 10400.0, 5200.0, 3600.0, 64000.0, '2026-09-30');
