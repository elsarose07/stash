package com.ems.ui.employee;

import com.ems.dao.SalaryDAO;
import com.ems.exception.AppException;
import com.ems.model.Employee;
import com.ems.model.Salary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP My Salary Panel.
 */
public class MySalaryPanel extends JPanel {

    private Employee employee;
    private SalaryDAO salaryDAO;

    private JLabel latestSlipLabel;
    private JTable salaryTable;
    private DefaultTableModel tableModel;

    public MySalaryPanel(Employee employee) {
        this.employee = employee;
        this.salaryDAO = new SalaryDAO();

        initializeUI();
        loadSalaryData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Summary
        JPanel topBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBox.setBorder(BorderFactory.createTitledBorder("Latest Pay Slip"));
        latestSlipLabel = new JLabel("Loading...");
        latestSlipLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        topBox.add(latestSlipLabel);

        add(topBox, BorderLayout.NORTH);

        // Center Table
        tableModel = new DefaultTableModel();
        salaryTable = new JTable(tableModel);
        add(new JScrollPane(salaryTable), BorderLayout.CENTER);
    }

    public void loadSalaryData() {
        try {
            Salary latest = salaryDAO.getLatestSalaryByEmployee(employee.getEmpId());
            if (latest != null) {
                latestSlipLabel.setText(String.format("Month: %s %d | Basic: Rs. %.2f | HRA: Rs. %.2f | DA: Rs. %.2f | Deductions: Rs. %.2f | Net Salary: Rs. %.2f",
                    latest.getMonth(), latest.getYear(), latest.getBasicSalary(), latest.getHra(), latest.getDa(), latest.getDeductions(), latest.getNetSalary()));
            } else {
                latestSlipLabel.setText("No salary records generated yet. Base Pay: Rs. " + String.format("%.2f", employee.getBasicSalary()));
            }

            List<Salary> list = salaryDAO.getByEmployee(employee.getEmpId());
            Vector<String> columns = new Vector<>();
            columns.add("Salary ID");
            columns.add("Month");
            columns.add("Year");
            columns.add("Basic Pay");
            columns.add("HRA");
            columns.add("DA");
            columns.add("Deductions");
            columns.add("Net Salary");
            columns.add("Pay Date");

            Vector<Vector<Object>> data = new Vector<>();
            for (Salary s : list) {
                Vector<Object> row = new Vector<>();
                row.add(s.getSalaryId());
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
}
