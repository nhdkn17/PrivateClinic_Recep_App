package com.privateclinicms.shared.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCUtil {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            String serverName = "LAPTOP-CHNR6RQ3";
            String login = "sa";
            String password = "123";
            String databaseName = "ClinicManagementServer";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://" + serverName + ":1433;databaseName=" + databaseName + ";encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8";

            connection = DriverManager.getConnection(url, login, password);
        } catch (Exception e) {
            System.err.println("Lỗi kết nối DB:");
            e.printStackTrace();
        }

        return connection;
    }
    public static void closeConnection(Connection connection) {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
