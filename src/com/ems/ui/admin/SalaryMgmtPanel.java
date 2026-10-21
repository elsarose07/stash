package com.ems.ui.admin;

import com.ems.dao.EmployeeDAO;
import com.ems.dao.SalaryDAO;
import com.ems.exception.AppException;
import com.ems.model.Employee;
import com.ems.model.Salary;
import com.ems.util.SalaryCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP Salary Management Panel using standard Swing components.
 */
public class SalaryMgmtPanel extends JPanel {

    private JTextField salaryIdField;
    private JComboBox<Employee> employeeComboBox;
    private JComboBox<String> monthComboBox;
    private JTextField yearField;
    private JTextField basicSalaryField;
    private JTextField hraField;
    private JTextField daField;
    private JTextField deductionsField;
    private JTextField netSalaryField;
    private JTextField paymentDateField;

    private JButton calculateButton;
    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable salaryTable;
    private DefaultTableModel tableModel;

    private SalaryDAO salaryDAO;
    private EmployeeDAO employeeDAO;

    public SalaryMgmtPanel() {
        this.salaryDAO = new SalaryDAO();
        this.employeeDAO = new EmployeeDAO();

        initializeUI();
        loadEmployees();
        loadSalaryTable();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Form on WEST
        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.setPreferredSize(new Dimension(340, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Salary Details"));

        JPanel fieldsPanel = new JPanel(new GridLayout(10, 2, 5, 6));

        salaryIdField = new JTextField();
        salaryIdField.setEditable(false);

        employeeComboBox = new JComboBox<>();
        String[] months = {"January", "February", "March", "April", "May", "June",
                           "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        yearField = new JTextField(String.valueOf(LocalDate.now().getYear()));
        basicSalaryField = new JTextField();
        hraField = new JTextField();
        daField = new JTextField();
        deductionsField = new JTextField();

        netSalaryField = new JTextField();
        netSalaryField.setEditable(false);

        paymentDateField = new JTextField(LocalDate.now().toString());

        fieldsPanel.add(new JLabel("Record ID:"));
        fieldsPanel.add(salaryIdField);
        fieldsPanel.add(new JLabel("Employee: *"));
        fieldsPanel.add(employeeComboBox);
        fieldsPanel.add(new JLabel("Month: *"));
        fieldsPanel.add(monthComboBox);
        fieldsPanel.add(new JLabel("Year: *"));
        fieldsPanel.add(yearField);
        fieldsPanel.add(new JLabel("Basic Salary: *"));
        fieldsPanel.add(basicSalaryField);
        fieldsPanel.add(new JLabel("HRA:"));
        fieldsPanel.add(hraField);
        fieldsPanel.add(new JLabel("DA:"));
        fieldsPanel.add(daField);
        fieldsPanel.add(new JLabel("Deductions:"));
        fieldsPanel.add(deductionsField);
        fieldsPanel.add(new JLabel("Net Salary:"));
        fieldsPanel.add(netSalaryField);
        fieldsPanel.add(new JLabel("Pay Date:"));
        fieldsPanel.add(paymentDateField);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        btnPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

        calculateButton = new JButton("Calculate Net");
        saveButton = new JButton("Save Record");
        updateButton = new JButton("Update Record");
        deleteButton = new JButton("Delete Record");
        clearButton = new JButton("Clear");

        btnPanel.add(calculateButton);
        btnPanel.add(saveButton);
        btnPanel.add(updateButton);
        btnPanel.add(deleteButton);
        btnPanel.add(clearButton);

        formPanel.add(btnPanel, BorderLayout.SOUTH);
        add(formPanel, BorderLayout.WEST);

        // Table on CENTER
        tableModel = new DefaultTableModel();
        salaryTable = new JTable(tableModel);
        add(new JScrollPane(salaryTable), BorderLayout.CENTER);

        // Listeners
        employeeComboBox.addActionListener(e -> onEmployeeSelected());
        calculateButton.addActionListener(e -> calculateNet());
        saveButton.addActionListener(e -> saveSalary());
        updateButton.addActionListener(e -> updateSalary());
        deleteButton.addActionListener(e -> deleteSalary());
        clearButton.addActionListener(e -> clearForm());

        salaryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salaryTable.getSelectedRow();
                if (row != -1) {
                    populateFromTable(row);
                }
            }
        });
    }

    public void loadEmployees() {
        try {
            employeeComboBox.removeAllItems();
            List<Employee> list = employeeDAO.getAll();
            for (Employee emp : list) {
                employeeComboBox.addItem(emp);
            }
            if (!list.isEmpty()) {
                onEmployeeSelected();
            }
        } catch (AppException e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }
    }

    private void onEmployeeSelected() {
        Employee emp = (Employee) employeeComboBox.getSelectedItem();
        if (emp != null) {
            double basic = emp.getBasicSalary();
            basicSalaryField.setText(String.format("%.2f", basic));
            hraField.setText(String.format("%.2f", SalaryCalculator.calculateHRA(basic)));
            daField.setText(String.format("%.2f", SalaryCalculator.calculateDA(basic)));
            deductionsField.setText(String.format("%.2f", SalaryCalculator.calculateDeductions(basic)));
            calculateNet();
        }
    }

