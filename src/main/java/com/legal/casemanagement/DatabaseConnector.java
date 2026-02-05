package com.legal.casemanagement;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/legal_case_management?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "Parth123";

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            Connection con = DriverManager.getConnection(url, user, password);

            System.out.println("✅ Database Connected Successfully!");

            con.close();

        } catch (Exception e) {
            System.out.println("❌ Connection Failed!");
            e.printStackTrace();
        }
    }
}
