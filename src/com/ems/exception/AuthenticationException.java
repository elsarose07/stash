package com.ems.exception;

/**
 * Custom exception thrown when user authentication fails.
 * Demonstrates: Module 3: Exception hierarchy and Custom Exceptions
 */
public class AuthenticationException extends AppException {

    public AuthenticationException(String message) {
        super(message);
    }
}
