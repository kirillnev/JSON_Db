package server;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import json.JsonQuery;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private final static String ADDRESS = "127.0.0.1";
    private final static int PORT = 23456;

    public static void main(String[] args) {
        boolean isExit = false;
        try (ServerSocket server = new ServerSocket(PORT);) {
            System.out.println("Server started!");
            Database db = new Database();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            try {
                while (!isExit) {

                    Socket socket = server.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                    Controller controller = new Controller();
                    JsonQuery json = new Gson().fromJson(input.readUTF(), JsonQuery.class);

                    executor.submit(() -> {
                        try {
                            String msg = "";
                            switch (json.getType()) {
                                case "get":
                                    controller.setCommand(new GetCommand(db, json.getKey()));
                                    msg = controller.executeCommand();
                                    break;
                                case "set":
                                    controller.setCommand(new SetCommand(db, json.getKey(), json.getValue()));
                                    msg = controller.executeCommand();
                                    break;
                                case "delete":
                                    controller.setCommand(new DeleteCommand(db, json.getKey()));
                                    msg = controller.executeCommand();
                                    break;
                                case "exit":
                                    msg = "{\"response\":\"OK\"}";
                                    break;
                                default:
                                    msg = "{\"response\":\"ERROR\",\"reason\":\"\"}";
                            }
                            output.writeUTF(msg);
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    isExit = json.getType().equals("exit");
                }
            } finally {
                executor.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}