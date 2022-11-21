package json;

import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonQuery {
    private String type;
    private JsonElement value;
    private List<String> key;

    public JsonQuery(String[] args) {
        List<String> listStr = Arrays.asList(args);
        String type = null;
        List<String> key = new ArrayList<>();
        JsonElement value = JsonParser.parseString("{}");

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

                JsonElement jsnEl = JsonParser.parseString(jsonInput);

                if(jsnEl.isJsonObject()){
                    JsonObject jsonObject = jsnEl.getAsJsonObject();
                    if (jsonObject.keySet().contains("type")) {
                        type = jsonObject.get("type").getAsString();
                    }
                    if (jsonObject.keySet().contains("value")) {
                        value = jsonObject.get("value");
                    }
                    if (jsonObject.keySet().contains("key")) {
                        if (jsonObject.get("key").isJsonPrimitive()) {
                            key.add(jsonObject.get("key").getAsString());
                        } else {
                            JsonArray jsonArray = jsonObject.get("key").getAsJsonArray();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                key.add(jsonArray.get(i).getAsString());
                            }
                        }
                    }
                }
            }
        } else if (listStr.indexOf("-t") >= 0){
            type = listStr.get(listStr.indexOf("-t") + 1);
            if (listStr.indexOf("-k") >= 0){
                key.add(listStr.get(listStr.indexOf("-k") + 1));
                if (listStr.indexOf("-v") >= 0){
                    value = JsonParser.parseString("\"" + listStr.get(listStr.indexOf("-v") + 1) + "\"");
                }
            }
        }

        this.type = type;
        this.value = value;
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public JsonElement getValue() {
        return value;
    }

    public List<String> getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "JsonQuery{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
