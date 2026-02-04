package com.expensetracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ExpenseTracker {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n=== Expense Tracker ===");
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. Total Expenses");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch(choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    viewExpenses();
                    break;
                case 3:
                    totalExpenses();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice, try again!");
            }

        } while(choice != 4);
    }

    private static void addExpense() {
        System.out.print("Enter expense title: ");
        String title = sc.nextLine();

        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine(); // consume newline

        System.out.print("Enter category: ");
        String category = sc.nextLine();

        System.out.print("Enter date (DD-MM-YYYY): ");
        String dateInput = sc.nextLine();

        // Convert date to YYYY-MM-DD format for MySQL
        String formattedDate = null;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(dateInput);
            formattedDate = dbFormat.format(date);
        } catch (ParseException e) {
            System.out.println("Invalid date format! Using current date instead.");
            formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO expenses(title, amount, category, date) VALUES(?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, title);
            pst.setDouble(2, amount);
            pst.setString(3, category);
            pst.setString(4, formattedDate);
            pst.executeUpdate();
            System.out.println("Expense added successfully!");
        } catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewExpenses() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM expenses ORDER BY id";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.println("\n--- All Expenses ---");
            System.out.printf("%-5s %-20s %-10s %-15s %-12s\n", "ID", "Title", "Amount", "Category", "Date");
            System.out.println("-------------------------------------------------------------");
            while(rs.next()) {
                System.out.printf("%-5d %-20s ₹%-10.2f %-15s %-12s\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getDate("date"));
            }
        } catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void totalExpenses() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT SUM(amount) AS total FROM expenses";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if(rs.next()) {
                System.out.println("Total Expenses: ₹" + rs.getDouble("total"));
            }
        } catch(SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
