package com.ems.util;

/**
 * Utility class for salary calculations.
 * Demonstrates:
 *  - Module 1: Arithmetic Operators, Precedence, Primitive Types
 *  - Module 2: Static Members, Method Overloading (Polymorphism)
 */
public class SalaryCalculator {

    // Default percentage constants
    public static final double DEFAULT_HRA_PERCENT = 0.20; // 20% HRA
    public static final double DEFAULT_DA_PERCENT = 0.10;  // 10% DA
    public static final double DEFAULT_DEDUCTION_PERCENT = 0.05; // 5% PF/Tax

    // Private constructor to prevent instantiation (Utility class pattern)
    private SalaryCalculator() {}

    /**
     * Overloaded Method 1: Calculates net salary given explicit allowances and deductions.
     * Formula: Net Salary = Basic Salary + HRA + DA - Deductions
     */
    public static double calculateNetSalary(double basicSalary, double hra, double da, double deductions) {
        if (basicSalary < 0) {
            throw new IllegalArgumentException("Basic salary cannot be negative.");
        }
        return (basicSalary + hra + da) - deductions;
    }

    /**
     * Overloaded Method 2: Calculates net salary using standard predefined percentages.
     * Demonstrates Method Overloading (Module 2: Polymorphism)
     */
    public static double calculateNetSalary(double basicSalary) {
        double hra = calculateHRA(basicSalary);
        double da = calculateDA(basicSalary);
        double deductions = calculateDeductions(basicSalary);
        return calculateNetSalary(basicSalary, hra, da, deductions);
    }

    public static double calculateHRA(double basicSalary) {
        return basicSalary * DEFAULT_HRA_PERCENT;
    }

    public static double calculateDA(double basicSalary) {
        return basicSalary * DEFAULT_DA_PERCENT;
    }

    public static double calculateDeductions(double basicSalary) {
        return basicSalary * DEFAULT_DEDUCTION_PERCENT;
    }
}
