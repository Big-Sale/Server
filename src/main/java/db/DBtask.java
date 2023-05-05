package db;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class DBtask {

    public JsonNode execute(JsonNode node) {
        DBlogger.log("temp"); //TODO ändra
        return doExecute(node);

    }

    public abstract JsonNode doExecute(JsonNode node);
}
