package server;

import com.google.gson.JsonElement;

import java.util.List;

public class SetCommand implements Command{
    private Database db;
    private List<String> key;
    private JsonElement value;

    public SetCommand(Database db, List<String> key, JsonElement value) {
        this.db = db;
        this.key = key;
        this.value = value;
    }

    @Override
    public String execute() {
        return db.set(key, value);
    }
}
