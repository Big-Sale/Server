package db;

import beans.PendingOrder;
import beans.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marshall.UnmarshallHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindSellerTask extends DBtask {

    @Override
    protected String doExecute(String s, int userId)  {
        return findSeller(Integer.parseInt(s), userId);

    }

    private String findSeller(int productId, int userId) {
        PendingOrder p = new PendingOrder();
        p.product = new Product();
        p.buyerId = userId;
        try (Connection connection = DataBaseConnection.getDatabaseConnection()) {
            String query = "SELECT * FROM get_product_with_buyer_username(?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2,userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                p.buyer = rs.getString("buyer_username");
                p.product.productId = rs.getInt("productid");
                p.product.productType = rs.getString("producttype");
                p.product.status = rs.getString("status");
                p.product.price = rs.getFloat("price");
                p.product.seller = rs.getInt("seller");
                p.product.colour = rs.getString("colour");
                p.product.productName = rs.getString("productname");
                p.product.yearOfProduction = rs.getString("yearofproduction");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(p);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
