package com.ems.ui.admin;

import com.ems.dao.UserDAO;
import com.ems.exception.AppException;
import com.ems.model.Admin;
import com.ems.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP Settings Panel for Administrator.
 * Allows Admin to manage their own password and view the system user directory (Read-Only).
 * Admin cannot view, modify, or override employee confidential passwords.
 */
public class SettingsPanel extends JPanel {

    private Admin admin;
    private JTable usersTable;
    private DefaultTableModel tableModel;

    // Change Admin Password Form
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changePasswordButton;

    private UserDAO userDAO;

    public SettingsPanel() {
        this(null);
    }

    public SettingsPanel(Admin admin) {
        this.admin = admin;
        this.userDAO = new UserDAO();

        initializeUI();
        loadUsersTable();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Container on WEST: Admin Password Change Only
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(320, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Change Administrator Password"));

        JPanel pwdGrid = new JPanel(new GridLayout(4, 2, 5, 10));
        currentPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        pwdGrid.add(new JLabel("Admin Username:"));
        JLabel adminUserLbl = new JLabel(admin != null ? admin.getUsername() : "admin");
        adminUserLbl.setFont(new Font("Arial", Font.BOLD, 12));
        pwdGrid.add(adminUserLbl);

        pwdGrid.add(new JLabel("Current Password: *"));
        pwdGrid.add(currentPasswordField);
        pwdGrid.add(new JLabel("New Password: *"));
        pwdGrid.add(newPasswordField);
        pwdGrid.add(new JLabel("Confirm Password: *"));
        pwdGrid.add(confirmPasswordField);

        leftPanel.add(pwdGrid, BorderLayout.NORTH);

        changePasswordButton = new JButton("Update Admin Password");
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrapper.add(changePasswordButton);
        leftPanel.add(btnWrapper, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Center Container: User Directory (Read-Only)
        JPanel rightPanel = new JPanel(new BorderLayout(5, 8));
        rightPanel.setBorder(BorderFactory.createTitledBorder("System User Accounts Directory (Read-Only)"));

        JLabel noticeLabel = new JLabel("<html><i>Note: Employee accounts are created with default passwords during Employee Registration.<br>" +
            "Employee passwords are private and managed exclusively by employees. Admins cannot alter employee passwords.</i></html>");
        noticeLabel.setBorder(new EmptyBorder(5, 5, 8, 5));
        rightPanel.add(noticeLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        usersTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rightPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        JButton refreshTableBtn = new JButton("Refresh Directory");
        refreshTableBtn.addActionListener(e -> loadUsersTable());
        JPanel rightBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBottom.add(refreshTableBtn);
        rightPanel.add(rightBottom, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);

        // Listener
        changePasswordButton.addActionListener(e -> changeAdminPassword());
    }

    public void loadEmployees() {
        // No longer needed; employee account creation is handled during registration in Employee Management
    }

    public void loadUsersTable() {
        try {
            List<User> list = userDAO.getAllUsers();

            Vector<String> columns = new Vector<>();
            columns.add("User ID");
            columns.add("Username");
            columns.add("Role");
            columns.add("Emp ID");

            Vector<Vector<Object>> data = new Vector<>();
            for (User u : list) {
                Vector<Object> row = new Vector<>();
                row.add(u.getUserId());
                row.add(u.getUsername());
                row.add(u.getRole());
                row.add(u.getEmpId() != null ? u.getEmpId() : "N/A (Admin)");
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeAdminPassword() {
        String curPwd = new String(currentPasswordField.getPassword()).trim();
        String newPwd = new String(newPasswordField.getPassword()).trim();
        String confPwd = new String(confirmPasswordField.getPassword()).trim();

        if (curPwd.isEmpty() || newPwd.isEmpty() || confPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All password fields are required.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (admin != null && admin.getPassword() != null && !admin.getPassword().isEmpty() &&
            !admin.getPassword().equals(curPwd)) {
            JOptionPane.showMessageDialog(this, "Current administrator password is incorrect.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPwd.length() < 4) {
            JOptionPane.showMessageDialog(this, "New password must be at least 4 characters long.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPwd.equals(confPwd)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int adminUserId = (admin != null && admin.getUserId() > 0) ? admin.getUserId() : 1;

        try {
            if (userDAO.changePassword(adminUserId, newPwd)) {
                if (admin != null) {
                    admin.setPassword(newPwd);
                }
                JOptionPane.showMessageDialog(this, "Administrator password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                currentPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to update password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
