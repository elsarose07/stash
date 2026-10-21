package com.ems.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

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

    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "ems_db";
    private static String user = "root";
    private static String password = "Nexus@123";

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

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(getUrl(dbName), user, password);
            ensureTablesExist(conn);
            return conn;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) { // Database doesn't exist
                tryAutoInitDatabase();
                Connection conn = DriverManager.getConnection(getUrl(dbName), user, password);
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
        try (Connection conn = DriverManager.getConnection(getUrl(""), user, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (Exception ignored) {}

        try (Connection conn = DriverManager.getConnection(getUrl(dbName), user, password)) {
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
