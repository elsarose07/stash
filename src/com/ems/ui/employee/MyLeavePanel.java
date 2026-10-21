package com.ems.ui.employee;

import com.ems.dao.LeaveDAO;
import com.ems.exception.AppException;
import com.ems.model.Employee;
import com.ems.model.LeaveRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP My Leave Panel using standard Swing components.
 */
public class MyLeavePanel extends JPanel {

    private Employee employee;
    private LeaveDAO leaveDAO;

    private JComboBox<String> leaveTypeComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField reasonField;
    private JButton submitButton;

    private JTable leaveTable;
    private DefaultTableModel tableModel;

    public MyLeavePanel(Employee employee) {
        this.employee = employee;
        this.leaveDAO = new LeaveDAO();

        initializeUI();
        loadLeaveHistory();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Form Panel on WEST
        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Apply for Leave"));

        JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 5, 8));
        String[] types = {"Casual Leave", "Sick Leave", "Annual Leave"};
        leaveTypeComboBox = new JComboBox<>(types);
        startDateField = new JTextField(LocalDate.now().plusDays(1).toString());
        endDateField = new JTextField(LocalDate.now().plusDays(2).toString());
        reasonField = new JTextField();

        fieldsPanel.add(new JLabel("Leave Type: *"));
        fieldsPanel.add(leaveTypeComboBox);
        fieldsPanel.add(new JLabel("Start Date: *"));
        fieldsPanel.add(startDateField);
        fieldsPanel.add(new JLabel("End Date: *"));
        fieldsPanel.add(endDateField);
        fieldsPanel.add(new JLabel("Reason: *"));
        fieldsPanel.add(reasonField);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        submitButton = new JButton("Submit Leave Request");
        submitButton.addActionListener(e -> submitLeave());
        formPanel.add(submitButton, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.WEST);

        // History Table on CENTER
        tableModel = new DefaultTableModel();
        leaveTable = new JTable(tableModel);
        add(new JScrollPane(leaveTable), BorderLayout.CENTER);
    }

    public void loadLeaveHistory() {
        try {
            List<LeaveRequest> list = leaveDAO.getByEmployee(employee.getEmpId());

            Vector<String> columns = new Vector<>();
            columns.add("ID");
            columns.add("Type");
            columns.add("Start Date");
            columns.add("End Date");
            columns.add("Reason");
            columns.add("Status");
            columns.add("Applied On");

            Vector<Vector<Object>> data = new Vector<>();
            for (LeaveRequest r : list) {
                Vector<Object> row = new Vector<>();
                row.add(r.getLeaveId());
                row.add(r.getLeaveType());
                row.add(r.getStartDate());
                row.add(r.getEndDate());
                row.add(r.getReason());
                row.add(r.getStatus());
                row.add(r.getAppliedOn());
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load leave history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitLeave() {
        String reason = reasonField.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reason.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date startDate = Date.valueOf(startDateField.getText().trim());
            Date endDate = Date.valueOf(endDateField.getText().trim());

            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String type = (String) leaveTypeComboBox.getSelectedItem();
            Date today = Date.valueOf(LocalDate.now());

            LeaveRequest req = new LeaveRequest(employee.getEmpId(), startDate, endDate, type, reason, today);
            if (leaveDAO.add(req)) {
                JOptionPane.showMessageDialog(this, "Leave application submitted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                reasonField.setText("");
                loadLeaveHistory();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Date must be in format YYYY-MM-DD", "Notice", JOptionPane.WARNING_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
