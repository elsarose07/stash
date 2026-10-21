package com.ems.dao;

import com.ems.exception.AppException;
import com.ems.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Department operations.
 * Demonstrates:
 *  - Module 3: Implementing interfaces (GenericDAO)
 *  - Module 4: Performing CRUD Operations with JDBC
 */
public class DepartmentDAO implements GenericDAO<Department> {

    @Override
    public boolean add(Department dept) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO departments (dept_name, description) VALUES (?, ?)";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, dept.getDeptName());
            stmt.setString(2, dept.getDescription());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    dept.setDeptId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new AppException("Failed to add department: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean update(Department dept) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE departments SET dept_name = ?, description = ? WHERE dept_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, dept.getDeptName());
            stmt.setString(2, dept.getDescription());
            stmt.setInt(3, dept.getDeptId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to update department: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM departments WHERE dept_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to delete department: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public Department getById(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT dept_id, dept_name, description FROM departments WHERE dept_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Department(
                    rs.getInt("dept_id"),
                    rs.getString("dept_name"),
                    rs.getString("description")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new AppException("Failed to fetch department by ID: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Department> getAll() throws AppException {
        List<Department> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT dept_id, dept_name, description FROM departments ORDER BY dept_id ASC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(new Department(
                    rs.getInt("dept_id"),
                    rs.getString("dept_name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to fetch department list: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Helper method to get the total number of departments for the Dashboard overview.
     */
    public int getDepartmentCount() throws AppException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM departments";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new AppException("Failed to get department count: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }
}
