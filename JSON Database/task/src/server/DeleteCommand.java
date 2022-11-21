package server;

import java.util.List;

public class DeleteCommand implements Command{
    private Database db;
    private List<String> key;

    public DeleteCommand(Database db, List<String> key) {
        this.db = db;
        this.key = key;
    }

    @Override
    public String execute() {
        return db.delete(key);
    }
}
