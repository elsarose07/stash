package com.ems.ui.admin;

import com.ems.dao.LeaveDAO;
import com.ems.exception.AppException;
import com.ems.model.LeaveRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * Simple MVP Leave Management Panel.
 */
public class LeaveMgmtPanel extends JPanel {

    private JTable leaveTable;
    private DefaultTableModel tableModel;

    private JButton approveButton;
    private JButton rejectButton;
    private JButton refreshButton;

    private LeaveDAO leaveDAO;

    public LeaveMgmtPanel() {
        this.leaveDAO = new LeaveDAO();

        initializeUI();
        loadLeaveTable();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Action Bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        approveButton = new JButton("Approve Selected");
        rejectButton = new JButton("Reject Selected");
        refreshButton = new JButton("Refresh");

        topPanel.add(approveButton);
        topPanel.add(rejectButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Table
        tableModel = new DefaultTableModel();
        leaveTable = new JTable(tableModel);
        leaveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(leaveTable), BorderLayout.CENTER);

        // Action Listeners
        approveButton.addActionListener(e -> updateSelectedStatus("Approved"));
        rejectButton.addActionListener(e -> updateSelectedStatus("Rejected"));
        refreshButton.addActionListener(e -> loadLeaveTable());
    }

    public void loadLeaveTable() {
        try {
            List<LeaveRequest> list = leaveDAO.getAll();

            Vector<String> columns = new Vector<>();
            columns.add("Leave ID");
            columns.add("Emp ID");
            columns.add("Employee Name");
            columns.add("Leave Type");
            columns.add("Start Date");
            columns.add("End Date");
            columns.add("Reason");
            columns.add("Status");
            columns.add("Applied On");

            Vector<Vector<Object>> data = new Vector<>();
            for (LeaveRequest req : list) {
                Vector<Object> row = new Vector<>();
                row.add(req.getLeaveId());
                row.add(req.getEmpId());
                row.add(req.getEmpName());
                row.add(req.getLeaveType());
                row.add(req.getStartDate());
                row.add(req.getEndDate());
                row.add(req.getReason());
                row.add(req.getStatus());
                row.add(req.getAppliedOn());
                data.add(row);
            }

            tableModel.setDataVector(data, columns);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Failed to load leave requests: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedStatus(String newStatus) {
        int row = leaveTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a leave request from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int leaveId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        try {
            if (leaveDAO.updateStatus(leaveId, newStatus)) {
                JOptionPane.showMessageDialog(this, "Leave request marked as " + newStatus + "!", "Updated", JOptionPane.INFORMATION_MESSAGE);
                loadLeaveTable();
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
