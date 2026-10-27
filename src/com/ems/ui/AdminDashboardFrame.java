package com.ems.ui;

import com.ems.model.Admin;
import com.ems.ui.admin.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simple MVP Admin Dashboard Frame.
 */
public class AdminDashboardFrame extends JFrame {

    private Admin admin;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private AdminHomePanel homePanel;
    private EmployeeMgmtPanel empPanel;
    private DepartmentMgmtPanel deptPanel;
    private AttendanceMgmtPanel attPanel;
    private LeaveMgmtPanel leavePanel;
    private SalaryMgmtPanel salaryPanel;
    private SettingsPanel settingsPanel;

    public AdminDashboardFrame(Admin admin) {
        this.admin = admin;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Employee Management System - Admin Dashboard (" + admin.getUsername() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Navigation
        JPanel sidebar = new JPanel(new GridLayout(8, 1, 0, 5));
        sidebar.setPreferredSize(new Dimension(200, 600));
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnDashboard = new JButton("Dashboard");
        JButton btnEmployees = new JButton("Employee Management");
        JButton btnDepts = new JButton("Department Management");
        JButton btnAttendance = new JButton("Attendance Management");
        JButton btnLeaves = new JButton("Leave Management");
        JButton btnSalary = new JButton("Salary Management");
        JButton btnSettings = new JButton("Settings");
        JButton btnLogout = new JButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(btnEmployees);
        sidebar.add(btnDepts);
        sidebar.add(btnAttendance);
        sidebar.add(btnLeaves);
        sidebar.add(btnSalary);
        sidebar.add(btnSettings);
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // Center Content Area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        homePanel = new AdminHomePanel();
        empPanel = new EmployeeMgmtPanel();
        deptPanel = new DepartmentMgmtPanel();
        attPanel = new AttendanceMgmtPanel();
        leavePanel = new LeaveMgmtPanel();
        salaryPanel = new SalaryMgmtPanel();
        settingsPanel = new SettingsPanel(admin);

        contentPanel.add(homePanel, "dashboard");
        contentPanel.add(empPanel, "employees");
        contentPanel.add(deptPanel, "departments");
        contentPanel.add(attPanel, "attendance");
        contentPanel.add(leavePanel, "leaves");
        contentPanel.add(salaryPanel, "salary");
        contentPanel.add(settingsPanel, "settings");

        add(contentPanel, BorderLayout.CENTER);

        // Navigation Actions
        btnDashboard.addActionListener(e -> {
            homePanel.refreshStats();
            cardLayout.show(contentPanel, "dashboard");
        });

        btnEmployees.addActionListener(e -> {
            empPanel.loadDepartments();
            empPanel.loadEmployeeTable(null);
            cardLayout.show(contentPanel, "employees");
        });

        btnDepts.addActionListener(e -> {
            deptPanel.loadDepartmentTable();
            deptPanel.loadAssignmentCombos();
            cardLayout.show(contentPanel, "departments");
        });

        btnAttendance.addActionListener(e -> {
            attPanel.loadEmployees();
            attPanel.loadAttendanceTable();
            cardLayout.show(contentPanel, "attendance");
        });

        btnLeaves.addActionListener(e -> {
            leavePanel.loadLeaveTable();
            cardLayout.show(contentPanel, "leaves");
        });

        btnSalary.addActionListener(e -> {
            salaryPanel.loadEmployees();
            salaryPanel.loadSalaryTable();
            cardLayout.show(contentPanel, "salary");
        });

        btnSettings.addActionListener(e -> {
            settingsPanel.loadUsersTable();
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
