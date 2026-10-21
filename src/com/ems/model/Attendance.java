package com.ems.model;

import java.sql.Date;

/**
 * Model class representing an Attendance record.
 * Demonstrates: Encapsulation, Date handling, and naming conventions.
 */
public class Attendance {
    private int attendanceId;
    private int empId;
    private String empName; // Joined from employees table
    private Date date;
    private String status;  // Present, Absent, Half-Day, On-Leave
    private String notes;

    public Attendance() {}

    public Attendance(int attendanceId, int empId, String empName, Date date, String status, String notes) {
        this.attendanceId = attendanceId;
        this.empId = empId;
        this.empName = empName;
        this.date = date;
        this.status = status;
        this.notes = notes;
    }

    public Attendance(int empId, Date date, String status, String notes) {
        this.empId = empId;
        this.date = date;
        this.status = status;
        this.notes = notes;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
