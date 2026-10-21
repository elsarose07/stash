package com.ems.model;

import java.sql.Date;

/**
 * Subclass representing an Employee user with organizational details.
 * Demonstrates:
 *  - Module 1: Encapsulation, Primitive types, Strings, Access Modifiers
 *  - Module 2: Sub Class, 'super' keyword, Method Overriding, Calling Order of Constructors
 */
public class Employee extends User {
    private int empId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int deptId;
    private String deptName; // Joined from departments table for display
    private String designation;
    private double basicSalary;
    private Date joinDate;

    // Default Constructor
    public Employee() {
        super();
        this.role = ROLE_EMPLOYEE;
    }

    // Constructor with full employee profile (Module 2: The super keyword, calling order)
    public Employee(int userId, String username, String password, int empId,
                    String firstName, String lastName, String email, String phone,
                    int deptId, String designation, double basicSalary, Date joinDate) {
        super(userId, username, password, ROLE_EMPLOYEE, empId);
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.deptId = deptId;
        this.designation = designation;
        this.basicSalary = basicSalary;
        this.joinDate = joinDate;
    }

    // Simplified constructor for employee directory CRUD
    public Employee(int empId, String firstName, String lastName, String email, String phone,
                    int deptId, String designation, double basicSalary, Date joinDate) {
        super(0, "", "", ROLE_EMPLOYEE, empId);
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.deptId = deptId;
        this.designation = designation;
        this.basicSalary = basicSalary;
        this.joinDate = joinDate;
    }

    // Overriding abstract method from User superclass (Module 2: Method Overriding)
    @Override
    public String getDashboardWelcome() {
        return "Welcome, " + getFullName() + " (Employee ID: " + empId + ")";
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    // Getters and Setters
    @Override
    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
        super.setEmpId(empId);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName != null ? deptName : "Unassigned";
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    @Override
    public String toString() {
        return empId + " - " + getFullName() + " (" + designation + ")";
    }
}
