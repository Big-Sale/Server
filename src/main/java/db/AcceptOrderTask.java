package db;

import beans.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AcceptOrderTask extends DBtask {

     @Override
     protected String doExecute(String s, int userID) {
          ObjectMapper objectMapper = new ObjectMapper();
          try {
               //AcceptOrderRequest order = UnmarshallHandler.unmarshall(s, AcceptOrderRequest.class);
               OrderRequest order = objectMapper.readValue(s, OrderRequest.class);
               int productID = order.payload.productId;
               int buyerID = order.payload.buyer;
               acceptOrder(productID, buyerID);
               return new ObjectMapper().writeValueAsString(order);
          } catch (Exception e) {
               e.printStackTrace();
          }
          return null;
     }

     private void acceptOrder(int productID, int userID) {
          try (Connection connection = DataBaseConnection.getDatabaseConnection();
               PreparedStatement stm = connection.prepareStatement("CALL accept_pending_order(?, ?, ?)")) {
               stm.setInt(1, userID);
               stm.setInt(2, productID);
               stm.setDate(3, new Date(System.currentTimeMillis()));
               stm.executeUpdate();
          } catch (SQLException e){
               throw new RuntimeException(e);
          }
     }
}
