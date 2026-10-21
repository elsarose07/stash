package com.ems.model;

/**
 * Superclass representing a generic system user.
 * Demonstrates:
 *  - Module 1: Data Abstraction, Encapsulation, Access Modifiers, 'this' keyword
 *  - Module 2: Inheritance (Super Class), Protected Members, Constructor Chaining, Final Variables
 */
public abstract class User {
    // Final constants for user roles (Module 2: Final Variables)
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";

    // Protected members accessible by subclasses (Module 2: protected Members)
    protected int userId;
    protected String username;
    protected String password;
    protected String role;
    protected Integer empId; // Nullable for admin, holds employee ID for employee users

    // Default Constructor (Module 1: Constructors)
    public User() {
        this.userId = 0;
        this.username = "";
        this.password = "";
        this.role = "";
        this.empId = null;
    }

    // Parameterized Constructor using 'this' keyword (Module 1: this keyword)
    public User(int userId, String username, String password, String role, Integer empId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.empId = empId;
    }

    // Abstract method to be overridden by subclasses (Module 2: Dynamic Method Dispatch)
    public abstract String getDashboardWelcome();

    // Final method: cannot be overridden by subclasses (Module 2: Using final with Inheritance)
    public final boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(this.role);
    }

    // Getters and Setters (Module 1: Encapsulation)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", role=" + role + "]";
    }
}
