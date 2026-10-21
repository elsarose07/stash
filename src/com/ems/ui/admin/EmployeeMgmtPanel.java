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
import java.sql.Date;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP Employee Management Panel using standard Swing components.
 */
public class EmployeeMgmtPanel extends JPanel {

    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<Department> deptComboBox;
    private JTextField designationField;
    private JTextField salaryField;
    private JTextField joinDateField;

    private JTextField searchField;
    private JButton searchButton;
    private JButton resetButton;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;

    public EmployeeMgmtPanel() {
        this.employeeDAO = new EmployeeDAO();
        this.departmentDAO = new DepartmentDAO();

        initializeUI();
        loadDepartments();
        loadEmployeeTable(null);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Search Bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        resetButton = new JButton("Reset");

        topPanel.add(new JLabel("Search Employee:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(resetButton);
        add(topPanel, BorderLayout.NORTH);

        // Form Panel on WEST
        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        JPanel fieldsPanel = new JPanel(new GridLayout(9, 2, 5, 8));
        idField = new JTextField();
        idField.setEditable(false);
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        deptComboBox = new JComboBox<>();
        designationField = new JTextField();
        salaryField = new JTextField();
        joinDateField = new JTextField("2026-01-01");

        fieldsPanel.add(new JLabel("ID:"));
        fieldsPanel.add(idField);
        fieldsPanel.add(new JLabel("First Name: *"));
        fieldsPanel.add(firstNameField);
        fieldsPanel.add(new JLabel("Last Name: *"));
        fieldsPanel.add(lastNameField);
        fieldsPanel.add(new JLabel("Email: *"));
        fieldsPanel.add(emailField);
        fieldsPanel.add(new JLabel("Phone:"));
        fieldsPanel.add(phoneField);
        fieldsPanel.add(new JLabel("Department:"));
        fieldsPanel.add(deptComboBox);
        fieldsPanel.add(new JLabel("Designation: *"));
        fieldsPanel.add(designationField);
        fieldsPanel.add(new JLabel("Basic Salary: *"));
        fieldsPanel.add(salaryField);
        fieldsPanel.add(new JLabel("Join Date (YYYY-MM-DD):"));
        fieldsPanel.add(joinDateField);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Bar
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
        add(formPanel, BorderLayout.WEST);

        // Table on CENTER
        tableModel = new DefaultTableModel();
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearForm());
        searchButton.addActionListener(e -> searchEmployees());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            loadEmployeeTable(null);
        });

        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = employeeTable.getSelectedRow();
                if (row != -1) {
                    populateFormFromRow(row);
                }
            }
        });
    }

    public void loadDepartments() {
        try {
            deptComboBox.removeAllItems();
            List<Department> depts = departmentDAO.getAll();
            for (Department d : depts) {
                deptComboBox.addItem(d);
            }
        } catch (AppException e) {
            System.err.println("Failed to load departments: " + e.getMessage());
        }
    }

    public void loadEmployeeTable(List<Employee> customList) {
        try {
            List<Employee> list = customList != null ? customList : employeeDAO.getAll();

            Vector<String> columns = new Vector<>();
            columns.add("ID");
            columns.add("First Name");
            columns.add("Last Name");
            columns.add("Email");
            columns.add("Phone");
            columns.add("Department");
            columns.add("Designation");
            columns.add("Basic Salary");
            columns.add("Join Date");

            Vector<Vector<Object>> data = new Vector<>();
            for (Employee emp : list) {
                Vector<Object> row = new Vector<>();
                row.add(emp.getEmpId());
                row.add(emp.getFirstName());
                row.add(emp.getLastName());
                row.add(emp.getEmail());
                row.add(emp.getPhone());
                row.add(emp.getDeptName());
                row.add(emp.getDesignation());
                row.add(emp.getBasicSalary());
                row.add(emp.getJoinDate());
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load employees: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromRow(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        firstNameField.setText(tableModel.getValueAt(row, 1).toString());
        lastNameField.setText(tableModel.getValueAt(row, 2).toString());
        emailField.setText(tableModel.getValueAt(row, 3).toString());
        Object phoneVal = tableModel.getValueAt(row, 4);
        phoneField.setText(phoneVal != null ? phoneVal.toString() : "");

        String deptName = tableModel.getValueAt(row, 5).toString();
        for (int i = 0; i < deptComboBox.getItemCount(); i++) {
            if (deptComboBox.getItemAt(i).getDeptName().equalsIgnoreCase(deptName)) {
                deptComboBox.setSelectedIndex(i);
                break;
            }
        }

        designationField.setText(tableModel.getValueAt(row, 6).toString());
        salaryField.setText(tableModel.getValueAt(row, 7).toString());
        joinDateField.setText(tableModel.getValueAt(row, 8).toString());
    }

    private void addEmployee() {
        if (!validateInputs()) return;

        try {
            Department selectedDept = (Department) deptComboBox.getSelectedItem();
            int deptId = selectedDept != null ? selectedDept.getDeptId() : 0;
            double salary = Double.parseDouble(salaryField.getText().trim());
            Date joinDate = Date.valueOf(joinDateField.getText().trim());

            Employee emp = new Employee(
                0,
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                deptId,
                designationField.getText().trim(),
                salary,
                joinDate
            );

            if (employeeDAO.add(emp)) {
                JOptionPane.showMessageDialog(this, "Employee added successfully (ID: " + emp.getEmpId() + ")",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEmployeeTable(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an employee to update.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;

        try {
            int empId = Integer.parseInt(idField.getText().trim());
            Department selectedDept = (Department) deptComboBox.getSelectedItem();
            int deptId = selectedDept != null ? selectedDept.getDeptId() : 0;
            double salary = Double.parseDouble(salaryField.getText().trim());
            Date joinDate = Date.valueOf(joinDateField.getText().trim());

            Employee emp = new Employee(
                empId,
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                deptId,
                designationField.getText().trim(),
                salary,
                joinDate
            );

            if (employeeDAO.update(emp)) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEmployeeTable(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an employee to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this employee?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int empId = Integer.parseInt(idField.getText().trim());
                if (employeeDAO.delete(empId)) {
                    JOptionPane.showMessageDialog(this, "Employee deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadEmployeeTable(null);
                }
            } catch (AppException e) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchEmployees() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadEmployeeTable(null);
            return;
        }
        try {
            List<Employee> results = employeeDAO.search(keyword);
            loadEmployeeTable(results);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        designationField.setText("");
        salaryField.setText("");
        joinDateField.setText("2026-01-01");
        employeeTable.clearSelection();
    }

    private boolean validateInputs() {
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            designationField.getText().trim().isEmpty() ||
            salaryField.getText().trim().isEmpty() ||
            joinDateField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill in all required fields (*)", "Notice", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
