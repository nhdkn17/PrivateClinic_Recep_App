package com.privateclinicms.dao;

import com.privateclinicms.model.NhanVien;
import com.privateclinicms.util.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO implements DAO<NhanVien> {

    @Override
    public void add(NhanVien nhanVien) {
        String sql = "INSERT INTO NhanVien (HoTen, VaiTro, SoDienThoai, Email, MatKhau) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nhanVien.getHoTen());
            stmt.setString(2, nhanVien.getVaiTro());
            stmt.setString(3, nhanVien.getSoDienThoai());
            stmt.setString(4, nhanVien.getEmail());
            stmt.setString(5, nhanVien.getMatKhau());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public NhanVien getById(int id) {
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                NhanVien nhanVien = new NhanVien();
                nhanVien.setMaNhanVien(rs.getInt("MaNhanVien"));
                nhanVien.setHoTen(rs.getString("HoTen"));
                nhanVien.setVaiTro(rs.getString("VaiTro"));
                nhanVien.setSoDienThoai(rs.getString("SoDienThoai"));
                nhanVien.setEmail(rs.getString("Email"));
                nhanVien.setMatKhau(rs.getString("MatKhau"));
                return nhanVien;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<NhanVien> getAll() {
        List<NhanVien> nhanVienList = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                NhanVien nhanVien = new NhanVien();
                nhanVien.setMaNhanVien(rs.getInt("MaNhanVien"));
                nhanVien.setHoTen(rs.getString("HoTen"));
                nhanVien.setVaiTro(rs.getString("VaiTro"));
                nhanVien.setSoDienThoai(rs.getString("SoDienThoai"));
                nhanVien.setEmail(rs.getString("Email"));
                nhanVien.setMatKhau(rs.getString("MatKhau"));
                nhanVienList.add(nhanVien);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nhanVienList;
    }

    @Override
    public void update(NhanVien nhanVien) {
        String sql = "UPDATE NhanVien SET HoTen = ?, VaiTro = ?, SoDienThoai = ?, Email = ?, MatKhau = ? WHERE MaNhanVien = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nhanVien.getHoTen());
            stmt.setString(2, nhanVien.getVaiTro());
            stmt.setString(3, nhanVien.getSoDienThoai());
            stmt.setString(4, nhanVien.getEmail());
            stmt.setString(5, nhanVien.getMatKhau());
            stmt.setInt(6, nhanVien.getMaNhanVien());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
