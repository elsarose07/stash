package com.ems.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

/**
 * Database Connection Manager using JDBC.
 * Demonstrates:
 *  - Module 4: Developing Database Applications using JDBC
 *  - Module 4: Connection Establishment, Common JDBC Components
 *  - Module 3: Exception Handling (try, catch, finally, checked exceptions)
 *  - Module 2: Static Members and Final Variables
 */
public class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Common passwords in student/dev environments for automatic fallback
    private static final String[] CANDIDATE_PASSWORDS = {
        "", "Nexus@123", "root", "1234", "admin", "password", "123456", "mysql"
    };

    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "ems_db";
    private static String user = "root";
    private static String password = "";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
        loadConfiguration();
    }

    private DBConnection() {}

    public static void loadConfiguration() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
                host = props.getProperty("db.host", host);
                port = props.getProperty("db.port", port);
                dbName = props.getProperty("db.name", dbName);
                user = props.getProperty("db.user", user);
                password = props.getProperty("db.password", password);
            } catch (IOException e) {
                System.err.println("Notice: Could not read " + CONFIG_FILE + ", using preset credentials.");
            }
        }
    }

    private static String getUrl(String targetDb) {
        String base = "jdbc:mysql://" + host + ":" + port;
        if (targetDb != null && !targetDb.isEmpty()) {
            base += "/" + targetDb;
        }
        return base + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    /**
     * Establishes a raw connection to MySQL. If access is denied (error 1045),
     * it automatically tests candidate passwords to find the working one.
     * If all candidates fail, it prompts the user with a GUI dialog to enter their password.
     */
    private static Connection openRawConnection(String targetDb) throws SQLException {
        try {
            return DriverManager.getConnection(getUrl(targetDb), user, password);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1045 || "28000".equals(e.getSQLState())) {
                for (String candidate : CANDIDATE_PASSWORDS) {
                    if (candidate.equals(password)) {
                        continue;
                    }
                    try {
                        Connection conn = DriverManager.getConnection(getUrl(targetDb), user, candidate);
                        password = candidate; // Cache successful password for all subsequent calls
                        saveConfiguration(); // Persist so next launch connects instantly
                        return conn;
                    } catch (SQLException ignored) {
                        // Continue trying next candidate
                    }
                }

                // If all candidate passwords fail, prompt the user via dialog
                String prompted = promptForPassword();
                if (prompted != null) {
                    try {
                        Connection conn = DriverManager.getConnection(getUrl(targetDb), user, prompted);
                        password = prompted;
                        saveConfiguration(); // Persist to db.properties
                        return conn;
                    } catch (SQLException ex) {
                        throw new SQLException("Access denied with the entered password. Please verify your MySQL credentials.", ex.getSQLState(), ex.getErrorCode());
                    }
                }
            }
            throw e;
        }
    }

    /**
     * Displays a graphical password dialog if standard credentials fail.
     */
    private static String promptForPassword() {
        if (GraphicsEnvironment.isHeadless()) {
            return null;
        }

        final JPasswordField passwordField = new JPasswordField(20);
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        JLabel label = new JLabel("<html><b>MySQL Database Authentication</b><br><br>" +
            "Could not connect to MySQL using standard passwords.<br>" +
            "Please enter your local MySQL <b>" + user + "</b> password:</html>");
        panel.add(label, BorderLayout.NORTH);
        panel.add(passwordField, BorderLayout.CENTER);

        final int[] result = new int[1];
        if (SwingUtilities.isEventDispatchThread()) {
            result[0] = JOptionPane.showConfirmDialog(
                null, panel, "MySQL Authentication Required",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    result[0] = JOptionPane.showConfirmDialog(
                        null, panel, "MySQL Authentication Required",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                });
            } catch (Exception ex) {
                return null;
            }
        }

        if (result[0] == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        }
        return null;
    }

    /**
     * Saves the current database configuration to db.properties.
     */
    public static void saveConfiguration() {
        File file = new File(CONFIG_FILE);
        Properties props = new Properties();
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException ignored) {}
        }
        props.setProperty("db.host", host);
        props.setProperty("db.port", port);
        props.setProperty("db.name", dbName);
        props.setProperty("db.user", user);
        props.setProperty("db.password", password);

        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "Database Configuration for Employee Management System");
        } catch (IOException e) {
            System.err.println("Notice: Could not save " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = openRawConnection(dbName);
            ensureTablesExist(conn);
            return conn;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) { // Database doesn't exist
                tryAutoInitDatabase();
                Connection conn = openRawConnection(dbName);
                ensureTablesExist(conn);
                return conn;
            }
            throw e;
        }
    }

    private static void ensureTablesExist(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 FROM users LIMIT 1")) {
            // Table exists
        } catch (SQLException e) {
            runSchemaScript(conn);
        }
    }

    private static void tryAutoInitDatabase() {
        try (Connection conn = openRawConnection("");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (Exception ignored) {}

        try (Connection conn = openRawConnection(dbName)) {
            runSchemaScript(conn);
        } catch (Exception ignored) {}
    }

    public static void runSchemaScript(Connection conn) {
        String[] candidatePaths = {
            "database.sql",
            "d:/stash/database.sql",
            "../database.sql",
            System.getProperty("user.dir") + File.separator + "database.sql"
        };

        File foundFile = null;
        for (String path : candidatePaths) {
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                foundFile = f;
                break;
            }
        }

        if (foundFile == null) {
            return;
        }

        try {
            String script = new String(Files.readAllBytes(foundFile.toPath()));
            script = script.replaceAll("/\\*[\\s\\S]*?\\*/", "");

            StringBuilder sb = new StringBuilder();
            for (String line : script.split("\\r?\\n")) {
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("--")) {
                    continue;
                }
                int commentIdx = line.indexOf("--");
                if (commentIdx != -1) {
                    line = line.substring(0, commentIdx);
                }
                sb.append(line).append("\n");
            }

            String[] statements = sb.toString().split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty() &&
                        !trimmed.toLowerCase().startsWith("create database") &&
                        !trimmed.toLowerCase().startsWith("use ")) {
                        try {
                            stmt.execute(trimmed);
                        } catch (SQLException ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}
