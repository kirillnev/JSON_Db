package server;

import java.util.List;

public class GetCommand implements Command{
    private Database db;
    private List<String> key;

    public GetCommand(Database db, List<String> key) {
        this.db = db;
        this.key = key;
    }

    @Override
    public String execute() {
        return db.get(key);
    }
}
