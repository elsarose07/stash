package com.ems.ui.employee;

import com.ems.dao.UserDAO;
import com.ems.exception.AppException;
import com.ems.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Settings Panel for Employee Portal.
 * Provides self-service password management:
 * 1. Change Password (when current password is known)
 * 2. Forgotten Password Recovery (via registered email verification)
 */
public class EmployeeSettingsPanel extends JPanel {

    private Employee employee;
    private UserDAO userDAO;

    // Change Password components
    private JPasswordField curPassField;
    private JPasswordField newPassField;
    private JPasswordField confPassField;
    private JButton btnChangePassword;

    // Forgotten Password Recovery components
    private JTextField emailRecoveryField;
    private JPasswordField resetNewPassField;
    private JPasswordField resetConfPassField;
    private JButton btnResetPassword;

    public EmployeeSettingsPanel(Employee employee) {
        this.employee = employee;
        this.userDAO = new UserDAO();

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 20, 15, 20));

        // Header Title
        JLabel titleLabel = new JLabel("Account & Security Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Center split into 2 cards: Change Password & Forgotten Password Recovery
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 20, 0));

        // 1. Change Password Card
        JPanel changePassCard = new JPanel(new BorderLayout(8, 8));
        changePassCard.setBorder(BorderFactory.createTitledBorder("Change Password"));

        JPanel changeGrid = new JPanel(new GridLayout(4, 2, 8, 12));
        curPassField = new JPasswordField();
        newPassField = new JPasswordField();
        confPassField = new JPasswordField();

        changeGrid.add(new JLabel("Username:"));
        JLabel userLbl = new JLabel(employee.getUsername() != null ? employee.getUsername() : "N/A");
        userLbl.setFont(new Font("Arial", Font.BOLD, 12));
        changeGrid.add(userLbl);

        changeGrid.add(new JLabel("Current Password: *"));
        changeGrid.add(curPassField);
        changeGrid.add(new JLabel("New Password: *"));
        changeGrid.add(newPassField);
        changeGrid.add(new JLabel("Confirm New Password: *"));
        changeGrid.add(confPassField);

        changePassCard.add(changeGrid, BorderLayout.CENTER);

        btnChangePassword = new JButton("Update Password");
        JPanel btnWrapper1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrapper1.add(btnChangePassword);
        changePassCard.add(btnWrapper1, BorderLayout.SOUTH);

        // 2. Forgotten Password Recovery Card
        JPanel recoverCard = new JPanel(new BorderLayout(8, 8));
        recoverCard.setBorder(BorderFactory.createTitledBorder("Forgotten Password Recovery"));

        JLabel recoverNotice = new JLabel("<html><i>Forgot your current password?<br>Verify your registered email address to set a new password.</i></html>");
        recoverNotice.setBorder(new EmptyBorder(5, 5, 8, 5));
        recoverCard.add(recoverNotice, BorderLayout.NORTH);

        JPanel recoverGrid = new JPanel(new GridLayout(4, 2, 8, 12));
        emailRecoveryField = new JTextField();
        resetNewPassField = new JPasswordField();
        resetConfPassField = new JPasswordField();

        recoverGrid.add(new JLabel("Username:"));
        JLabel recUserLbl = new JLabel(employee.getUsername() != null ? employee.getUsername() : "N/A");
        recUserLbl.setFont(new Font("Arial", Font.BOLD, 12));
        recoverGrid.add(recUserLbl);

        recoverGrid.add(new JLabel("Registered Email: *"));
        recoverGrid.add(emailRecoveryField);
        recoverGrid.add(new JLabel("New Password: *"));
        recoverGrid.add(resetNewPassField);
        recoverGrid.add(new JLabel("Confirm Password: *"));
        recoverGrid.add(resetConfPassField);

        recoverCard.add(recoverGrid, BorderLayout.CENTER);

        btnResetPassword = new JButton("Reset Password via Email");
        JPanel btnWrapper2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrapper2.add(btnResetPassword);
        recoverCard.add(btnWrapper2, BorderLayout.SOUTH);

        centerGrid.add(changePassCard);
        centerGrid.add(recoverCard);

        add(centerGrid, BorderLayout.CENTER);

        // Action Listeners
        btnChangePassword.addActionListener(e -> handleChangePassword());
        btnResetPassword.addActionListener(e -> handleResetForgottenPassword());
    }

    private void handleChangePassword() {
        String curPass = new String(curPassField.getPassword()).trim();
        String newPass = new String(newPassField.getPassword()).trim();
        String confPass = new String(confPassField.getPassword()).trim();

        if (curPass.isEmpty() || newPass.isEmpty() || confPass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all password fields.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (employee.getPassword() != null && !employee.getPassword().isEmpty() &&
            !employee.getPassword().equals(curPass)) {
            JOptionPane.showMessageDialog(this,
                "Current password does not match.",
                "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPass.length() < 4) {
            JOptionPane.showMessageDialog(this,
                "New password must be at least 4 characters long.",
                "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confPass)) {
            JOptionPane.showMessageDialog(this,
                "New password and confirmation password do not match.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newPass.equals(curPass)) {
            JOptionPane.showMessageDialog(this,
                "New password cannot be the same as your current password.",
                "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (userDAO.changePassword(employee.getUserId(), newPass)) {
                employee.setPassword(newPass);
                JOptionPane.showMessageDialog(this,
                    "Password updated successfully!\nYour password is now confidential.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                curPassField.setText("");
                newPassField.setText("");
                confPassField.setText("");
            }
        } catch (AppException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to update password: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleResetForgottenPassword() {
        String email = emailRecoveryField.getText().trim();
        String newPass = new String(resetNewPassField.getPassword()).trim();
        String confPass = new String(resetConfPassField.getPassword()).trim();

        if (email.isEmpty() || newPass.isEmpty() || confPass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in your registered email and new passwords.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newPass.length() < 4) {
            JOptionPane.showMessageDialog(this,
                "New password must be at least 4 characters long.",
                "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confPass)) {
            JOptionPane.showMessageDialog(this,
                "New password and confirmation password do not match.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (userDAO.resetForgottenPassword(employee.getUsername(), email, newPass)) {
                employee.setPassword(newPass);
                JOptionPane.showMessageDialog(this,
                    "Identity verified successfully!\nYour password has been reset.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                emailRecoveryField.setText("");
                resetNewPassField.setText("");
                resetConfPassField.setText("");
            }
        } catch (AppException ex) {
            JOptionPane.showMessageDialog(this,
                "Verification failed: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
