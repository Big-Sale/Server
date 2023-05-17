package db;

public abstract class DBtask {

    public String execute(String payload, String userID) {
        DBlogger.log(payload, userID); //TODO ändra
        return doExecute(payload, userID);

    }

    public abstract String doExecute(String s, String userID);

}
