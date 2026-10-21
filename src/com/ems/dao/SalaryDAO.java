package com.ems.dao;

import com.ems.exception.AppException;
import com.ems.model.Salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Salary records.
 * Demonstrates:
 *  - Module 3: Implementing GenericDAO
 *  - Module 4: Performing CRUD Operations with JDBC
 */
public class SalaryDAO implements GenericDAO<Salary> {

    @Override
    public boolean add(Salary s) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO salaries (emp_id, month, year, basic_salary, hra, da, deductions, net_salary, payment_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, s.getEmpId());
            stmt.setString(2, s.getMonth());
            stmt.setInt(3, s.getYear());
            stmt.setDouble(4, s.getBasicSalary());
            stmt.setDouble(5, s.getHra());
            stmt.setDouble(6, s.getDa());
            stmt.setDouble(7, s.getDeductions());
            stmt.setDouble(8, s.getNetSalary());
            stmt.setDate(9, s.getPaymentDate());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    s.setSalaryId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new AppException("Failed to record salary: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean update(Salary s) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE salaries SET month = ?, year = ?, basic_salary = ?, hra = ?, da = ?, " +
                     "deductions = ?, net_salary = ?, payment_date = ? WHERE salary_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, s.getMonth());
            stmt.setInt(2, s.getYear());
            stmt.setDouble(3, s.getBasicSalary());
            stmt.setDouble(4, s.getHra());
            stmt.setDouble(5, s.getDa());
            stmt.setDouble(6, s.getDeductions());
            stmt.setDouble(7, s.getNetSalary());
            stmt.setDate(8, s.getPaymentDate());
            stmt.setInt(9, s.getSalaryId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to update salary record: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM salaries WHERE salary_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Failed to delete salary record: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    @Override
    public Salary getById(int id) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT s.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM salaries s " +
                     "JOIN employees e ON s.emp_id = e.emp_id " +
                     "WHERE s.salary_id = ?";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSalary(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve salary by ID: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    @Override
    public List<Salary> getAll() throws AppException {
        List<Salary> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT s.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM salaries s " +
                     "JOIN employees e ON s.emp_id = e.emp_id " +
                     "ORDER BY s.year DESC, s.salary_id DESC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(mapResultSetToSalary(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve salary records: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Retrieves all salary history for a specific employee.
     */
    public List<Salary> getByEmployee(int empId) throws AppException {
        List<Salary> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT s.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM salaries s " +
                     "JOIN employees e ON s.emp_id = e.emp_id " +
                     "WHERE s.emp_id = ? " +
                     "ORDER BY s.year DESC, s.salary_id DESC";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, empId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToSalary(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve employee salary records: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Gets the latest salary record for an employee.
     */
    public Salary getLatestSalaryByEmployee(int empId) throws AppException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT s.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name " +
                     "FROM salaries s " +
                     "JOIN employees e ON s.emp_id = e.emp_id " +
                     "WHERE s.emp_id = ? " +
                     "ORDER BY s.year DESC, s.payment_date DESC, s.salary_id DESC LIMIT 1";

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, empId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSalary(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new AppException("Failed to retrieve latest salary: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    private Salary mapResultSetToSalary(ResultSet rs) throws SQLException {
        return new Salary(
            rs.getInt("salary_id"),
            rs.getInt("emp_id"),
            rs.getString("emp_name"),
            rs.getString("month"),
            rs.getInt("year"),
            rs.getDouble("basic_salary"),
            rs.getDouble("hra"),
            rs.getDouble("da"),
            rs.getDouble("deductions"),
            rs.getDouble("net_salary"),
            rs.getDate("payment_date")
        );
    }
}
