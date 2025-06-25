package com.privateclinicms.client.network;

import com.privateclinicms.shared.config.Config;
import com.privateclinicms.shared.protocol.Request;
import com.privateclinicms.shared.protocol.Response;
import com.privateclinicms.shared.util.JsonUtils;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private static SocketClient instance;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private SocketClient() throws IOException {
        connect();
    }

    private void connect() throws IOException {
        socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public static synchronized SocketClient getInstance() throws IOException {
        if (instance == null || instance.socket == null || instance.socket.isClosed()) {
            instance = new SocketClient();
        }
        return instance;
    }

    public synchronized Response send(Request request) {
        try {
            if (socket == null || socket.isClosed()) {
                connect();
            }

            String json = JsonUtils.toJson(request);
            if (json == null || json.isBlank()) {
                throw new IOException("Không thể gửi dữ liệu rỗng.");
            }

            System.out.println(">> Client Gửi request: " + json);
            writer.write(json);
            writer.newLine();
            writer.flush();

            String resJson = reader.readLine();
            System.out.println("<< Client Nhận response: " + resJson);
            return JsonUtils.fromJson(resJson, Response.class);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi dữ liệu: " + e.getMessage());
            return new Response("error", "Lỗi kết nối tới server: " + e.getMessage(), null);
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
