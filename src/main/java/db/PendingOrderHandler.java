package db;

import beans.PendingOrder;

import java.net.http.WebSocket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class PendingOrderHandler {

    public PendingOrder[] sellerPendingOrders(int userID) {
        LinkedList<PendingOrder> pendingOrders = new LinkedList<>();
         try (Connection con = DataBaseConnection.getDatabaseConnection();
         PreparedStatement stm = con.prepareStatement("SELECT * FROM selectpendingorders(?)")) {
            stm.setInt(1, userID);
             ResultSet rs = stm.executeQuery();
             while (rs.next()){
                 PendingOrder pendingOrder = new PendingOrder();
                 pendingOrder.userID = rs.getInt("userid");
                 pendingOrder.product = rs.getInt("productid");
                 pendingOrders.add(pendingOrder);
             }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


         return null;

    }

}
