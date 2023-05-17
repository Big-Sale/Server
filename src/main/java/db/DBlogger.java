package db;

public class DBlogger {

    public static void log(String payload, String userID) {
        System.out.printf("Message from user %s: %s", userID, payload);
    }
}