    private void calculateNet() {
        try {
            double basic = Double.parseDouble(basicSalaryField.getText().trim());
            double hra = hraField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(hraField.getText().trim());
            double da = daField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(daField.getText().trim());
            double deductions = deductionsField.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(deductionsField.getText().trim());

            double net = SalaryCalculator.calculateNetSalary(basic, hra, da, deductions);
            netSalaryField.setText(String.format("%.2f", net));
        } catch (NumberFormatException e) {
            // Ignore format errors while typing
        }
    }

    public void loadSalaryTable() {
        try {
            List<Salary> list = salaryDAO.getAll();

            Vector<String> columns = new Vector<>();
            columns.add("ID");
            columns.add("Emp ID");
            columns.add("Employee Name");
            columns.add("Month");
            columns.add("Year");
            columns.add("Basic");
            columns.add("HRA");
            columns.add("DA");
            columns.add("Deductions");
            columns.add("Net Salary");
            columns.add("Pay Date");

            Vector<Vector<Object>> data = new Vector<>();
            for (Salary s : list) {
                Vector<Object> row = new Vector<>();
                row.add(s.getSalaryId());
                row.add(s.getEmpId());
                row.add(s.getEmpName());
                row.add(s.getMonth());
                row.add(s.getYear());
                row.add(String.format("%.2f", s.getBasicSalary()));
                row.add(String.format("%.2f", s.getHra()));
                row.add(String.format("%.2f", s.getDa()));
                row.add(String.format("%.2f", s.getDeductions()));
                row.add(String.format("%.2f", s.getNetSalary()));
                row.add(s.getPaymentDate());
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load salaries: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFromTable(int row) {
        salaryIdField.setText(tableModel.getValueAt(row, 0).toString());
        int empId = Integer.parseInt(tableModel.getValueAt(row, 1).toString());
        for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
            if (employeeComboBox.getItemAt(i).getEmpId() == empId) {
                employeeComboBox.setSelectedIndex(i);
                break;
            }
        }
        monthComboBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        yearField.setText(tableModel.getValueAt(row, 4).toString());
        basicSalaryField.setText(tableModel.getValueAt(row, 5).toString());
        hraField.setText(tableModel.getValueAt(row, 6).toString());
        daField.setText(tableModel.getValueAt(row, 7).toString());
        deductionsField.setText(tableModel.getValueAt(row, 8).toString());
        netSalaryField.setText(tableModel.getValueAt(row, 9).toString());
        paymentDateField.setText(tableModel.getValueAt(row, 10).toString());
    }

    private void saveSalary() {
        Employee emp = (Employee) employeeComboBox.getSelectedItem();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Select an employee.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        calculateNet();

        try {
            int year = Integer.parseInt(yearField.getText().trim());
            double basic = Double.parseDouble(basicSalaryField.getText().trim());
            double hra = Double.parseDouble(hraField.getText().trim());
            double da = Double.parseDouble(daField.getText().trim());
            double deductions = Double.parseDouble(deductionsField.getText().trim());
            double net = Double.parseDouble(netSalaryField.getText().trim());
            Date payDate = Date.valueOf(paymentDateField.getText().trim());

            Salary s = new Salary(emp.getEmpId(), (String) monthComboBox.getSelectedItem(), year, basic, hra, da, deductions, net, payDate);

            if (salaryDAO.add(s)) {
                JOptionPane.showMessageDialog(this, "Salary recorded!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSalaryTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSalary() {
        if (salaryIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record to update.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        calculateNet();

        try {
            int id = Integer.parseInt(salaryIdField.getText().trim());
            int year = Integer.parseInt(yearField.getText().trim());
            double basic = Double.parseDouble(basicSalaryField.getText().trim());
            double hra = Double.parseDouble(hraField.getText().trim());
            double da = Double.parseDouble(daField.getText().trim());
            double deductions = Double.parseDouble(deductionsField.getText().trim());
            double net = Double.parseDouble(netSalaryField.getText().trim());
            Date payDate = Date.valueOf(paymentDateField.getText().trim());

            Salary s = new Salary();
            s.setSalaryId(id);
            s.setMonth((String) monthComboBox.getSelectedItem());
            s.setYear(year);
            s.setBasicSalary(basic);
            s.setHra(hra);
            s.setDa(da);
            s.setDeductions(deductions);
            s.setNetSalary(net);
            s.setPaymentDate(payDate);

            if (salaryDAO.update(s)) {
                JOptionPane.showMessageDialog(this, "Salary updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSalaryTable();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSalary() {
        if (salaryIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this salary record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(salaryIdField.getText().trim());
                if (salaryDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadSalaryTable();
                }
            } catch (AppException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        salaryIdField.setText("");
        salaryTable.clearSelection();
        onEmployeeSelected();
    }
}
