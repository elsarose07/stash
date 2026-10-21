package com.ems.main;

import com.ems.ui.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Main application entry point for the Employee Management System.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            }
        });
    }
}
