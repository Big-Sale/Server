package db;

public abstract class DBtask {

    public String execute(String s) {
        DBlogger.log("temp"); //TODO ändra
        return doExecute(s);

    }

    public abstract String doExecute(String s);

}
