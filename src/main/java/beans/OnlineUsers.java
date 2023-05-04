package beans;

import org.java_websocket.WebSocket;

import java.util.HashMap;

public class OnlineUsers {
    HashMap<String, WebSocket> online = new HashMap<>();
    HashMap<WebSocket, String> online2 = new HashMap<>();


    public void put(String username, WebSocket webSocket) {
        online.put(username, webSocket);
        online2.put(webSocket, username);
    }
    public WebSocket get(String username) {
        return online.get(username);
    }
    public String get(WebSocket webSocket) {
        return online2.get(webSocket);
    }
    public void remove(String username) {
        online.remove(username);
        online2.remove(online.get(username));
    }
    public void remove(WebSocket webSocket) {
        online2.remove(webSocket);
        online.remove(online2.get(webSocket));
    }
}
