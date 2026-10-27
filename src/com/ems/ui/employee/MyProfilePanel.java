package com.ems.ui.employee;

import com.ems.dao.EmployeeDAO;
import com.ems.dao.UserDAO;
import com.ems.exception.AppException;
import com.ems.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simple MVP My Profile Panel with update and password change support.
 */
public class MyProfilePanel extends JPanel {

    private Employee employee;
    private EmployeeDAO employeeDAO;
    private UserDAO userDAO;

    private JLabel empIdLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel deptLabel;
    private JLabel designationLabel;
    private JLabel salaryLabel;
    private JLabel joinDateLabel;
    private JLabel usernameLabel;

    private JButton editProfileButton;
    private JButton changePasswordButton;

    public MyProfilePanel(Employee employee) {
        this.employee = employee;
        this.employeeDAO = new EmployeeDAO();
        this.userDAO = new UserDAO();

        initializeUI();
        loadProfileData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel profileBox = new JPanel(new GridLayout(9, 2, 8, 10));
        profileBox.setBorder(BorderFactory.createTitledBorder("My Profile Details"));

        empIdLabel = new JLabel();
        nameLabel = new JLabel();
        usernameLabel = new JLabel();
        emailLabel = new JLabel();
        phoneLabel = new JLabel();
        deptLabel = new JLabel();
        designationLabel = new JLabel();
        salaryLabel = new JLabel();
        joinDateLabel = new JLabel();

        profileBox.add(new JLabel("Employee ID:"));
        profileBox.add(empIdLabel);
        profileBox.add(new JLabel("Full Name:"));
        profileBox.add(nameLabel);
        profileBox.add(new JLabel("Username:"));
        profileBox.add(usernameLabel);
        profileBox.add(new JLabel("Email:"));
        profileBox.add(emailLabel);
        profileBox.add(new JLabel("Phone:"));
        profileBox.add(phoneLabel);
        profileBox.add(new JLabel("Department:"));
        profileBox.add(deptLabel);
        profileBox.add(new JLabel("Designation:"));
        profileBox.add(designationLabel);
        profileBox.add(new JLabel("Basic Salary:"));
        profileBox.add(salaryLabel);
        profileBox.add(new JLabel("Join Date:"));
        profileBox.add(joinDateLabel);

        add(profileBox, BorderLayout.NORTH);

        // Action Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        editProfileButton = new JButton("Update Profile Details");
        changePasswordButton = new JButton("Change Password");

        buttonPanel.add(editProfileButton);
        buttonPanel.add(changePasswordButton);
        add(buttonPanel, BorderLayout.CENTER);

        // Action Listeners
        editProfileButton.addActionListener(e -> showEditProfileDialog());
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
    }

    public void loadProfileData() {
        try {
            Employee updated = employeeDAO.getById(employee.getEmpId());
            if (updated != null) {
                if (updated.getUserId() <= 0 && this.employee.getUserId() > 0) {
                    updated.setUserId(this.employee.getUserId());
                }
                if (updated.getUsername() == null && this.employee.getUsername() != null) {
                    updated.setUsername(this.employee.getUsername());
                }
                if (updated.getPassword() == null && this.employee.getPassword() != null) {
                    updated.setPassword(this.employee.getPassword());
                }
                this.employee = updated;
            }
        } catch (AppException e) {
            System.err.println("Could not refresh profile: " + e.getMessage());
        }

        empIdLabel.setText(String.valueOf(employee.getEmpId()));
        nameLabel.setText(employee.getFullName());
        usernameLabel.setText(employee.getUsername() != null ? employee.getUsername() : "N/A");
        emailLabel.setText(employee.getEmail());
        phoneLabel.setText(employee.getPhone() != null ? employee.getPhone() : "N/A");
        deptLabel.setText(employee.getDeptName());
        designationLabel.setText(employee.getDesignation());
        salaryLabel.setText("Rs. " + String.format("%.2f", employee.getBasicSalary()));
        joinDateLabel.setText(employee.getJoinDate() != null ? employee.getJoinDate().toString() : "N/A");
    }

    private void showEditProfileDialog() {
        JTextField editFirstName = new JTextField(employee.getFirstName(), 20);
        JTextField editLastName = new JTextField(employee.getLastName(), 20);
        JTextField editEmail = new JTextField(employee.getEmail(), 20);
        JTextField editPhone = new JTextField(employee.getPhone() != null ? employee.getPhone() : "", 20);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 10));
        form.add(new JLabel("First Name: *"));
        form.add(editFirstName);
        form.add(new JLabel("Last Name: *"));
        form.add(editLastName);
        form.add(new JLabel("Email: *"));
        form.add(editEmail);
        form.add(new JLabel("Phone:"));
        form.add(editPhone);

        int result = JOptionPane.showConfirmDialog(
            this, form, "Update My Profile Details",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String fn = editFirstName.getText().trim();
            String ln = editLastName.getText().trim();
            String em = editEmail.getText().trim();
            String ph = editPhone.getText().trim();

            if (fn.isEmpty() || ln.isEmpty() || em.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "First Name, Last Name, and Email are required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!em.contains("@") || !em.contains(".")) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Employee updated = new Employee(
                employee.getEmpId(),
                fn,
                ln,
                em,
                ph,
                employee.getDeptId(),
                employee.getDesignation(),
                employee.getBasicSalary(),
                employee.getJoinDate()
            );

            try {
                if (employeeDAO.update(updated)) {
                    JOptionPane.showMessageDialog(this,
                        "Profile details updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProfileData();
                }
            } catch (AppException ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to update profile: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showChangePasswordDialog() {
        JPasswordField currentPassField = new JPasswordField(20);
        JPasswordField newPassField = new JPasswordField(20);
        JPasswordField confirmPassField = new JPasswordField(20);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 10));
        form.add(new JLabel("Current Password: *"));
        form.add(currentPassField);
        form.add(new JLabel("New Password: *"));
        form.add(newPassField);
        form.add(new JLabel("Confirm New Password: *"));
        form.add(confirmPassField);

        int result = JOptionPane.showConfirmDialog(
            this, form, "Change Confidential Password",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String currentPass = new String(currentPassField.getPassword()).trim();
            String newPass = new String(newPassField.getPassword()).trim();
            String confirmPass = new String(confirmPassField.getPassword()).trim();

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "All password fields are required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (employee.getPassword() != null && !employee.getPassword().isEmpty() &&
                !employee.getPassword().equals(currentPass)) {
                JOptionPane.showMessageDialog(this,
                    "Incorrect current password. Please try again.",
                    "Authentication Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPass.length() < 4) {
                JOptionPane.showMessageDialog(this,
                    "New password must be at least 4 characters long.",
                    "Weak Password", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this,
                    "New password and confirmation password do not match.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (newPass.equals(currentPass)) {
                JOptionPane.showMessageDialog(this,
                    "New password cannot be the same as your current password.",
                    "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int userId = employee.getUserId();
            if (userId <= 0) {
                try {
                    Employee fresh = employeeDAO.getById(employee.getEmpId());
                    if (fresh != null && fresh.getUserId() > 0) {
                        userId = fresh.getUserId();
                        employee.setUserId(userId);
                    }
                } catch (Exception ignored) {}
            }

            if (userId <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Could not locate your user account ID. Please contact administrator.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (userDAO.changePassword(userId, newPass)) {
                    employee.setPassword(newPass);
                    JOptionPane.showMessageDialog(this,
                        "Password changed successfully!\nYour password is now confidential and private to you.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (AppException ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to update password: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
