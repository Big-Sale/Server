package db;

public abstract class DBtask {

    public String execute(String payload, int userID) {
        DBlogger.dbLog(payload, userID); //TODO ändra
        return doExecute(payload, userID);

    }

    public abstract String doExecute(String s, int userID);

}
