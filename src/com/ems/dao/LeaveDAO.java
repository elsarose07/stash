package com.ems.dao;

import com.ems.exception.AppException;
import com.ems.model.LeaveRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Leave Requests.
 * Demonstrates:
 *  - Module 3: Implementing GenericDAO
 *  - Module 4: CRUD operations, parameterized SQL queries
 */
public class LeaveDAO implements GenericDAO<LeaveRequest> {

    @Override
    public boolean add(LeaveRequest req) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO leave_requests (emp_id, start_date, end_date, leave_type, reason, status, applied_on) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, req.getEmpId());
            stmt.setDate(2, req.getStartDate());
            stmt.setDate(3, req.getEndDate());
            stmt.setString(4, req.getLeaveType());
            stmt.setString(5, req.getReason());
            stmt.setString(6, req.getStatus());
            stmt.setDate(7, req.getAppliedOn());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    req.setLeaveId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new AppException("Failed to submit leave request: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean update(LeaveRequest req) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE leave_requests SET start_date = ?, end_date = ?, leave_type = ?, reason = ?, status = ? " +
                     "WHERE leave_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, req.getStartDate());
            stmt.setDate(2, req.getEndDate());
            stmt.setString(3, req.getLeaveType());
            stmt.setString(4, req.getReason());
            stmt.setString(5, req.getStatus());
            stmt.setInt(6, req.getLeaveId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to update leave request: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    /**
     * Updates the status of a leave request (e.g. Approved or Rejected).
     */
    public boolean updateStatus(int leaveId, String status) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE leave_requests SET status = ? WHERE leave_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, leaveId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to update leave status: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM leave_requests WHERE leave_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to delete leave request: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public LeaveRequest getById(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT l.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM leave_requests l " +
                     "JOIN employees e ON l.emp_id = e.emp_id " +
                     "WHERE l.leave_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLeave(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve leave request: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    @Override
    public List<LeaveRequest> getAll() throws AppException {
        List<LeaveRequest> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT l.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM leave_requests l " +
                     "JOIN employees e ON l.emp_id = e.emp_id " +
                     "ORDER BY l.applied_on DESC, l.leave_id DESC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(mapResultSetToLeave(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve all leave requests: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Retrieves leave requests for a specific employee (Employee Dashboard).
     */
    public List<LeaveRequest> getByEmployee(int empId) throws AppException {
        List<LeaveRequest> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT l.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM leave_requests l " +
                     "JOIN employees e ON l.emp_id = e.emp_id " +
                     "WHERE l.emp_id = ? " +
                     "ORDER BY l.applied_on DESC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, empId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToLeave(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve employee leave requests: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Retrieves the count of pending leave requests for Dashboard overview.
     */
    public int getPendingCount() throws AppException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE status = 'Pending'";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new AppException("Failed to count pending leaves: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    private LeaveRequest mapResultSetToLeave(ResultSet rs) throws SQLException {
        return new LeaveRequest(
            rs.getInt("leave_id"),
            rs.getInt("emp_id"),
            rs.getString("emp_name"),
            rs.getDate("start_date"),
            rs.getDate("end_date"),
            rs.getString("leave_type"),
            rs.getString("reason"),
            rs.getString("status"),
            rs.getDate("applied_on")
        );
    }
}
