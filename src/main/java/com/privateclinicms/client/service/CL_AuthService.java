package com.privateclinicms.client.service;

import com.privateclinicms.client.network.SocketClient;
import com.privateclinicms.shared.protocol.Action;
import com.privateclinicms.shared.protocol.Request;
import com.privateclinicms.shared.protocol.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CL_AuthService {
    public Response login(String email, String password) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);
        return SocketClient.getInstance().send(new Request(Action.LOGIN, data));
    }

    public Response register(Map<String, Object> data) throws IOException {
        return SocketClient.getInstance().send(new Request(Action.REGISTER, data));
    }

    public Response getAllAccounts() throws IOException {
        return SocketClient.getInstance().send(new Request(Action.GET_ALL_ACCOUNTS, null));
    }

    public Response lockAccount(String email) throws IOException {
        return SocketClient.getInstance().send(new Request(Action.LOCK_ACCOUNT, Map.of("email", email)));
    }

    public Response unlockAccount(String email) throws IOException {
        return SocketClient.getInstance().send(new Request(Action.UNLOCK_ACCOUNT, Map.of("email", email)));
    }
}
