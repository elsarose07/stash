package com.ems.ui.admin;

import com.ems.dao.DepartmentDAO;
import com.ems.dao.EmployeeDAO;
import com.ems.exception.AppException;
import com.ems.model.Department;
import com.ems.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP Department Management Panel using standard Swing components.
 */
public class DepartmentMgmtPanel extends JPanel {

    private JTextField deptIdField;
    private JTextField deptNameField;
    private JTextField descField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable deptTable;
    private DefaultTableModel tableModel;

    // Assignment section
    private JComboBox<Employee> employeeComboBox;
    private JComboBox<Department> assignDeptComboBox;
    private JButton assignButton;

    private DepartmentDAO departmentDAO;
    private EmployeeDAO employeeDAO;

    public DepartmentMgmtPanel() {
        this.departmentDAO = new DepartmentDAO();
        this.employeeDAO = new EmployeeDAO();

        initializeUI();
        loadDepartmentTable();
        loadAssignmentCombos();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Container
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(320, 0));

        // 1. Dept Form
        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Details"));

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 5, 8));
        deptIdField = new JTextField();
        deptIdField.setEditable(false);
        deptNameField = new JTextField();
        descField = new JTextField();

        fieldsPanel.add(new JLabel("Dept ID:"));
        fieldsPanel.add(deptIdField);
        fieldsPanel.add(new JLabel("Dept Name: *"));
        fieldsPanel.add(deptNameField);
        fieldsPanel.add(new JLabel("Description:"));
        fieldsPanel.add(descField);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");

        btnPanel.add(addButton);
        btnPanel.add(updateButton);
        btnPanel.add(deleteButton);
        btnPanel.add(clearButton);

        formPanel.add(btnPanel, BorderLayout.SOUTH);
        leftPanel.add(formPanel, BorderLayout.NORTH);

        // 2. Assign Employee to Department
        JPanel assignPanel = new JPanel(new BorderLayout(5, 5));
        assignPanel.setBorder(BorderFactory.createTitledBorder("Assign Employee to Department"));

        JPanel assignGrid = new JPanel(new GridLayout(2, 2, 5, 8));
        employeeComboBox = new JComboBox<>();
        assignDeptComboBox = new JComboBox<>();

        assignGrid.add(new JLabel("Employee:"));
        assignGrid.add(employeeComboBox);
        assignGrid.add(new JLabel("Department:"));
        assignGrid.add(assignDeptComboBox);

        assignPanel.add(assignGrid, BorderLayout.CENTER);

        assignButton = new JButton("Assign Department");
        assignPanel.add(assignButton, BorderLayout.SOUTH);

        leftPanel.add(assignPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // Right Table
        tableModel = new DefaultTableModel();
        deptTable = new JTable(tableModel);
        add(new JScrollPane(deptTable), BorderLayout.CENTER);

        // Action Listeners
        addButton.addActionListener(e -> addDepartment());
        updateButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        clearButton.addActionListener(e -> clearForm());
        assignButton.addActionListener(e -> assignEmployeeToDept());

        deptTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = deptTable.getSelectedRow();
                if (row != -1) {
                    deptIdField.setText(tableModel.getValueAt(row, 0).toString());
                    deptNameField.setText(tableModel.getValueAt(row, 1).toString());
                    Object desc = tableModel.getValueAt(row, 2);
                    descField.setText(desc != null ? desc.toString() : "");
                }
            }
        });
    }

    public void loadDepartmentTable() {
        try {
            List<Department> list = departmentDAO.getAll();
            Vector<String> columns = new Vector<>();
            columns.add("Dept ID");
            columns.add("Department Name");
            columns.add("Description");

            Vector<Vector<Object>> data = new Vector<>();
            for (Department d : list) {
                Vector<Object> row = new Vector<>();
                row.add(d.getDeptId());
                row.add(d.getDeptName());
                row.add(d.getDescription());
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load departments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadAssignmentCombos() {
        try {
            employeeComboBox.removeAllItems();
            List<Employee> emps = employeeDAO.getAll();
            for (Employee emp : emps) {
                employeeComboBox.addItem(emp);
            }

            assignDeptComboBox.removeAllItems();
            List<Department> depts = departmentDAO.getAll();
            for (Department d : depts) {
                assignDeptComboBox.addItem(d);
            }
        } catch (AppException e) {
            System.err.println("Error loading combos: " + e.getMessage());
        }
    }

    private void addDepartment() {
        String name = deptNameField.getText().trim();
        String desc = descField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Department Name is required.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Department d = new Department(0, name, desc);
            if (departmentDAO.add(d)) {
                JOptionPane.showMessageDialog(this, "Department added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadDepartmentTable();
                loadAssignmentCombos();
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDepartment() {
        if (deptIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a department to update.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(deptIdField.getText().trim());
            String name = deptNameField.getText().trim();
            String desc = descField.getText().trim();

            Department d = new Department(id, name, desc);
            if (departmentDAO.update(d)) {
                JOptionPane.showMessageDialog(this, "Department updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadDepartmentTable();
                loadAssignmentCombos();
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDepartment() {
        if (deptIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a department to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this department?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(deptIdField.getText().trim());
                if (departmentDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Department deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadDepartmentTable();
                    loadAssignmentCombos();
                }
            } catch (AppException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void assignEmployeeToDept() {
        Employee emp = (Employee) employeeComboBox.getSelectedItem();
        Department dept = (Department) assignDeptComboBox.getSelectedItem();

        if (emp == null || dept == null) {
            JOptionPane.showMessageDialog(this, "Select an employee and a department.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (employeeDAO.assignDepartment(emp.getEmpId(), dept.getDeptId())) {
                JOptionPane.showMessageDialog(this, "Assigned " + emp.getFullName() + " to " + dept.getDeptName() + "!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to assign: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        deptIdField.setText("");
        deptNameField.setText("");
        descField.setText("");
        deptTable.clearSelection();
    }
}
