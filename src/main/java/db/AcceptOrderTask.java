package db;

import beans.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AcceptOrderTask extends DBtask {

    @Override
    protected String doExecute(String s, int userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OrderRequest order = objectMapper.readValue(s, OrderRequest.class);
            int productId = order.payload.productId;
            int buyerId = order.payload.buyer;
            acceptOrder(productId, buyerId);
            return new ObjectMapper().writeValueAsString(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void acceptOrder(int productId, int userId) {
        try (Connection connection = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = connection.prepareStatement("CALL accept_pending_order(?, ?, ?)")) {
            stm.setInt(1, userId);
            stm.setInt(2, productId);
            stm.setDate(3, new Date(System.currentTimeMillis()));
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
