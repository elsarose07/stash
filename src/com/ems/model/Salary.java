package com.ems.model;

import java.sql.Date;

/**
 * Model class representing an Employee Salary record.
 * Demonstrates: Encapsulation, Arithmetic calculations on primitive data types.
 */
public class Salary {
    private int salaryId;
    private int empId;
    private String empName;
    private String month;
    private int year;
    private double basicSalary;
    private double hra;
    private double da;
    private double deductions;
    private double netSalary;
    private Date paymentDate;

    public Salary() {}

    public Salary(int salaryId, int empId, String empName, String month, int year,
                  double basicSalary, double hra, double da, double deductions,
                  double netSalary, Date paymentDate) {
        this.salaryId = salaryId;
        this.empId = empId;
        this.empName = empName;
        this.month = month;
        this.year = year;
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.deductions = deductions;
        this.netSalary = netSalary;
        this.paymentDate = paymentDate;
    }

    public Salary(int empId, String month, int year, double basicSalary,
                  double hra, double da, double deductions, double netSalary, Date paymentDate) {
        this.empId = empId;
        this.month = month;
        this.year = year;
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.deductions = deductions;
        this.netSalary = netSalary;
        this.paymentDate = paymentDate;
    }

    public int getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(int salaryId) {
        this.salaryId = salaryId;
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public double getHra() {
        return hra;
    }

    public void setHra(double hra) {
        this.hra = hra;
    }

    public double getDa() {
        return da;
    }

    public void setDa(double da) {
        this.da = da;
    }

    public double getDeductions() {
        return deductions;
    }

    public void setDeductions(double deductions) {
        this.deductions = deductions;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
