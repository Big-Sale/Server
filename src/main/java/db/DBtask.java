package db;

import logger.Logger;

public abstract class DBtask {

    public String execute(String payload, int userId) {
        Logger.dbLog(payload, userId); //TODO ändra
        return doExecute(payload, userId);

    }

    protected abstract String doExecute(String s, int userId);

}
