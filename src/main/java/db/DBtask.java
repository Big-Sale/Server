package db;

import logger.Logger;

public abstract class DBtask {

    public String execute(String payload, int userID) {
        Logger.dbLog(payload, userID); //TODO ändra
        return doExecute(payload, userID);

    }

    public abstract String doExecute(String s, int userID);

}
