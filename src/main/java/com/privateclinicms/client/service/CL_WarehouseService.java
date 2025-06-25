package com.privateclinicms.client.service;

import com.privateclinicms.client.network.SocketClient;
import com.privateclinicms.shared.model.Thuoc;
import com.privateclinicms.shared.protocol.Action;
import com.privateclinicms.shared.protocol.Request;
import com.privateclinicms.shared.protocol.Response;
import com.privateclinicms.shared.util.JsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CL_WarehouseService {

    public List<Thuoc> findAll() throws IOException {
        Request request = new Request(Action.WAREHOUSE_FIND_ALL, null);
        Response response = SocketClient.getInstance().send(request);
        if (response != null && "success".equals(response.getStatus())) {
            return JsonUtils.convertToList(response.getData(), Thuoc.class);
        }
        return null;
    }

    public boolean addThuoc(Thuoc thuoc) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("warehouse", thuoc);
        Response res = SocketClient.getInstance().send(new Request(Action.WAREHOUSE_ADD, data));
        return res != null && "success".equals(res.getStatus());
    }

    public boolean updateThuoc(Thuoc thuoc) throws IOException {
        Map<String, Object> data = JsonUtils.toMap(thuoc);
        Response res = SocketClient.getInstance().send(new Request(Action.WAREHOUSE_UPDATE, data));
        return res != null && "success".equals(res.getStatus());
    }

    public boolean deleteThuoc(int id) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        Response res = SocketClient.getInstance().send(new Request(Action.WAREHOUSE_DELETE, data));
        return res != null && "success".equals(res.getStatus());
    }

    public List<Thuoc> getThuocSapHet() throws IOException {
        Request req = new Request(Action.WAREHOUSE_LOW_STOCK, null);
        Response response = SocketClient.getInstance().send(req);
        if (response != null && "success".equals(response.getStatus())) {
            return JsonUtils.convertToList(response.getData(), Thuoc.class);
        }
        return null;
    }
}
