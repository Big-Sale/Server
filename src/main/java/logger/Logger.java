package logger;

public class Logger {

    public static void dbLog(String payload, int userID) {
        System.out.printf("Action related to user %s: %s%n", userID, payload);
    }

    public static void connectLog(String remoteAddress) {
        System.out.printf("New connection from %s%n", remoteAddress);
    }

    public static void disconnectLog(String remoteAddress) {
        System.out.printf("Connection with %s was closed%n", remoteAddress);
    }

    public static void messageLog(String remoteAddress) {
        System.out.printf("New request from %s", remoteAddress);
    }

    public static void errorLog(String remoteAddress, Exception e) {
        System.err.printf("Error from %s%n", remoteAddress);
        e.printStackTrace();
    }

}
