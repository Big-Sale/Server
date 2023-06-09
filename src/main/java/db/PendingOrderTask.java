package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import beans.PendingOrder;
import beans.PendingOrderType;
import beans.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class PendingOrderTask extends DBtask {

    @Override
    protected String doExecute(String s, int userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonReturn;
        try {
            jsonReturn = objectMapper.writeValueAsString(sellerPendingOrders(userId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonReturn;
    }

    public PendingOrderType sellerPendingOrders(int userId) {
        LinkedList<PendingOrder> pendingOrders = new LinkedList<>();
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("SELECT * FROM pending_order_list(?)")) {
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                PendingOrder pendingOrder = new PendingOrder();
                pendingOrder.product = new Product();
                pendingOrder.buyerId = rs.getInt(1);
                pendingOrder.buyer = rs.getString(2);
                pendingOrder.product.productId = rs.getInt(3);
                pendingOrder.product.productType = rs.getString(4);
                pendingOrder.product.price = rs.getFloat(5);
                pendingOrder.product.colour = rs.getString(6);
                pendingOrder.product.condition = rs.getString(7);
                pendingOrder.product.productName = rs.getString(8);
                pendingOrder.product.seller = rs.getInt(9);
                pendingOrder.product.yearOfProduction = rs.getString(10);
                pendingOrders.add(pendingOrder);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        PendingOrderType pot = new PendingOrderType();
        pot.type = "pending_orders";
        pot.payload = pendingOrders.toArray(new PendingOrder[0]);
        return pot;
    }
}
