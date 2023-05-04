import beans.LoginType;
import beans.OnlineUsers;
import beans.SearchType;
import beans.SignupType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.SearchHandler;
import db.ValidateUser;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;


public class Server extends WebSocketServer {
    OnlineUsers users = new OnlineUsers();
    SearchHandler searchHandler = new SearchHandler();

    public Server() {
        super(new InetSocketAddress(1234));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress());

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("Closed connection to " + webSocket.getRemoteSocketAddress());
        users.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("Message from " + webSocket.getRemoteSocketAddress() + ": " + s);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String type = rootNode.get("type").asText();
        switch (type) {
            case "login":
                login(s, webSocket);
                break;
            case "signup":
                signup(s, webSocket);
                break;
            case "search":
                search(s, webSocket);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Error from " + webSocket.getRemoteSocketAddress());
        e.printStackTrace();
        users.remove(webSocket);
        webSocket.close();
    }



    private void login(String s, WebSocket webSocket) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LoginType user = objectMapper.readValue(s, LoginType.class);
            System.out.println(user.payload.username + " " + user.payload.pw);
            if (ValidateUser.validate(user.payload.username, user.payload.pw)) {
                webSocket.send("{\"type\":\"login\",\"payload\":{\"success\":true}}");
                users.put(user.payload.username, webSocket);
            } else {
                webSocket.send("{\"type\":\"login\",\"payload\":{\"success\":false}}");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private void signup(String s, WebSocket webSocket) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SignupType signup = objectMapper.readValue(s, SignupType.class);
            if (db.SignupHandler.signup(signup.payload)) {
                webSocket.send("{\"type\":\"signup\",\"payload\":{\"success\":true}}");
            } else {
                webSocket.send("{\"type\":\"signup\",\"payload\":{\"success\":false}}");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private void search(String s, WebSocket webSocket) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SearchType search = objectMapper.readValue(s, SearchType.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
