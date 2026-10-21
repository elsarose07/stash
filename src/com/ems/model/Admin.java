package com.ems.model;

/**
 * Subclass representing an Administrator user.
 * Demonstrates:
 *  - Module 2: Sub Class, 'super' keyword, Method Overriding, Dynamic Method Dispatch
 */
public class Admin extends User {

    public Admin() {
        super();
        this.role = ROLE_ADMIN;
    }

    public Admin(int userId, String username, String password) {
        // Calling superclass constructor using 'super' keyword (Module 2: The super keyword)
        super(userId, username, password, ROLE_ADMIN, null);
    }

    // Overriding abstract method from User superclass (Module 2: Method Overriding)
    @Override
    public String getDashboardWelcome() {
        return "Welcome, Administrator: " + this.username;
    }
}
