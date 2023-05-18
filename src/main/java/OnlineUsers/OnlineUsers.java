package OnlineUsers;

import org.java_websocket.WebSocket;

import java.util.HashMap;

public class OnlineUsers {
    static HashMap<Integer, WebSocket> online = new HashMap<>();
    static HashMap<WebSocket, Integer> online2 = new HashMap<>();


    public static void put(int id, WebSocket webSocket) {
        online.put(id, webSocket);
        online2.put(webSocket, id);
    }
    public static WebSocket get(int id) {
        return online.get(id);
    }
    public static int get(WebSocket webSocket) {
        return online2.get(webSocket);
    }
    public static void remove(int id) {
        online.remove(id);
        online2.remove(online.get(id));
    }
    public static void remove(WebSocket webSocket) {
        online2.remove(webSocket);
        online.remove(online2.get(webSocket));
    }

    public static boolean contains(Integer integer) {
        return online.containsKey(integer);
    }
}
