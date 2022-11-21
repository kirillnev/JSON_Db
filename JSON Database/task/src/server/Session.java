package server;

import com.google.gson.Gson;
import json.JsonQuery;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session extends Thread{
    private Socket socket;
    private Database db;
    private boolean isExit;

    public Session(Socket socket, Database db) {
        super();
        this.socket = socket;
        this.db = db;
        this.start();
        this.isExit = false;
    }

    @Override
    public void run() {
        try (
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output  = new DataOutputStream(socket.getOutputStream());
        ) {
            Controller controller = new Controller();

            String msg = "";

            //Params p = new Params(input.readUTF());
            JsonQuery json = new Gson().fromJson(input.readUTF(), JsonQuery.class);
            //System.out.println("json: " + json);
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
                    this.isExit = true;
                    msg = "{\"response\":\"OK\"}";
                    break;
                default:
                    msg = "{\"response\":\"ERROR\",\"reason\":\"\"}";
            }
            output.writeUTF(msg);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean getExit() {
        return isExit;
    }
}
