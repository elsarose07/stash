package com.ems.dao;

import com.ems.exception.AppException;
import com.ems.model.Attendance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Attendance tracking.
 * Demonstrates:
 *  - Module 3: Implementing GenericDAO
 *  - Module 4: Performing CRUD Operations with JDBC
 */
public class AttendanceDAO implements GenericDAO<Attendance> {

    @Override
    public boolean add(Attendance att) throws AppException {
        return markAttendance(att);
    }

    /**
     * Marks or updates attendance for an employee on a specific date.
     * Uses ON DUPLICATE KEY UPDATE so marking attendance multiple times updates correctly.
     */
    public boolean markAttendance(Attendance att) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO attendance (emp_id, date, status, notes) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status), notes = VALUES(notes)";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, att.getEmpId());
            stmt.setDate(2, att.getDate());
            stmt.setString(3, att.getStatus());
            stmt.setString(4, att.getNotes());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to record attendance: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean update(Attendance att) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE attendance SET status = ?, notes = ? WHERE attendance_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, att.getStatus());
            stmt.setString(2, att.getNotes());
            stmt.setInt(3, att.getAttendanceId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to update attendance record: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to delete attendance record: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public Attendance getById(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT a.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM attendance a " +
                     "JOIN employees e ON a.emp_id = e.emp_id " +
                     "WHERE a.attendance_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAttendance(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve attendance by ID: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Attendance> getAll() throws AppException {
        List<Attendance> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT a.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM attendance a " +
                     "JOIN employees e ON a.emp_id = e.emp_id " +
                     "ORDER BY a.date DESC, a.attendance_id DESC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(mapResultSetToAttendance(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve all attendance records: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Retrieves attendance history for a single employee (Employee Dashboard).
     */
    public List<Attendance> getByEmployee(int empId) throws AppException {
        List<Attendance> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT a.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM attendance a " +
                     "JOIN employees e ON a.emp_id = e.emp_id " +
                     "WHERE a.emp_id = ? " +
                     "ORDER BY a.date DESC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, empId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToAttendance(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve employee attendance history: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Returns total count of employees present today for Dashboard overview.
     */
    public int getTodayAttendanceCount() throws AppException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM attendance WHERE date = CURDATE() AND status = 'Present'";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new AppException("Failed to fetch today's attendance count: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Attendance att = new Attendance(
            rs.getInt("attendance_id"),
            rs.getInt("emp_id"),
            rs.getString("emp_name"),
            rs.getDate("date"),
            rs.getString("status"),
            rs.getString("notes")
        );
        return att;
    }
}
