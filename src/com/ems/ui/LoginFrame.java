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
        setSize(680, 310);
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
        JPanel adminPanel = new JPanel(new GridLayout(4, 2, 8, 10));
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
        adminPanel.add(new JLabel(""));
        adminPanel.add(new JLabel(""));

        // Panel 2: Employee Login
        JPanel empPanel = new JPanel(new GridLayout(4, 2, 8, 10));
        empPanel.setBorder(BorderFactory.createTitledBorder("Employee Login"));

        empUserField = new JTextField();
        empPassField = new JPasswordField();
        empLoginButton = new JButton("Login as Employee");
        JButton forgotPassButton = new JButton("Forgot Password?");

        empPanel.add(new JLabel("Username:"));
        empPanel.add(empUserField);
        empPanel.add(new JLabel("Password:"));
        empPanel.add(empPassField);
        empPanel.add(new JLabel(""));
        empPanel.add(empLoginButton);
        empPanel.add(new JLabel(""));
        empPanel.add(forgotPassButton);

        centerPanel.add(adminPanel);
        centerPanel.add(empPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Event Listeners
        adminLoginButton.addActionListener(new LoginActionListener(User.ROLE_ADMIN));
        empLoginButton.addActionListener(new LoginActionListener(User.ROLE_EMPLOYEE));

        adminPassField.addActionListener(new LoginActionListener(User.ROLE_ADMIN));
        empPassField.addActionListener(new LoginActionListener(User.ROLE_EMPLOYEE));

        forgotPassButton.addActionListener(e -> showForgotPasswordDialog());
    }

    private void showForgotPasswordDialog() {
        JTextField uField = new JTextField(15);
        JTextField emField = new JTextField(15);
        JPasswordField npField = new JPasswordField(15);
        JPasswordField cnpField = new JPasswordField(15);

        JPanel fp = new JPanel(new GridLayout(4, 2, 8, 10));
        fp.add(new JLabel("Username: *"));
        fp.add(uField);
        fp.add(new JLabel("Registered Email: *"));
        fp.add(emField);
        fp.add(new JLabel("New Password: *"));
        fp.add(npField);
        fp.add(new JLabel("Confirm New Password: *"));
        fp.add(cnpField);

        int opt = JOptionPane.showConfirmDialog(LoginFrame.this, fp,
            "Reset Forgotten Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            String uName = uField.getText().trim();
            String email = emField.getText().trim();
            String np = new String(npField.getPassword()).trim();
            String cnp = new String(cnpField.getPassword()).trim();

            if (uName.isEmpty() || email.isEmpty() || np.isEmpty() || cnp.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                    "Please fill in all fields.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (np.length() < 4) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                    "New password must be at least 4 characters long.", "Weak Password", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!np.equals(cnp)) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                    "New password and confirmation do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (userDAO.resetForgottenPassword(uName, email, np)) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Password reset successfully! You can now log in with your new password.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    empUserField.setText(uName);
                    empPassField.setText("");
                }
            } catch (AppException ex) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                    "Reset failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
