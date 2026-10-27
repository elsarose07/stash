package com.ems.ui;

import com.ems.model.Employee;
import com.ems.ui.employee.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simple MVP Employee Dashboard Frame.
 */
public class EmployeeDashboardFrame extends JFrame {

    private Employee employee;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private EmployeeHomePanel homePanel;
    private MyProfilePanel profilePanel;
    private MyAttendancePanel attendancePanel;
    private MyLeavePanel leavePanel;
    private MySalaryPanel salaryPanel;
    private EmployeeSettingsPanel settingsPanel;

    public EmployeeDashboardFrame(Employee employee) {
        this.employee = employee;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Employee Management System - Employee Portal (" + employee.getFullName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Navigation
        JPanel sidebar = new JPanel(new GridLayout(7, 1, 0, 8));
        sidebar.setPreferredSize(new Dimension(180, 600));
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnDashboard = new JButton("Dashboard");
        JButton btnProfile = new JButton("My Profile");
        JButton btnAttendance = new JButton("My Attendance");
        JButton btnLeave = new JButton("My Leave");
        JButton btnSalary = new JButton("My Salary");
        JButton btnSettings = new JButton("Settings");
        JButton btnLogout = new JButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(btnProfile);
        sidebar.add(btnAttendance);
        sidebar.add(btnLeave);
        sidebar.add(btnSalary);
        sidebar.add(btnSettings);
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // Center Content Area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        homePanel = new EmployeeHomePanel(employee);
        profilePanel = new MyProfilePanel(employee);
        attendancePanel = new MyAttendancePanel(employee);
        leavePanel = new MyLeavePanel(employee);
        salaryPanel = new MySalaryPanel(employee);
        settingsPanel = new EmployeeSettingsPanel(employee);

        contentPanel.add(homePanel, "dashboard");
        contentPanel.add(profilePanel, "profile");
        contentPanel.add(attendancePanel, "attendance");
        contentPanel.add(leavePanel, "leave");
        contentPanel.add(salaryPanel, "salary");
        contentPanel.add(settingsPanel, "settings");

        add(contentPanel, BorderLayout.CENTER);

        // Action Listeners
        btnDashboard.addActionListener(e -> {
            homePanel.refreshData();
            cardLayout.show(contentPanel, "dashboard");
        });

        btnProfile.addActionListener(e -> {
            profilePanel.loadProfileData();
            cardLayout.show(contentPanel, "profile");
        });

        btnAttendance.addActionListener(e -> {
            attendancePanel.loadAttendanceData();
            cardLayout.show(contentPanel, "attendance");
        });

        btnLeave.addActionListener(e -> {
            leavePanel.loadLeaveHistory();
            cardLayout.show(contentPanel, "leave");
        });

        btnSalary.addActionListener(e -> {
            salaryPanel.loadSalaryData();
            cardLayout.show(contentPanel, "salary");
        });

        btnSettings.addActionListener(e -> {
            cardLayout.show(contentPanel, "settings");
        });

        btnLogout.addActionListener(e -> logout());

        cardLayout.show(contentPanel, "dashboard");
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?",
            "Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
