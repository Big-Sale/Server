package db;

import beans.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DenyOrderTask extends DBtask {

     @Override
     public String doExecute(String s, int userID) {
          ObjectMapper objectMapper = new ObjectMapper();
          try {
               OrderRequest order = objectMapper.readValue(s, OrderRequest.class);
               int productID = order.payload.productId;
               int buyerID = order.payload.buyer;
               denyOrder(productID, buyerID);
               return new ObjectMapper().writeValueAsString(order);
          } catch (Exception e) {
               e.printStackTrace();
          }
          return null;
     }

     private void denyOrder(int productID, int buyerID) {
          String query = "DELETE FROM pending_orders WHERE userid = ? AND productid = ?";
          try (Connection connection = DataBaseConnection.getDatabaseConnection();
               PreparedStatement stm = connection.prepareStatement(query)) {
               stm.setInt(1, buyerID);
               stm.setInt(2, productID);
               stm.executeUpdate();
          } catch (SQLException e){
               throw new RuntimeException(e);
          }
     }
}
