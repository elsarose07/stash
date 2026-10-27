package com.ems.dao;

import com.ems.exception.AppException;
import com.ems.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Employee operations.
 * Demonstrates:
 *  - Module 3: Implementing GenericDAO interface
 *  - Module 2: Method Overloading (search methods)
 *  - Module 4: Performing CRUD Operations with JDBC, PreparedStatement
 */
public class EmployeeDAO implements GenericDAO<Employee> {

    @Override
    public boolean add(Employee emp) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO employees (first_name, last_name, email, phone, dept_id, designation, basic_salary, join_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, emp.getFirstName());
            stmt.setString(2, emp.getLastName());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getPhone());
            if (emp.getDeptId() > 0) {
                stmt.setInt(5, emp.getDeptId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setString(6, emp.getDesignation());
            stmt.setDouble(7, emp.getBasicSalary());
            stmt.setDate(8, emp.getJoinDate());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    emp.setEmpId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new AppException("Failed to add employee: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean update(Employee emp) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, phone = ?, " +
                     "dept_id = ?, designation = ?, basic_salary = ?, join_date = ? WHERE emp_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, emp.getFirstName());
            stmt.setString(2, emp.getLastName());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getPhone());
            if (emp.getDeptId() > 0) {
                stmt.setInt(5, emp.getDeptId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setString(6, emp.getDesignation());
            stmt.setDouble(7, emp.getBasicSalary());
            stmt.setDate(8, emp.getJoinDate());
            stmt.setInt(9, emp.getEmpId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to update employee: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM employees WHERE emp_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to delete employee: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public Employee getById(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT e.*, d.dept_name, u.user_id, u.username, u.password FROM employees e " +
                     "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                     "LEFT JOIN users u ON e.emp_id = u.emp_id " +
                     "WHERE e.emp_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new AppException("Failed to fetch employee by ID: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Employee> getAll() throws AppException {
        List<Employee> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT e.*, d.dept_name FROM employees e " +
                     "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                     "ORDER BY e.emp_id ASC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to fetch all employees: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Search employees by a text keyword matching name, email, or designation.
     * Demonstrates: Module 2: Method Overloading (Polymorphism)
     */
    public List<Employee> search(String keyword) throws AppException {
        List<Employee> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT e.*, d.dept_name FROM employees e " +
                     "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                     "WHERE e.first_name LIKE ? OR e.last_name LIKE ? OR e.email LIKE ? OR e.designation LIKE ? " +
                     "ORDER BY e.emp_id ASC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Error during employee search: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Overloaded search method filtering by department and keyword.
     * Demonstrates: Module 2: Method Overloading
     */
    public List<Employee> search(int deptId, String keyword) throws AppException {
        List<Employee> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT e.*, d.dept_name FROM employees e " +
                     "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                     "WHERE e.dept_id = ? AND (e.first_name LIKE ? OR e.last_name LIKE ? OR e.email LIKE ?) " +
                     "ORDER BY e.emp_id ASC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            String pattern = "%" + keyword + "%";
            stmt.setInt(1, deptId);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Error during department employee search: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Assigns an employee to a specific department.
     */
    public boolean assignDepartment(int empId, int deptId) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE employees SET dept_id = ? WHERE emp_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, deptId);
            stmt.setInt(2, empId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to assign department: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    /**
     * Returns total number of employees for dashboard statistics.
     */
    public int getEmployeeCount() throws AppException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM employees";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new AppException("Failed to get employee count: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    // Helper method to map ResultSet to Employee object
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee(
            rs.getInt("emp_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getInt("dept_id"),
            rs.getString("designation"),
            rs.getDouble("basic_salary"),
            rs.getDate("join_date")
        );
        emp.setDeptName(rs.getString("dept_name"));

        try {
            int uid = rs.getInt("user_id");
            if (!rs.wasNull()) {
                emp.setUserId(uid);
            }
            emp.setUsername(rs.getString("username"));
            emp.setPassword(rs.getString("password"));
        } catch (SQLException ignored) {
            // Columns not part of this specific query projection
        }

        return emp;
    }
}
