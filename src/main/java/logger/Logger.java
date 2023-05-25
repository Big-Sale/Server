package logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");

    public static void dbLog(String payload, int userID) {
        String payloadBlock = block(payload);
        System.out.println(
                block(now()) +
                        block("ACTION") +
                        block("FROM") +
                        block(String.valueOf(userID)) +
                        block("PAYLOAD: " + payloadBlock));
    }

    public static void messageLog(String remoteAddress) {
        System.out.println(
                block(now()) +
                        block("REQUEST") +
                        block("FROM") +
                        block(remoteAddress));
    }

    public static void connectLog(String remoteAddress) {
        System.out.println(
                block(now()) +
                        block("CONNECTION") +
                        block("OPENED") +
                        block("FROM") +
                        block(remoteAddress));
    }

    public static void disconnectLog(String remoteAddress) {
        System.out.println(
                block(now()) +
                        block("CONNECTION") +
                        block("CLOSED") +
                        block("FROM") +
                        block(remoteAddress));
    }

    public static void errorLog(String remoteAddress, Exception e) {
        System.err.println(
                block("ERROR") +
                        block("FROM") +
                        block(remoteAddress));
        e.printStackTrace();
    }

    private static String block(String toEnclose) {
        return "[" + toEnclose + "]";
    }

    private static String now() {
        return dtf.format(LocalDateTime.now());
    }
}
