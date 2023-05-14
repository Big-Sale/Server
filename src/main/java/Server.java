import beans.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.sql.*;
import java.util.LinkedList;


public class Server extends WebSocketServer {
    SearchHandler searchHandler = new SearchHandler();

    public Server() {
        super(new InetSocketAddress(8080));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress());
        ObjectMapper objectMapper = new ObjectMapper();
        BuyProductType buyProductType = new BuyProductType();
        buyProductType.type = "randomProducts";
        buyProductType.payload = SearchHandler.getRandomProducts();
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
    public void onMessage(WebSocket webSocket, String json) {
        System.out.println("Message from " + webSocket.getRemoteSocketAddress() + ": " + json);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String type = rootNode.get("type").asText();
        switch (type) {
            case "login" -> login(json, webSocket);
            case "signup" -> signup(json, webSocket);
            case "search" -> search(json, webSocket);
            case "notifications" -> notifications(webSocket);
            case "addProduct" -> addProduct(json, webSocket);
            case "OrderHistoryRequest" -> orderHistory(json, webSocket);
            case "buyProduct" -> buyProduct(json, webSocket);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

    }

    private void buyProduct(String json, WebSocket webSocket) {
    }

    private void addProduct(String json, WebSocket webSocket) {
        int id = OnlineUsers.get(webSocket);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ProductType product = objectMapper.readValue(json, ProductType.class);
            ProductWithId productWithId = new ProductWithId(product.payload, id);
            ProductHandler.addProduct(productWithId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private void notifications(WebSocket webSocket) {
        int id = OnlineUsers.get(webSocket);
        LinkedList<BuyProduct> list = new LinkedList<>();
        try {
            Connection connection = db.DataBaseConnection.getDatabaseConnection();
            String query = "select * from get_notifications(?);";
            PreparedStatement stm = connection.prepareStatement(query);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                BuyProduct product = new BuyProduct();
                product.productId = rs.getInt(1);
                product.productType = rs.getString(2);
                product.price = rs.getFloat(3);
                product.colour = rs.getString(4);
                product.condition = rs.getString(5);
                product.productName = rs.getString(7);
                product.seller = rs.getInt(8);
                product.yearOfProduction = rs.getString(9);
                list.add(product);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        BuyProduct[] arr = list.toArray(new BuyProduct[0]);
        ObjectMapper objectMapper = new ObjectMapper();
        BuyProductType buyProductType = new BuyProductType();
        buyProductType.type = "notifications";
        buyProductType.payload = arr;
        try {
            String json = objectMapper.writeValueAsString(buyProductType);
            webSocket.send(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //TODO: Query somr retunerar alla produkter som id premunurerar på (och flytta till r'tt handler)
    }

    //TODO FLYTTA TILL KORREKT HANDLER
    private void orderHistory(String json, WebSocket webSocket) {
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedList<OrderHistoryProduct> list = new LinkedList<>();
        try {
            OrderHistoryRequestType ohrt = objectMapper.readValue(json, OrderHistoryRequestType.class);
            Connection connection = db.DataBaseConnection.getDatabaseConnection();
            String query = "select * from get_order_history(?, ?);";
            PreparedStatement stm = connection.prepareStatement(query);
            stm.setInt(1, ohrt.payload.userId);
            stm.setDate(2, Date.valueOf(ohrt.payload.date));
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                OrderHistoryProduct ohp = new OrderHistoryProduct();
                ohp.productId = rs.getInt(1);
                ohp.productType = rs.getString(2);
                ohp.price = rs.getFloat(3);
                ohp.colour = rs.getString(4);
                ohp.condition = rs.getString(5);
                ohp.productName = rs.getString(6);
                ohp.seller = rs.getInt(7);
                ohp.yearOfProduction = rs.getString(8);
                ohp.dateOfPurchase = rs.getDate(9);
                list.add(ohp);
            }
            OrderHistoryProduct[] arr = list.toArray(new OrderHistoryProduct[0]);
            OrderHistoryType oht = new OrderHistoryType();
            oht.type = "order_history_request";
            oht.payload = arr;
            String jsonReturn = objectMapper.writeValueAsString(oht);
            rs.close();
            stm.close();
            connection.close();
            webSocket.send(jsonReturn);
        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException(e);
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
            int id = ValidateUser.validate(user.payload.username, user.payload.pw);
            if (id != -1) {
                webSocket.send("{\"type\":\"login\",\"payload\":{\"id\":"+id+"}}");
                OnlineUsers.put(id, webSocket);
            } else {
                webSocket.send("{\"type\":\"login\",\"payload\":{\"id\":-1}}");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private void signup(String s, WebSocket webSocket) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SignupType signup = objectMapper.readValue(s, SignupType.class);
            int id = SignupHandler.signup(signup.payload);
            if (id != -1) {
                OnlineUsers.put(id, webSocket);
                webSocket.send("{\"type\":\"signup\",\"payload\":{\"id\":" + id + "}}");
            } else {
                webSocket.send("{\"type\":\"signup\",\"payload\":{\"id\":-1}}");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private void search(String s, WebSocket webSocket) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SearchType search = objectMapper.readValue(s, SearchType.class);
            SearchRequest buyProductRequest = new SearchRequest();
            buyProductRequest.type = "search";
            buyProductRequest.payload = searchHandler.search(search.payload);
            webSocket.send(objectMapper.writeValueAsString(buyProductRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
