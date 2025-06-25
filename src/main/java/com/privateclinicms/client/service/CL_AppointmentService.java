package com.privateclinicms.client.service;

import com.privateclinicms.client.network.SocketClient;
import com.privateclinicms.shared.model.BacSi;
import com.privateclinicms.shared.model.BenhNhan;
import com.privateclinicms.shared.model.LichKham;
import com.privateclinicms.shared.model.LichKhamModel;
import com.privateclinicms.shared.protocol.Action;
import com.privateclinicms.shared.protocol.Request;
import com.privateclinicms.shared.protocol.Response;
import com.privateclinicms.shared.util.JsonUtils;
import com.privateclinicms.shared.util.LichKhamConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CL_AppointmentService {
    public boolean addAppointment(LichKham lichKham) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("appointment", lichKham);
        Request request = new Request(Action.APPOINTMENT_ADD, data);
        Response response = SocketClient.getInstance().send(request);
        return response != null && "success".equals(response.getStatus());
    }

    public boolean updateAppointment(LichKham lichKham) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("appointment", lichKham);
        Request request = new Request(Action.APPOINTMENT_UPDATE, data);
        Response response = SocketClient.getInstance().send(request);
        return response != null && "success".equals(response.getStatus());
    }

    public boolean deleteAppointment(int maLichKham) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("maLichKham", maLichKham);
        Request request = new Request(Action.APPOINTMENT_DELETE, data);
        Response response = SocketClient.getInstance().send(request);
        return response != null && "success".equals(response.getStatus());
    }

    public List<LichKhamModel> getAppointmentsToday() throws IOException {
        Request request = new Request(Action.APPOINTMENT_FIND_ALL, null);
        Response response = SocketClient.getInstance().send(request);
        if (response != null && "success".equals(response.getStatus())) {
            return JsonUtils.convertToList(response.getData(), LichKhamModel.class);
        }
        return null;
    }

    private List<BenhNhan> cachedPatients = null;
    private List<BacSi> cachedDoctors = null;

    public List<LichKhamModel> getAppointmentsByDate(LocalDate date) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("date", date.toString());
        Request request = new Request(Action.APPOINTMENT_FIND_BY_DATE, data);
        Response response = SocketClient.getInstance().send(request);

        if ("success".equals(response.getStatus())) {
            List<LichKham> lichKhams = JsonUtils.convertToList(response.getData(), LichKham.class);
            if (lichKhams == null) return List.of();

            if (cachedPatients == null)
                cachedPatients = new CL_PatientService().getAllPatients();

            if (cachedDoctors == null)
                cachedDoctors = new CL_DoctorService().getAllDoctors();

            Map<Integer, String> mapBenhNhan = new HashMap<>();
            for (BenhNhan bn : cachedPatients) {
                mapBenhNhan.put(bn.getMaBenhNhan(), bn.getTenBenhNhan());
            }

            Map<Integer, String> mapBacSi = new HashMap<>();
            for (BacSi bs : cachedDoctors) {
                mapBacSi.put(bs.getMaBacSi(), bs.getTenBacSi());
            }

            return lichKhams.stream()
                    .map(lk -> LichKhamConverter.toModel(
                            lk,
                            mapBenhNhan.getOrDefault(lk.getMaBenhNhan(), "Không rõ"),
                            mapBacSi.getOrDefault(lk.getMaBacSi(), "Không rõ")
                    ))
                    .toList();
        }

        return List.of();
    }

    public List<LichKham> getAppointmentsEntitiesByDate(LocalDate date) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("date", date.toString());

        Request request = new Request(Action.APPOINTMENT_ENTITY_BY_DATE, data);
        Response response = SocketClient.getInstance().send(request);

        if (response != null && "success".equals(response.getStatus())) {
            return JsonUtils.convertToList(response.getData(), LichKham.class);
        }
        return null;
    }
}
