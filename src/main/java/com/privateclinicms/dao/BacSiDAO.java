package com.privateclinicms.dao;

import com.privateclinicms.model.BacSi;
import com.privateclinicms.util.JDBCUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BacSiDAO implements DAO<BacSi> {

    @Override
    public void add(BacSi bacSi) {
        String sql = "INSERT INTO BacSi (TenBacSi, ChuyenKhoa, SoDienThoai, Email) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bacSi.getTenBacSi());
            stmt.setString(2, bacSi.getChuyenKhoa());
            stmt.setString(3, bacSi.getSoDienThoai());
            stmt.setString(4, bacSi.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BacSi getById(int id) {
        String sql = "SELECT * FROM BacSi WHERE MaBacSi = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BacSi bacSi = new BacSi();
                bacSi.setMaBacSi(rs.getInt("MaBacSi"));
                bacSi.setTenBacSi(rs.getString("TenBacSi"));
                bacSi.setChuyenKhoa(rs.getString("ChuyenKhoa"));
                bacSi.setSoDienThoai(rs.getString("SoDienThoai"));
                bacSi.setEmail(rs.getString("Email"));
                return bacSi;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BacSi> getAll() {
        List<BacSi> bacSiList = new ArrayList<>();
        String sql = "SELECT * FROM BacSi";
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                BacSi bacSi = new BacSi();
                bacSi.setMaBacSi(rs.getInt("MaBacSi"));
                bacSi.setTenBacSi(rs.getString("TenBacSi"));
                bacSi.setChuyenKhoa(rs.getString("ChuyenKhoa"));
                bacSi.setSoDienThoai(rs.getString("SoDienThoai"));
                bacSi.setEmail(rs.getString("Email"));
                bacSiList.add(bacSi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bacSiList;
    }

    @Override
    public void update(BacSi bacSi) {
        String sql = "UPDATE bacsi SET tenBacSi = ?, chuyenKhoa = ?, soDienThoai = ?, email = ? WHERE maBacSi = ?";
        try (Connection conn = JDBCUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bacSi.getTenBacSi());
            stmt.setString(2, bacSi.getChuyenKhoa());
            stmt.setString(3, bacSi.getSoDienThoai());
            stmt.setString(4, bacSi.getEmail());
            stmt.setInt(5, bacSi.getMaBacSi());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void delete(int id) {
        String sql = "DELETE FROM BacSi WHERE MaBacSi = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> laySoBacSiTheoChuyenKhoa() {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT ChuyenKhoa, COUNT(*) AS SoBacSi FROM BacSi GROUP BY ChuyenKhoa";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String chuyenKhoa = rs.getString("ChuyenKhoa");
                int soBacSi = rs.getInt("SoBacSi");
                result.put(chuyenKhoa, soBacSi);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
