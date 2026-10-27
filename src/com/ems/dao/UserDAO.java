package com.ems.dao;

import com.ems.exception.AppException;
import com.ems.exception.AuthenticationException;
import com.ems.model.Admin;
import com.ems.model.Employee;
import com.ems.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User authentication and credential management.
 * Demonstrates:
 *  - Module 4: Executing basic SQL Queries, Working with ResultSet, PreparedStatement
 *  - Module 3: Custom Exceptions (AuthenticationException, AppException)
 *  - Module 2: Dynamic Method Dispatch (returning User base reference for Admin/Employee)
 */
public class UserDAO {

    /**
     * Authenticates user credentials against the database.
     * @param username The entered username
     * @param password The entered password
     * @param expectedRole The expected role ("ADMIN" or "EMPLOYEE")
     * @return Authenticated User object (Admin or Employee)
     * @throws AuthenticationException If credentials or role do not match
     * @throws AppException If database error occurs
     */
    public User authenticate(String username, String password, String expectedRole) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT u.user_id, u.username, u.password, u.role, u.emp_id, " +
                     "       e.first_name, e.last_name, e.email, e.phone, e.dept_id, " +
                     "       e.designation, e.basic_salary, e.join_date, d.dept_name " +
                     "FROM users u " +
                     "LEFT JOIN employees e ON u.emp_id = e.emp_id " +
                     "LEFT JOIN departments d ON e.dept_id = d.dept_id " +
                     "WHERE u.username = ? AND u.password = ? AND u.role = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, expectedRole);

            rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String role = rs.getString("role");
                int rawEmpId = rs.getInt("emp_id");
                Integer empId = rs.wasNull() ? null : rawEmpId;

                if (User.ROLE_ADMIN.equalsIgnoreCase(role)) {
                    // Polymorphic instantiation of Admin
                    return new Admin(userId, username, password);
                } else {
                    // Polymorphic instantiation of Employee
                    if (empId == null) {
                        throw new AuthenticationException("Employee account is not linked to an employee profile.");
                    }
                    Employee emp = new Employee(
                        userId,
                        username,
                        password,
                        empId,
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
                    return emp;
                }
            } else {
                throw new AuthenticationException("Invalid username, password, or role selection.");
            }
        } catch (SQLException e) {
            throw new AppException("Database error during authentication: " + e.getMessage(), e);
        } finally {
            // Module 3: finally block for resource cleanup
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Updates the password for a specific user.
     */
    public boolean changePassword(int userId, String newPassword) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to update password: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    /**
     * Resets password for an employee verifying username and registered email.
     */
    public boolean resetForgottenPassword(String username, String email, String newPassword) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String verifySql = "SELECT u.user_id FROM users u " +
                           "JOIN employees e ON u.emp_id = e.emp_id " +
                           "WHERE u.username = ? AND LOWER(e.email) = LOWER(?) AND u.role = 'EMPLOYEE'";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(verifySql);
            stmt.setString(1, username.trim());
            stmt.setString(2, email.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                DBConnection.close(null, stmt, rs);
                return changePassword(userId, newPassword);
            } else {
                throw new AppException("No employee account found matching this username and registered email.");
            }
        } catch (SQLException e) {
            throw new AppException("Failed to reset password: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Retrieves all user accounts (used in Admin Settings).
     */
    public List<User> getAllUsers() throws AppException {
        List<User> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT user_id, username, password, role, emp_id FROM users ORDER BY user_id ASC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String uName = rs.getString("username");
                String pwd = rs.getString("password");
                String role = rs.getString("role");
                int rawEId = rs.getInt("emp_id");
                Integer eId = rs.wasNull() ? null : rawEId;

                if (User.ROLE_ADMIN.equalsIgnoreCase(role)) {
                    list.add(new Admin(id, uName, pwd));
                } else {
                    Employee emp = new Employee();
                    emp.setUserId(id);
                    emp.setUsername(uName);
                    emp.setPassword(pwd);
                    emp.setRole(role);
                    if (eId != null) emp.setEmpId(eId);
                    list.add(emp);
                }
            }
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve user accounts: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Creates a login account for an employee.
     */
    public boolean createEmployeeAccount(int empId, String username, String password) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO users (username, password, role, emp_id) VALUES (?, ?, 'EMPLOYEE', ?)";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, empId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to create employee user account: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }
}
