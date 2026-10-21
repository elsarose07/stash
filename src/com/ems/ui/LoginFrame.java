package com.ems.ui;

import com.ems.dao.UserDAO;
import com.ems.exception.AppException;
import com.ems.model.Admin;
import com.ems.model.Employee;
import com.ems.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple MVP Login Window with Admin and Employee login panels.
 * Directly fulfills requirement:
 * "The login page should contain two panels, Admin Login and Employee Login,
 * each with username and password fields and a login button."
 */
public class LoginFrame extends JFrame {

    private JTextField adminUserField;
    private JPasswordField adminPassField;
    private JButton adminLoginButton;

    private JTextField empUserField;
    private JPasswordField empPassField;
    private JButton empLoginButton;

    private UserDAO userDAO;

    public LoginFrame() {
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Employee Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 280);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Header
        JLabel titleLabel = new JLabel("Employee Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(15, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Center: Two panels for Admin and Employee Login
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBorder(new EmptyBorder(10, 20, 15, 20));

        // Panel 1: Admin Login
        JPanel adminPanel = new JPanel(new GridLayout(3, 2, 8, 10));
        adminPanel.setBorder(BorderFactory.createTitledBorder("Admin Login"));

        adminUserField = new JTextField();
        adminPassField = new JPasswordField();
        adminLoginButton = new JButton("Login as Admin");

        adminPanel.add(new JLabel("Username:"));
        adminPanel.add(adminUserField);
        adminPanel.add(new JLabel("Password:"));
        adminPanel.add(adminPassField);
        adminPanel.add(new JLabel(""));
        adminPanel.add(adminLoginButton);

        // Panel 2: Employee Login
        JPanel empPanel = new JPanel(new GridLayout(3, 2, 8, 10));
        empPanel.setBorder(BorderFactory.createTitledBorder("Employee Login"));

        empUserField = new JTextField();
        empPassField = new JPasswordField();
        empLoginButton = new JButton("Login as Employee");

        empPanel.add(new JLabel("Username:"));
        empPanel.add(empUserField);
        empPanel.add(new JLabel("Password:"));
        empPanel.add(empPassField);
        empPanel.add(new JLabel(""));
        empPanel.add(empLoginButton);

        centerPanel.add(adminPanel);
        centerPanel.add(empPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Event Listeners
        adminLoginButton.addActionListener(new LoginActionListener(User.ROLE_ADMIN));
        empLoginButton.addActionListener(new LoginActionListener(User.ROLE_EMPLOYEE));

        adminPassField.addActionListener(new LoginActionListener(User.ROLE_ADMIN));
        empPassField.addActionListener(new LoginActionListener(User.ROLE_EMPLOYEE));
    }

    private class LoginActionListener implements ActionListener {
        private String targetRole;

        public LoginActionListener(String targetRole) {
            this.targetRole = targetRole;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String username;
            String password;

            if (User.ROLE_ADMIN.equalsIgnoreCase(targetRole)) {
                username = adminUserField.getText().trim();
                password = new String(adminPassField.getPassword()).trim();
            } else {
                username = empUserField.getText().trim();
                password = new String(empPassField.getPassword()).trim();
            }

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                    "Please enter username and password.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                User user = userDAO.authenticate(username, password, targetRole);
                JOptionPane.showMessageDialog(LoginFrame.this,
                    user.getDashboardWelcome(),
                    "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                if (user instanceof Admin) {
                    new AdminDashboardFrame((Admin) user).setVisible(true);
                } else if (user instanceof Employee) {
                    new EmployeeDashboardFrame((Employee) user).setVisible(true);
                }
                dispose();

            } catch (AppException ex) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                    ex.getMessage(),
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
