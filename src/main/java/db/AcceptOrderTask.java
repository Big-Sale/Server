package db;

import beans.AcceptOrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marshall.UnmarshallHandler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AcceptOrderTask extends DBtask {

     @Override
     public String doExecute(String s, int userID) {
          AcceptOrderRequest order = UnmarshallHandler.unmarshall(s, AcceptOrderRequest.class);
          int productID = order.productID;
          acceptOrder(userID, productID);

          try {
               return new ObjectMapper().writeValueAsString(order);
          } catch (JsonProcessingException e) {
               e.printStackTrace();
          }
          return null;
     }

     private void acceptOrder(int userID, int productID) {
          try (Connection connection = DataBaseConnection.getDatabaseConnection();
               PreparedStatement stm = connection.prepareStatement("SELECT accept_pending_order(?, ?, ?)")) {
               stm.setInt(1, userID);
               stm.setInt(2, productID);
               stm.setDate(3, new Date(System.currentTimeMillis()));
          } catch (SQLException e){
               throw new RuntimeException(e);
          }
     }
}
