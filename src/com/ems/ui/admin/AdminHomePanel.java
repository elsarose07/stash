package com.ems.ui.admin;

import com.ems.dao.AttendanceDAO;
import com.ems.dao.DepartmentDAO;
import com.ems.dao.EmployeeDAO;
import com.ems.dao.LeaveDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simple MVP Admin Dashboard Overview Panel.
 */
public class AdminHomePanel extends JPanel {

    private JLabel empCountLabel;
    private JLabel deptCountLabel;
    private JLabel attendanceCountLabel;
    private JLabel leaveCountLabel;

    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;
    private AttendanceDAO attendanceDAO;
    private LeaveDAO leaveDAO;

    public AdminHomePanel() {
        this.employeeDAO = new EmployeeDAO();
        this.departmentDAO = new DepartmentDAO();
        this.attendanceDAO = new AttendanceDAO();
        this.leaveDAO = new LeaveDAO();

        initializeUI();
        refreshStats();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Title
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Organization Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshStats());

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(refreshButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Grid of Overview Stats
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        empCountLabel = new JLabel("0", JLabel.CENTER);
        deptCountLabel = new JLabel("0", JLabel.CENTER);
        attendanceCountLabel = new JLabel("0", JLabel.CENTER);
        leaveCountLabel = new JLabel("0", JLabel.CENTER);

        statsPanel.add(createStatBox("Total Employees", empCountLabel));
        statsPanel.add(createStatBox("Total Departments", deptCountLabel));
        statsPanel.add(createStatBox("Today's Attendance (Present)", attendanceCountLabel));
        statsPanel.add(createStatBox("Pending Leave Requests", leaveCountLabel));

        add(statsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatBox(String title, JLabel valueLabel) {
        JPanel box = new JPanel(new BorderLayout(5, 5));
        box.setBorder(BorderFactory.createTitledBorder(title));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        box.add(valueLabel, BorderLayout.CENTER);
        return box;
    }

    public void refreshStats() {
        try {
            empCountLabel.setText(String.valueOf(employeeDAO.getEmployeeCount()));
            deptCountLabel.setText(String.valueOf(departmentDAO.getDepartmentCount()));
            attendanceCountLabel.setText(String.valueOf(attendanceDAO.getTodayAttendanceCount()));
            leaveCountLabel.setText(String.valueOf(leaveDAO.getPendingCount()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load stats: " + e.getMessage(),
                "Notice", JOptionPane.WARNING_MESSAGE);
        }
    }
}
