package com.ems.exception;

/**
 * Custom checked exception for application-level error handling.
 * Demonstrates:
 *  - Module 3: Checked Exceptions, Custom Exceptions, super keyword in exceptions
 */
public class AppException extends Exception {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
