package com.ems.ui.employee;

import com.ems.dao.EmployeeDAO;
import com.ems.exception.AppException;
import com.ems.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Simple MVP My Profile Panel.
 */
public class MyProfilePanel extends JPanel {

    private Employee employee;
    private EmployeeDAO employeeDAO;

    private JLabel empIdLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel deptLabel;
    private JLabel designationLabel;
    private JLabel salaryLabel;
    private JLabel joinDateLabel;
    private JLabel usernameLabel;

    public MyProfilePanel(Employee employee) {
        this.employee = employee;
        this.employeeDAO = new EmployeeDAO();

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
    }

    public void loadProfileData() {
        try {
            Employee updated = employeeDAO.getById(employee.getEmpId());
            if (updated != null) {
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
}
