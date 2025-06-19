package com.privateclinicms.util;

import java.sql.Connection;

import static com.privateclinicms.util.JDBCUtil.getConnection;

public class TestConnection {
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Kết nối thành công!");
        }
    }
}
