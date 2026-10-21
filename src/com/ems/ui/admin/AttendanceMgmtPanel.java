package com.ems.ui.admin;

import com.ems.dao.AttendanceDAO;
import com.ems.dao.EmployeeDAO;
import com.ems.exception.AppException;
import com.ems.model.Attendance;
import com.ems.model.Employee;

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
 * Simple MVP Attendance Management Panel using standard Swing components.
 */
public class AttendanceMgmtPanel extends JPanel {

    private JTextField attendanceIdField;
    private JComboBox<Employee> employeeComboBox;
    private JTextField dateField;
    private JComboBox<String> statusComboBox;
    private JTextField notesField;

    private JButton markButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    private AttendanceDAO attendanceDAO;
    private EmployeeDAO employeeDAO;

    public AttendanceMgmtPanel() {
        this.attendanceDAO = new AttendanceDAO();
        this.employeeDAO = new EmployeeDAO();

        initializeUI();
        loadEmployees();
        loadAttendanceTable();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Form on WEST
        JPanel formPanel = new JPanel(new BorderLayout(5, 5));
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Mark / Update Attendance"));

        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 5, 8));
        attendanceIdField = new JTextField();
        attendanceIdField.setEditable(false);

        employeeComboBox = new JComboBox<>();
        dateField = new JTextField(LocalDate.now().toString());

        String[] statuses = {"Present", "Absent", "Half-Day", "On-Leave"};
        statusComboBox = new JComboBox<>(statuses);
        notesField = new JTextField();

        fieldsPanel.add(new JLabel("Record ID:"));
        fieldsPanel.add(attendanceIdField);
        fieldsPanel.add(new JLabel("Employee: *"));
        fieldsPanel.add(employeeComboBox);
        fieldsPanel.add(new JLabel("Date (YYYY-MM-DD): *"));
        fieldsPanel.add(dateField);
        fieldsPanel.add(new JLabel("Status: *"));
        fieldsPanel.add(statusComboBox);
        fieldsPanel.add(new JLabel("Notes:"));
        fieldsPanel.add(notesField);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        markButton = new JButton("Mark / Save");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        btnPanel.add(markButton);
        btnPanel.add(updateButton);
        btnPanel.add(deleteButton);
        btnPanel.add(refreshButton);

        formPanel.add(btnPanel, BorderLayout.SOUTH);
        add(formPanel, BorderLayout.WEST);

        // Table on CENTER
        tableModel = new DefaultTableModel();
        attendanceTable = new JTable(tableModel);
        add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        // Action Listeners
        markButton.addActionListener(e -> markAttendance());
        updateButton.addActionListener(e -> updateAttendance());
        deleteButton.addActionListener(e -> deleteAttendance());
        refreshButton.addActionListener(e -> {
            clearForm();
            loadAttendanceTable();
        });

        attendanceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = attendanceTable.getSelectedRow();
                if (row != -1) {
                    attendanceIdField.setText(tableModel.getValueAt(row, 0).toString());
                    int empId = Integer.parseInt(tableModel.getValueAt(row, 1).toString());
                    for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
                        if (employeeComboBox.getItemAt(i).getEmpId() == empId) {
                            employeeComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                    dateField.setText(tableModel.getValueAt(row, 3).toString());
                    statusComboBox.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                    Object notes = tableModel.getValueAt(row, 5);
                    notesField.setText(notes != null ? notes.toString() : "");
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
        } catch (AppException e) {
            System.err.println("Error loading employees for attendance: " + e.getMessage());
        }
    }

    public void loadAttendanceTable() {
        try {
            List<Attendance> list = attendanceDAO.getAll();

            Vector<String> columns = new Vector<>();
            columns.add("ID");
            columns.add("Emp ID");
            columns.add("Employee Name");
            columns.add("Date");
            columns.add("Status");
            columns.add("Notes");

            Vector<Vector<Object>> data = new Vector<>();
            for (Attendance a : list) {
                Vector<Object> row = new Vector<>();
                row.add(a.getAttendanceId());
                row.add(a.getEmpId());
                row.add(a.getEmpName());
                row.add(a.getDate());
                row.add(a.getStatus());
                row.add(a.getNotes());
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAttendance() {
        Employee emp = (Employee) employeeComboBox.getSelectedItem();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Select an employee.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date date = Date.valueOf(dateField.getText().trim());
            String status = (String) statusComboBox.getSelectedItem();
            String notes = notesField.getText().trim();

            Attendance att = new Attendance(emp.getEmpId(), date, status, notes);
            if (attendanceDAO.markAttendance(att)) {
                JOptionPane.showMessageDialog(this, "Attendance recorded!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAttendanceTable();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Date must be in format YYYY-MM-DD", "Notice", JOptionPane.WARNING_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAttendance() {
        if (attendanceIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an entry from the table to update.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(attendanceIdField.getText().trim());
            String status = (String) statusComboBox.getSelectedItem();
            String notes = notesField.getText().trim();

            Attendance att = new Attendance();
            att.setAttendanceId(id);
            att.setStatus(status);
            att.setNotes(notes);

            if (attendanceDAO.update(att)) {
                JOptionPane.showMessageDialog(this, "Attendance updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAttendanceTable();
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAttendance() {
        if (attendanceIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an entry to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(attendanceIdField.getText().trim());
                if (attendanceDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadAttendanceTable();
                }
            } catch (AppException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        attendanceIdField.setText("");
        dateField.setText(LocalDate.now().toString());
        statusComboBox.setSelectedIndex(0);
        notesField.setText("");
        attendanceTable.clearSelection();
    }
}
