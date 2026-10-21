package com.ems.ui.employee;

import com.ems.dao.AttendanceDAO;
import com.ems.exception.AppException;
import com.ems.model.Attendance;
import com.ems.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP My Attendance Panel.
 */
public class MyAttendancePanel extends JPanel {

    private Employee employee;
    private AttendanceDAO attendanceDAO;

    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public MyAttendancePanel(Employee employee) {
        this.employee = employee;
        this.attendanceDAO = new AttendanceDAO();

        initializeUI();
        loadAttendanceData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAttendanceData());
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        attendanceTable = new JTable(tableModel);
        add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
    }

    public void loadAttendanceData() {
        try {
            List<Attendance> list = attendanceDAO.getByEmployee(employee.getEmpId());

            Vector<String> columns = new Vector<>();
            columns.add("Date");
            columns.add("Status");
            columns.add("Notes");

            Vector<Vector<Object>> data = new Vector<>();
            for (Attendance a : list) {
                Vector<Object> row = new Vector<>();
                row.add(a.getDate());
                row.add(a.getStatus());
                row.add(a.getNotes() != null ? a.getNotes() : "");
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
