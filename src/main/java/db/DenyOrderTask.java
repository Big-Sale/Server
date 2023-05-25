package db;

import beans.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DenyOrderTask extends DBtask {

    @Override
    protected String doExecute(String s, int userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OrderRequest order = objectMapper.readValue(s, OrderRequest.class);
            int productId = order.payload.productId;
            int buyerId = order.payload.buyer;
            denyOrder(productId, buyerId);
            return new ObjectMapper().writeValueAsString(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void denyOrder(int productId, int buyerId) {
        String query = "DELETE FROM pending_orders WHERE userid = ? AND productid = ?";
        try (Connection connection = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, buyerId);
            stm.setInt(2, productId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
