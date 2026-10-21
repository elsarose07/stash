package com.ems.ui.admin;

import com.ems.dao.EmployeeDAO;
import com.ems.dao.UserDAO;
import com.ems.exception.AppException;
import com.ems.model.Employee;
import com.ems.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP Settings Panel using standard Swing components.
 */
public class SettingsPanel extends JPanel {

    private JTable usersTable;
    private DefaultTableModel tableModel;

    // Change Password Form
    private JTextField selectedUserIdField;
    private JTextField selectedUsernameField;
    private JPasswordField newPasswordField;
    private JButton changePasswordButton;

    // Create Employee Account Form
    private JComboBox<Employee> employeeComboBox;
    private JTextField newAccUsernameField;
    private JPasswordField newAccPasswordField;
    private JButton createAccountButton;

    private UserDAO userDAO;
    private EmployeeDAO employeeDAO;

    public SettingsPanel() {
        this.userDAO = new UserDAO();
        this.employeeDAO = new EmployeeDAO();

        initializeUI();
        loadUsersTable();
        loadEmployees();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Container on WEST
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        leftPanel.setPreferredSize(new Dimension(320, 0));

        // 1. Change Password
        JPanel pwdPanel = new JPanel(new BorderLayout(5, 5));
        pwdPanel.setBorder(BorderFactory.createTitledBorder("Change Password"));

        JPanel pwdGrid = new JPanel(new GridLayout(3, 2, 5, 6));
        selectedUserIdField = new JTextField();
        selectedUserIdField.setEditable(false);
        selectedUsernameField = new JTextField();
        selectedUsernameField.setEditable(false);
        newPasswordField = new JPasswordField();

        pwdGrid.add(new JLabel("User ID:"));
        pwdGrid.add(selectedUserIdField);
        pwdGrid.add(new JLabel("Username:"));
        pwdGrid.add(selectedUsernameField);
        pwdGrid.add(new JLabel("New Password:"));
        pwdGrid.add(newPasswordField);

        pwdPanel.add(pwdGrid, BorderLayout.CENTER);

        changePasswordButton = new JButton("Update Password");
        pwdPanel.add(changePasswordButton, BorderLayout.SOUTH);
        leftPanel.add(pwdPanel);

        // 2. Create Employee Account
        JPanel accPanel = new JPanel(new BorderLayout(5, 5));
        accPanel.setBorder(BorderFactory.createTitledBorder("Create Employee Account"));

        JPanel accGrid = new JPanel(new GridLayout(3, 2, 5, 6));
        employeeComboBox = new JComboBox<>();
        newAccUsernameField = new JTextField();
        newAccPasswordField = new JPasswordField();

        accGrid.add(new JLabel("Employee:"));
        accGrid.add(employeeComboBox);
        accGrid.add(new JLabel("Username:"));
        accGrid.add(newAccUsernameField);
        accGrid.add(new JLabel("Password:"));
        accGrid.add(newAccPasswordField);

        accPanel.add(accGrid, BorderLayout.CENTER);

        createAccountButton = new JButton("Create User Account");
        accPanel.add(createAccountButton, BorderLayout.SOUTH);
        leftPanel.add(accPanel);

        add(leftPanel, BorderLayout.WEST);

        // Right Table on CENTER
        tableModel = new DefaultTableModel();
        usersTable = new JTable(tableModel);
        add(new JScrollPane(usersTable), BorderLayout.CENTER);

        // Listeners
        changePasswordButton.addActionListener(e -> changePassword());
        createAccountButton.addActionListener(e -> createEmployeeAccount());

        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = usersTable.getSelectedRow();
                if (row != -1) {
                    selectedUserIdField.setText(tableModel.getValueAt(row, 0).toString());
                    selectedUsernameField.setText(tableModel.getValueAt(row, 1).toString());
                }
            }
        });
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
                row.add(u.getEmpId() != null ? u.getEmpId() : "Admin");
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadEmployees() {
        try {
            employeeComboBox.removeAllItems();
            List<Employee> list = employeeDAO.getAll();
            for (Employee emp : list) {
                employeeComboBox.addItem(emp);
            }
        } catch (AppException e) {
            System.err.println("Failed to load employees: " + e.getMessage());
        }
    }

    private void changePassword() {
        if (selectedUserIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newPwd = new String(newPasswordField.getPassword()).trim();
        if (newPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int userId = Integer.parseInt(selectedUserIdField.getText().trim());
            if (userDAO.changePassword(userId, newPwd)) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                newPasswordField.setText("");
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createEmployeeAccount() {
        Employee emp = (Employee) employeeComboBox.getSelectedItem();
        String uName = newAccUsernameField.getText().trim();
        String pwd = new String(newAccPasswordField.getPassword()).trim();

        if (emp == null || uName.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill in all account fields.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (userDAO.createEmployeeAccount(emp.getEmpId(), uName, pwd)) {
                JOptionPane.showMessageDialog(this, "Account created for " + emp.getFullName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                newAccUsernameField.setText("");
                newAccPasswordField.setText("");
                loadUsersTable();
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
