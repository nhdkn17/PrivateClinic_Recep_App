package com.privateclinicms.client.service;

import com.privateclinicms.client.network.SocketClient;
import com.privateclinicms.shared.protocol.Action;
import com.privateclinicms.shared.protocol.Request;
import com.privateclinicms.shared.protocol.Response;
import com.privateclinicms.shared.util.JsonUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CL_DashboardService_Recep {

    public int getSoBenhNhanHomNay() throws IOException {
        Request req = new Request(Action.DASHBOARD_COUNT_PATIENT_TODAY, null);
        Response res = SocketClient.getInstance().send(req);
        return res != null && "success".equals(res.getStatus()) ? ((Double) res.getData()).intValue() : 0;
    }

    public int getSoLichHenHomNay() throws IOException {
        Request req = new Request(Action.DASHBOARD_COUNT_APPOINTMENT_TODAY, null);
        Response res = SocketClient.getInstance().send(req);
        return res != null && "success".equals(res.getStatus()) ? ((Double) res.getData()).intValue() : 0;
    }

    public int getSoDonThuocHomNay() throws IOException {
        Request req = new Request(Action.DASHBOARD_COUNT_PRESCRIPTION_TODAY, null);
        Response res = SocketClient.getInstance().send(req);
        return res != null && "success".equals(res.getStatus()) ? ((Double) res.getData()).intValue() : 0;
    }

    public String getDoanhThuHomNay() throws IOException {
        Request req = new Request(Action.DASHBOARD_REVENUE_TODAY, null);
        Response res = SocketClient.getInstance().send(req);
        return res != null && "success".equals(res.getStatus()) ? (String) res.getData() : "0";
    }

    public Map<Integer, BigDecimal> getDoanhThuTheoNgay(int thang, int nam) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("thang", thang);
        data.put("nam", nam);

        Request req = new Request(Action.DASHBOARD_REVENUE_MONTH, data);
        Response res = SocketClient.getInstance().send(req);

        if (res != null && "success".equals(res.getStatus())) {
            return JsonUtils.fromJson(
                    JsonUtils.toJson(res.getData()),
                    new com.google.gson.reflect.TypeToken<Map<Integer, BigDecimal>>() {}.getType()
            );
        }
        return new HashMap<>();
    }
}
