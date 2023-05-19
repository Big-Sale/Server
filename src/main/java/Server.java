import OnlineUsers.OnlineUsers;
import beans.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.*;
import marshall.UnmarshallHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.sql.*;
import java.util.LinkedList;


public class Server extends WebSocketServer {
    SearchTask searchTask = new SearchTask();

    public Server() {
        super(new InetSocketAddress(8080));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress());
        ObjectMapper objectMapper = new ObjectMapper();
        ReturnProductType returnProductType = new ReturnProductType();
        returnProductType.type = "randomProducts";
        returnProductType.payload = SearchTask.getRandomProducts();
        try {
            String json = objectMapper.writeValueAsString(returnProductType);
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
            case "removeNotification" -> removeNotification(rootNode.get("payload").asInt(), webSocket);
            case "subscribe" -> subscribe(rootNode.get("payload").asText(), webSocket);
            case "notificationCheck" -> notificationCheck(webSocket);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

    }

    private void notificationCheck(WebSocket webSocket) {

    }

    private void subscribe(String payload, WebSocket webSocket) {
        int userId = OnlineUsers.get(webSocket);
        SubscribeTask st = new SubscribeTask();
        st.execute(payload, userId);
    }

    private void removeNotification(int productId, WebSocket webSocket) {
        int userId = OnlineUsers.get(webSocket);
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("DELETE FROM notifications WHERE userid = ? AND productid = ?")) {
            stm.setInt(1, userId);
            stm.setInt(2, productId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void buyProduct(String json, WebSocket webSocket) {
        BuyProductTask bph = new BuyProductTask();
        int id = OnlineUsers.get(webSocket);
        String jsonProducts = bph.execute(json, id);

        FindSellerTask poh = new FindSellerTask();


        Integer[] products = UnmarshallHandler.unmarshall(jsonProducts, Integer[].class);
        for (Integer i : products) {
            int seller = Integer.parseInt(poh.execute(String.valueOf(i), id));
            if (OnlineUsers.contains(seller)){
                NotificationType notificationType = new NotificationType();
                notificationType.type = "pending_order_notification";
                try {
                    OnlineUsers.get(seller).send(new ObjectMapper().writeValueAsString(notificationType));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addProduct(String json, WebSocket webSocket) {
        int id = OnlineUsers.get(webSocket);
        AddProductTask aph = new AddProductTask();
        String jsonProduct = aph.execute(json, id);
        checkNotifications(UnmarshallHandler.unmarshall(jsonProduct, Product.class));
    }

    private void checkNotifications(Product product) {
        LinkedList<Integer> userIds = new LinkedList<>();
        try {
            Connection conn = db.DataBaseConnection.getDatabaseConnection();
            String query = "select userid from subscriptions where productname = ?;";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, product.productType);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                if (OnlineUsers.contains(id)) {
                    WebSocket webSocket = OnlineUsers.get(id);
                    if (webSocket != null && id != product.seller) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        NotificationType notificationType = new NotificationType();
                        notificationType.type = "subscribed_product";
                        notificationType.payload = product;
                        String json = objectMapper.writeValueAsString(notificationType);
                        webSocket.send(json);
                    }
                }
                userIds.add(id);
            }
            rs.close();
            stm.close();
            String q = "call add_notification(?, ?);";
            PreparedStatement st = conn.prepareStatement(q);
            st.setArray(1, conn.createArrayOf("integer", userIds.toArray()));
            st.setInt(2, product.productId);
            st.execute();
            st.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifications(WebSocket webSocket) {
        int id = OnlineUsers.get(webSocket);
        LinkedList<Product> list = new LinkedList<>();
        try {
            Connection connection = db.DataBaseConnection.getDatabaseConnection();
            String query = "select * from get_notifications(?);";
            PreparedStatement stm = connection.prepareStatement(query);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Product product = new Product();
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
            rs.close();
            stm.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ReturnProductType returnProductType = new ReturnProductType();
        returnProductType.type = "notifications";
        returnProductType.payload = list.toArray(new Product[0]);
        ;
        try {
            String json = objectMapper.writeValueAsString(returnProductType);
            webSocket.send(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //TODO: Query somr retunerar alla produkter som id premunurerar på (och flytta till r'tt handler)
    }

    //TODO FLYTTA TILL KORREKT HANDLER
    private void orderHistory(String json, WebSocket webSocket) {
        OrderHistoryTask oht = new OrderHistoryTask();
        webSocket.send(oht.execute(json, OnlineUsers.get(webSocket)));

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Error from " + webSocket.getRemoteSocketAddress());
        e.printStackTrace();
        OnlineUsers.remove(webSocket);
        webSocket.close();
    }


    private void login(String s, WebSocket webSocket) { //TODO kanske inte ska vara task kolla över hur man kan göra med ID
        LoginTask lt = new LoginTask();
        String toReturn = lt.execute(s, OnlineUsers.get(webSocket));
        webSocket.send(toReturn);
    }

    private void signup(String s, WebSocket webSocket) {
        SignupTask sh = new SignupTask();
        String stringID = sh.execute(s,-1);
        int id = Integer.parseInt(stringID);
        if (id != -1) {
            OnlineUsers.put(id, webSocket);
        }
        webSocket.send("{\"type\":\"signup\",\"payload\":{\"id\":" + id + "}}");
    }

    private void search(String s, WebSocket webSocket) {
        webSocket.send(searchTask.execute(s, OnlineUsers.get(webSocket)));
    }
}
