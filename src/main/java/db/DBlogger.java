package db;

public class DBlogger {

    public static void dbLog(String payload, int userID) {
        System.out.printf("Message from user %s: %s", userID, payload);
    }
}
