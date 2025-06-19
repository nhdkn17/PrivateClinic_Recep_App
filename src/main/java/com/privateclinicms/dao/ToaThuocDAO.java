package com.privateclinicms.dao;

import com.privateclinicms.model.ToaThuoc;
import com.privateclinicms.util.JDBCUtil;

import java.sql.*;

public class ToaThuocDAO {
    public int add(ToaThuoc toaThuoc) {
        String sql = "INSERT INTO ToaThuoc (MaBenhNhan, NgayLayThuoc) VALUES (?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, toaThuoc.getMaBenhNhan());
            stmt.setDate(2, Date.valueOf(toaThuoc.getNgayLayThuoc()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
