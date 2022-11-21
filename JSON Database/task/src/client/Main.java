package client;

import com.google.gson.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import json.JsonQuery;


public class Main {

    private final static String ADDRESS = "127.0.0.1";
    private final static int PORT = 23456;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (
            Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");

            //args = "-t set -k 1 -v Hello".split(" ");
            //args = "-in secondGetFile.json".split(" ");

            JsonQuery json = new JsonQuery(args);
            String jsonString = new Gson().toJson(json);

            String sentStr = argsTopJsonString(args);
            System.out.println("Sent: " + sentStr);
            output.writeUTF(jsonString);
            String receivedMsg = input.readUTF();
            System.out.printf("Received: %s\n", receivedMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static String argsTopJsonString(String[] args) {
        List<String> listStr = Arrays.asList(args);
        String type = "";
        String key = "";
        String value = "";

        if (listStr.indexOf("-in") >= 0) {
            String jsonInput = "";
            try {
                //String filePath = "JSON Database/task/src/client/data/" + listStr.get(listStr.indexOf("-in") + 1);
                String filePath = System.getProperty("user.dir") + "/src/client/data/" + listStr.get(listStr.indexOf("-in") + 1);

                jsonInput = new String(Files.readAllBytes(Paths.get(filePath)));
            } catch (IOException e) {
                System.out.println("Cannot read file1: " + e.getMessage());
            }
            if(!jsonInput.isEmpty()) {

                return jsonInput;
            }
        } else if (listStr.indexOf("-t") >= 0){
            type = listStr.get(listStr.indexOf("-t") + 1);
            if (listStr.indexOf("-k") >= 0){
                key = listStr.get(listStr.indexOf("-k") + 1);
                if (listStr.indexOf("-v") >= 0){
                    value = listStr.get(listStr.indexOf("-v") + 1);
                }
            }
        }

        JsonObject js = new JsonObject();
        if(!type.isEmpty()){
            js.addProperty("type", type);
        }
        if(!value.isEmpty()){
            js.addProperty("value", value);
        }
        if(!key.isEmpty()){
            js.addProperty("key", key);
        }
        return new Gson().toJson(js);
    }

}