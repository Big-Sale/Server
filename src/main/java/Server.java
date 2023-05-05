import beans.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginHandler;
import db.SearchHandler;
import db.ValidateUser;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.LinkedList;


public class Server extends WebSocketServer {
    SearchHandler searchHandler = new SearchHandler();

    public Server() {
        super(new InetSocketAddress(1234));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress());

        LinkedList<BuyProduct> list = SearchHandler.getRandomProducts();
        BuyProduct[] arr = list.toArray(new BuyProduct[0]);
        ObjectMapper objectMapper = new ObjectMapper();
        BuyProductType buyProductType = new BuyProductType();
        buyProductType.type = "randomProducts";
        buyProductType.payload = arr;
        try {
            String json = objectMapper.writeValueAsString(buyProductType);
            webSocket.send(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("Closed connection to " + webSocket.getRemoteSocketAddress());
        OnlineUsers.remove(webSocket);
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
        J
        switch (type) {
            case "login":
                new LoginHandler().execute(rootNode);
                break;
            case "signup":
                signup(s, webSocket);
                break;
            case "search":
                search(s, webSocket);
                break;
            case "addProduct":
                ProductType product = null;
              //  int id = users.get(webSocket);
                int id = 1;
                try {
                    product = objectMapper.readValue(s, ProductType.class);
                    ProductWithId productWithId = new ProductWithId(product.payload, id);
                    db.ProductHandler.addProduct(productWithId);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Error from " + webSocket.getRemoteSocketAddress());
        e.printStackTrace();
        OnlineUsers.remove(webSocket);
        webSocket.close();
    }



    private void login(String s, WebSocket webSocket) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LoginType user = objectMapper.readValue(s, LoginType.class);
            System.out.println(user.payload.username + " " + user.payload.pw);
            int id = ValidateUser.validate(user.payload.username, user.payload.pw);
            if (id == -1) {
                webSocket.send("{\"type\":\"login\",\"payload\":{\"success\":false}}");
            } else {
                webSocket.send("{\"type\":\"login\",\"payload\":{\"success\":true}}");
                OnlineUsers.put(id, webSocket);
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
            searchHandler.search(search.payload);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
