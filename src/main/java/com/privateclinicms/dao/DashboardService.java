package com.privateclinicms.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;
import java.util.TreeMap;

public class DashboardService {
    private Connection conn;

    public DashboardService(Connection conn) {
        this.conn = conn;
    }

    public int getSoBenhNhanHomNay() throws SQLException {
        String sql = """
        SELECT COUNT(DISTINCT MaBenhNhan) FROM (
            SELECT MaBenhNhan FROM LichKham WHERE CAST(GioBatDau AS DATE) = CAST(GETDATE() AS DATE)
            UNION
            SELECT MaBenhNhan FROM BenhNhan WHERE CAST(NgayKham AS DATE) = CAST(GETDATE() AS DATE)
        ) AS Combined
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }


    public int getSoLichHenHomNay() throws SQLException {
        String sql = "SELECT COUNT(*) FROM LichKham WHERE CAST(GioBatDau AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public int getSoDonThuocHomNay() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ToaThuoc WHERE CAST(NgayLayThuoc AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public BigDecimal getRawDoanhThuHomNay() throws SQLException {
        String sql = "SELECT SUM(ct.ThanhTien) " +
                "FROM ChiTietToaThuoc ct " +
                "JOIN ToaThuoc tt ON ct.MaToaThuoc = tt.MaToaThuoc " +
                "WHERE CAST(tt.NgayLayThuoc AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String getFormattedDoanhThuHomNay() throws SQLException {
        BigDecimal raw = getRawDoanhThuHomNay();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat formatter = new DecimalFormat("#,###.##", symbols);

        return formatter.format(raw);
    }

    public Map<Integer, BigDecimal> getTongTienTheoNgayTrongThang(int thang, int nam) throws SQLException {
        Map<Integer, BigDecimal> map = new TreeMap<>();
        String sql = "SELECT DAY(tt.NgayLayThuoc) AS ngay, SUM(ct.ThanhTien) AS tong_tien " +
                "FROM ChiTietToaThuoc ct " +
                "JOIN ToaThuoc tt ON ct.MaToaThuoc = tt.MaToaThuoc " +
                "WHERE MONTH(tt.NgayLayThuoc) = ? AND YEAR(tt.NgayLayThuoc) = ? " +
                "GROUP BY DAY(tt.NgayLayThuoc) " +
                "ORDER BY ngay";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int ngay = rs.getInt("ngay");
                BigDecimal tongTien = rs.getBigDecimal("tong_tien");
                map.put(ngay, tongTien);
            }
        }
        return map;
    }
}

