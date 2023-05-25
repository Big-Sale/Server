package db;

import beans.Product;
import beans.ReturnProductType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * task for notifying subscriber
 */

public class FetchNotificationTask extends DBtask {
    @Override
    protected String doExecute(String s, int userId) {
        LinkedList<Product> list = fetchNotifications(userId);
        ReturnProductType returnProductType = new ReturnProductType();
        returnProductType.type = "notifications";
        returnProductType.payload = list.toArray(new Product[0]);
        try {
            return new ObjectMapper().writeValueAsString(returnProductType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedList<Product> fetchNotifications(int userId) {
        LinkedList<Product> list = new LinkedList<>();
        try {
            Connection connection = db.DataBaseConnection.getDatabaseConnection();
            String query = "select * from get_notifications(?);";
            PreparedStatement stm = connection.prepareStatement(query);
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.productId = rs.getInt(1);
                product.productType = rs.getString(2);
                product.price = rs.getFloat(3);
                product.colour = rs.getString(4);
                product.condition = rs.getString(5);
                product.productName = rs.getString(7);
                product.seller = rs.getInt(8);
                product.yearOfProduction = rs.getString(9);
                list.add(product);
            }
            rs.close();
            stm.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }
}
