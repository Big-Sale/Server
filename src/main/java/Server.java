import OnlineUsers.OnlineUsers;
import beans.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.*;
import logger.Logger;
import marshall.UnmarshallHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class Server extends WebSocketServer {
    PendingOrderTask pendingOrderTask = new PendingOrderTask();
    SearchTask searchTask = new SearchTask();

    public Server() {
        super(new InetSocketAddress(8080));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        Logger.connectLog(webSocket.getRemoteSocketAddress().toString());
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
        Logger.disconnectLog(webSocket.getRemoteSocketAddress().toString());
        OnlineUsers.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String json) {
        Logger.messageLog(webSocket.getRemoteSocketAddress().toString());
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
            case "orderHistoryRequest" -> orderHistory(json, webSocket);
            case "buyProduct" -> buyProduct(json, webSocket);
            case "removeNotification" -> removeNotification(rootNode.get("payload").asText(), webSocket);
            case "subscribe" -> subscribe(json, webSocket);
            case "acceptProductSale" -> acceptOrder(json, webSocket);
            case "denyProductSale" -> denyOrder(json, webSocket);
            case "pendingOrderRequest" -> getPendingOrdersPerUser(json, webSocket);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void login(String s, WebSocket webSocket) {
        LoginTask lt = new LoginTask();
        String toReturn = lt.execute(s, -1);
        int id = Integer.parseInt(toReturn);
        if (id != -1) {
            OnlineUsers.put(id, webSocket);
            boolean notify = SubscribeTask.hasNotification(id) || SubscribeTask.hasPendingOrders(id);
            webSocket.send("{\"type\":\"login\",\"payload\":{\"id\":" + id + ",\"notify\":" + notify + "}}");
        } else {
            webSocket.send("{\"type\":\"login\",\"payload\":{\"id\":" + id + "}}");
        }
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

    private void notifications(WebSocket webSocket) {
        FetchNotificationTask fnt = new FetchNotificationTask();
        int id = OnlineUsers.get(webSocket);
        webSocket.send(fnt.execute(null, id));
    }

    private void addProduct(String json, WebSocket webSocket) {
        AddProductTask aph = new AddProductTask();
        int id = OnlineUsers.get(webSocket);
        String jsonProduct = aph.execute(json, id);
        checkNotifications(jsonProduct);
    }

    private void checkNotifications(String jsonProduct) {
        CheckNotificationTask cnt = new CheckNotificationTask();
        String jsonList = cnt.execute(jsonProduct, -1);
        LinkedList<Integer> userIDs = UnmarshallHandler.unmarshall(jsonList, LinkedList.class);
        Product product = UnmarshallHandler.unmarshall(jsonProduct, Product.class);
        for(Integer userID : userIDs) {
            if (OnlineUsers.contains(userID)) {
                WebSocket webSocket = OnlineUsers.get(userID);
                if (webSocket != null && userID != product.seller) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    NotificationType notificationType = new NotificationType();
                    notificationType.type = "subscribed_product";
                    notificationType.payload = product;
                    String json = null;
                    try {
                        json = objectMapper.writeValueAsString(notificationType);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    webSocket.send(json);
                }
            }
        }
    }

    private void orderHistory(String json, WebSocket webSocket) {
        OrderHistoryTask oht = new OrderHistoryTask();
        webSocket.send(oht.execute(json, OnlineUsers.get(webSocket)));
    }

    private void buyProduct(String json, WebSocket webSocket) {
        BuyProductTask bph = new BuyProductTask();
        int id = OnlineUsers.get(webSocket);
        String jsonProducts = bph.execute(json, id);

        FindSellerTask fst = new FindSellerTask();

        Integer[] products = UnmarshallHandler.unmarshall(jsonProducts, Integer[].class);
        for (Integer i : products) {
            String pendingOrder = fst.execute(String.valueOf(i), id);
            PendingOrder p = UnmarshallHandler.unmarshall(pendingOrder, PendingOrder.class);
            if (OnlineUsers.contains(p.product.seller)){
                PendingOrderNotify notificationType = new PendingOrderNotify();
                notificationType.type = "pending_order_notification";
                notificationType.payload = p;
                try {
                    OnlineUsers.get(p.product.seller).send(new ObjectMapper().writeValueAsString(notificationType));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void removeNotification(String productId, WebSocket webSocket) {
        RemoveNotificationTask rnt = new RemoveNotificationTask();
        int userId = OnlineUsers.get(webSocket);
        rnt.execute(productId, userId);
    }

    private void subscribe(String payload, WebSocket webSocket) {
        SubscribeTask st = new SubscribeTask();
        int userId = OnlineUsers.get(webSocket);
        st.execute(payload, userId);
    }

    private void acceptOrder(String json, WebSocket webSocket) {
        int id = OnlineUsers.get(webSocket);
        AcceptOrderTask acceptOrderTask = new AcceptOrderTask();
        acceptOrderTask.execute(json, id);
    }

    private void denyOrder(String json, WebSocket webSocket) {
        int id = OnlineUsers.get((webSocket));
        DenyOrderTask denyOrderTask = new DenyOrderTask();
        denyOrderTask.execute(json, id);
    }

    private void getPendingOrdersPerUser(String json, WebSocket webSocket){
        int id = OnlineUsers.get(webSocket);
        String jsonReturn = pendingOrderTask.execute(json, id);
        webSocket.send(jsonReturn);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Logger.errorLog(webSocket.getRemoteSocketAddress().toString(), e);
        OnlineUsers.remove(webSocket);
        webSocket.close();
    }
}