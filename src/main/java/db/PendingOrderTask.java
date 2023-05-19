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
    public String doExecute(String s, int userID)  {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonReturn = null;
        try {
            jsonReturn = objectMapper.writeValueAsString(sellerPendingOrders(userID));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonReturn;

    }

    private String findSeller(int productID) {
        int seller = -1;
        try (Connection connection = DataBaseConnection.getDatabaseConnection()) {
            String query = "SELECT seller FROM products WHERE productid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                seller = rs.getInt("seller");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return String.valueOf(seller);
    }
    public PendingOrderType sellerPendingOrders(int userID) {
        LinkedList<PendingOrder> pendingOrders = new LinkedList<>();
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("SELECT * FROM pending_order_list(?)")) {
            stm.setInt(1, userID);
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
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
