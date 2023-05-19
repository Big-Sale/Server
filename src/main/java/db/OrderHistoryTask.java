package db;

import beans.OrderHistoryProduct;
import beans.OrderHistoryRequestType;
import beans.OrderHistoryType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marshall.UnmarshallHandler;

import java.sql.*;
import java.util.LinkedList;

public class OrderHistoryTask extends DBtask {
    @Override
    protected String doExecute(String s, int userID) {
        OrderHistoryRequestType ohrt = UnmarshallHandler.unmarshall(s, OrderHistoryRequestType.class);
        LinkedList<OrderHistoryProduct> list = fetchOrderHistory(ohrt);
        OrderHistoryType oht = new OrderHistoryType();
        oht.type = "order_history_request";
        oht.payload = list.toArray(new OrderHistoryProduct[0]);
        try {
            return new ObjectMapper().writeValueAsString(oht);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedList<OrderHistoryProduct> fetchOrderHistory(OrderHistoryRequestType ohrt) {
        LinkedList<OrderHistoryProduct> list = new LinkedList<>();
        try (Connection connection = db.DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = connection.prepareStatement("SELECT * FROM get_order_history(?, ?)")) {

            stm.setInt(1, ohrt.payload.userId);
            stm.setDate(2, Date.valueOf(ohrt.payload.date));

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    OrderHistoryProduct ohp = new OrderHistoryProduct();
                    ohp.productId = rs.getInt(1);
                    ohp.productType = rs.getString(2);
                    ohp.price = rs.getFloat(3);
                    ohp.colour = rs.getString(4);
                    ohp.condition = rs.getString(5);
                    ohp.productName = rs.getString(6);
                    ohp.seller = rs.getInt(7);
                    ohp.yearOfProduction = rs.getString(8);
                    ohp.dateOfPurchase = rs.getDate(9);
                    list.add(ohp);
                }
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
