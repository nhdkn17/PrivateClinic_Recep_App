package com.privateclinicms.client.service;

import com.privateclinicms.shared.model.BenhNhan;
import com.privateclinicms.shared.protocol.Action;
import com.privateclinicms.shared.protocol.Request;
import com.privateclinicms.shared.protocol.Response;
import com.privateclinicms.shared.util.JDBCUtil;
import com.privateclinicms.shared.util.JsonUtils;
import com.privateclinicms.client.network.SocketClient;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CL_PatientService {
    public List<BenhNhan> getAllPatients() throws IOException {
        Request request = new Request(Action.PATIENT_FIND_ALL, null);
        Response response = SocketClient.getInstance().send(request);
        if (response != null && "success".equals(response.getStatus())) {
            return JsonUtils.convertToList(response.getData(), BenhNhan.class);
        }
        return null;
    }

    public boolean addPatient(BenhNhan patient) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("patient", patient);
        Request request = new Request(Action.PATIENT_ADD, data);
        Response response = SocketClient.getInstance().send(request);
        return response != null && "success".equals(response.getStatus());
    }

    public boolean updatePatient(BenhNhan patient) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("patient", patient);
        Request request = new Request(Action.PATIENT_UPDATE, data);
        Response response = SocketClient.getInstance().send(request);
        return response != null && "success".equals(response.getStatus());
    }

    public boolean deletePatient(int maBenhNhan) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("maBenhNhan", maBenhNhan);
        Request request = new Request(Action.PATIENT_DELETE, data);
        Response response = SocketClient.getInstance().send(request);
        return response != null && "success".equals(response.getStatus());
    }

    public Integer getMaBenhNhanByTen(String tenBenhNhan) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("tenBenhNhan", tenBenhNhan);

        Request request = new Request(Action.PATIENT_FIND_NAME, data);
        Response response = SocketClient.getInstance().send(request);

        if (response != null && "success".equals(response.getStatus())) {
            Double idDouble = (Double) response.getData();
            return idDouble.intValue();
        }

        return null;
    }

    public int insertAndReturnId(BenhNhan bn) throws SQLException {
        String sql = "INSERT INTO BenhNhan (TenBenhNhan, NgaySinh, GioiTinh, SoDienThoai, Email, DiaChi, NgayKham) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, bn.getTenBenhNhan());
            stmt.setDate(2, bn.getNgaySinh());
            stmt.setString(3, bn.getGioiTinh());
            stmt.setString(4, bn.getSoDienThoai());
            stmt.setString(5, bn.getEmail());
            stmt.setString(6, bn.getDiaChi());
            stmt.setDate(7, bn.getNgayKham());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        }
    }
}
