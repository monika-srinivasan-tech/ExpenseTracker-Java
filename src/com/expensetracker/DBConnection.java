package com.expensetracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/expense_db";
    private static final String USER = "root"; // change if your MySQL has password
    private static final String PASS = "Monika15#";     // your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
