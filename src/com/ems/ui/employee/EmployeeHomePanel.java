package com.ems.ui.employee;

import com.ems.dao.AttendanceDAO;
import com.ems.dao.LeaveDAO;
import com.ems.dao.SalaryDAO;
import com.ems.exception.AppException;
import com.ems.model.Attendance;
import com.ems.model.Employee;
import com.ems.model.LeaveRequest;
import com.ems.model.Salary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Simple MVP Employee Dashboard Overview Panel.
 */
public class EmployeeHomePanel extends JPanel {

    private Employee employee;
    private JLabel welcomeLabel;
    private JLabel attendanceStatLabel;
    private JLabel leaveStatLabel;
    private JLabel salaryStatLabel;

    private AttendanceDAO attendanceDAO;
    private LeaveDAO leaveDAO;
    private SalaryDAO salaryDAO;

    public EmployeeHomePanel(Employee employee) {
        this.employee = employee;
        this.attendanceDAO = new AttendanceDAO();
        this.leaveDAO = new LeaveDAO();
        this.salaryDAO = new SalaryDAO();

        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Welcome Header
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        welcomeLabel = new JLabel("Welcome, " + employee.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel infoLabel = new JLabel("Employee ID: " + employee.getEmpId() + " | Department: " + employee.getDeptName() + " | Designation: " + employee.getDesignation());

        topPanel.add(welcomeLabel);
        topPanel.add(infoLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center Summary Cards
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JPanel attBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        attBox.setBorder(BorderFactory.createTitledBorder("Attendance Summary"));
        attendanceStatLabel = new JLabel("Loading...");
        attendanceStatLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        attBox.add(attendanceStatLabel);

        JPanel leaveBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leaveBox.setBorder(BorderFactory.createTitledBorder("Leave Request Status"));
        leaveStatLabel = new JLabel("Loading...");
        leaveStatLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        leaveBox.add(leaveStatLabel);

        JPanel salBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        salBox.setBorder(BorderFactory.createTitledBorder("Latest Salary Summary"));
        salaryStatLabel = new JLabel("Loading...");
        salaryStatLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        salBox.add(salaryStatLabel);

        centerPanel.add(attBox);
        centerPanel.add(leaveBox);
        centerPanel.add(salBox);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        try {
            // Attendance summary
            List<Attendance> attList = attendanceDAO.getByEmployee(employee.getEmpId());
            int present = 0;
            for (Attendance a : attList) {
                if ("Present".equalsIgnoreCase(a.getStatus())) present++;
            }
            attendanceStatLabel.setText("Present: " + present + " / " + attList.size() + " Total Recorded Days");

            // Leave summary
            List<LeaveRequest> leaveList = leaveDAO.getByEmployee(employee.getEmpId());
            int pending = 0;
            for (LeaveRequest r : leaveList) {
                if (LeaveRequest.STATUS_PENDING.equalsIgnoreCase(r.getStatus())) pending++;
            }
            leaveStatLabel.setText(pending > 0 ? pending + " Pending Leave Request(s)" : "All leave requests reviewed");

            // Salary summary
            Salary latest = salaryDAO.getLatestSalaryByEmployee(employee.getEmpId());
            if (latest != null) {
                salaryStatLabel.setText("Net Salary: Rs. " + String.format("%.2f", latest.getNetSalary()) + " (" + latest.getMonth() + " " + latest.getYear() + ")");
            } else {
                salaryStatLabel.setText("No salary records generated yet");
            }

        } catch (AppException e) {
            System.err.println("Error refreshing employee home: " + e.getMessage());
        }
    }
}
