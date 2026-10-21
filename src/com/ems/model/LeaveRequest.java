package com.ems.model;

import java.sql.Date;

/**
 * Model class representing a Leave Request.
 * Demonstrates: Encapsulation, State representation.
 */
public class LeaveRequest {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";

    private int leaveId;
    private int empId;
    private String empName;
    private Date startDate;
    private Date endDate;
    private String leaveType; // Casual Leave, Sick Leave, Annual Leave
    private String reason;
    private String status;    // Pending, Approved, Rejected
    private Date appliedOn;

    public LeaveRequest() {
        this.status = STATUS_PENDING;
    }

    public LeaveRequest(int leaveId, int empId, String empName, Date startDate, Date endDate,
                        String leaveType, String reason, String status, Date appliedOn) {
        this.leaveId = leaveId;
        this.empId = empId;
        this.empName = empName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.reason = reason;
        this.status = status;
        this.appliedOn = appliedOn;
    }

    public LeaveRequest(int empId, Date startDate, Date endDate, String leaveType, String reason, Date appliedOn) {
        this.empId = empId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.reason = reason;
        this.status = STATUS_PENDING;
        this.appliedOn = appliedOn;
    }

    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(Date appliedOn) {
        this.appliedOn = appliedOn;
    }
}
