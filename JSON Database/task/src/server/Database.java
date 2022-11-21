package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    private JsonElement data;
    public static final String EMPTY = "";
    public static final String ERROR_MSG = "ERROR";
    public static final String OK_MSG = "OK";
    public static final String UNKNOWN_COMMAND_MSG = "Unknown command";
    //public static final String PATH_TO_FILE = "JSON Database/task/src/server/data/db.json";
    public static final String PATH_TO_FILE = System.getProperty("user.dir") + "/src/server/data/db.json";
    private File file;
    Lock readLock;
    Lock writeLock;



    class Json{
        String type;
        List<String> key;
        String value;

        public Json(String type, List<String> key, String value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        public List<String> getKeys() {
            return key;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }


    Database() {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }


    public static JsonElement readDbFromFile() {
        String jsonInput = "";
        try {
            jsonInput = new String(Files.readAllBytes(Paths.get(PATH_TO_FILE)));
        } catch (IOException e) {
            System.out.println("Cannot read file2: " + e.getMessage());
        }
        return JsonParser.parseString(jsonInput);
    }

    public synchronized static void writeDbToFile(JsonElement data) {
        String dataJson = new Gson().toJson(data);
        try {
            Files.write(Paths.get(PATH_TO_FILE), dataJson.getBytes());
        } catch (IOException e) {
            System.out.println("Cannot read file3: " + e.getMessage());
        }

    }

    public String get(List<String> key) {
        readLock.lock();
        data = readDbFromFile();
        readLock.unlock();

        JsonElement jsnEl = data;
        boolean isKey = true;
        for (String keyStr: key) {
            if (jsnEl.isJsonObject()) {
                jsnEl = jsnEl.getAsJsonObject().get(keyStr);
            } else {
                isKey = false;
                break;
            }
        }
        String res;
        if (jsnEl.isJsonPrimitive()) {
            res = "\"" + jsnEl.getAsString() + "\"";
        } else {
            res = jsnEl.toString();
        }

        if(isKey) {
            return "{\"response\":\"OK\",\"value\":" + res + "}";
        } else {
            return "{\"response\":\"ERROR\",\"reason\":\"No such key\"}";
        }
    }

    public String set(List<String> key, JsonElement value) {
        readLock.lock();
        data = readDbFromFile();
        readLock.unlock();
        writeLock.lock();
        JsonElement jsnEl = data;

        for (int i = 0; i < key.size() - 1; i++) {
            if (jsnEl.isJsonObject()) {
                jsnEl = jsnEl.getAsJsonObject().get(key.get(i));
            } else {
                break;
            }
        }
        if (value.isJsonObject()) {
            jsnEl.getAsJsonObject().add(key.get(key.size() - 1), value.getAsJsonObject());
        } else {
            jsnEl.getAsJsonObject().addProperty(key.get(key.size() - 1), value.getAsString());
        }
        writeDbToFile(data);
        writeLock.unlock();

        return "{\"response\":\"OK\"}";
    }

    public String delete(List<String> key) {
        readLock.lock();
        data = readDbFromFile();
        readLock.unlock();

        JsonElement jsnEl = data;
        boolean isKey = true;
        for (int i = 0; i < key.size() - 1; i++) {
            if (jsnEl.isJsonObject()) {
                jsnEl = jsnEl.getAsJsonObject().get(key.get(i));
            } else {
                isKey = false;
                break;
            }
        }

        if(isKey) {
            writeLock.lock();
            jsnEl.getAsJsonObject().remove(key.get(key.size() - 1));
            writeDbToFile(data);
            writeLock.unlock();
            return "{\"response\":\"OK\"}";
        } else {
            return "{\"response\":\"ERROR\",\"reason\":\"No such key\"}";
        }
    }
}
