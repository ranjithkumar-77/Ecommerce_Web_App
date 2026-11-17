package com.ecommerce.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_URL = setting("DB_URL");
    private static final String DB_USER = setting("DB_USER");
    private static final String DB_PASS = setting("DB_PASSWORD");

    private static String setting(String name) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? null : value;
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (DB_URL == null || DB_USER == null || DB_PASS == null) {
            throw new SQLException("Set DB_URL, DB_USER, and DB_PASSWORD in the Tomcat process environment.");
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
