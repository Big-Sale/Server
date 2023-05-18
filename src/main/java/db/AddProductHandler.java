package db;

import beans.ProductType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marshall.UnmarshallHandler;
import beans.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;

public class AddProductHandler extends DBtask {



     private int addProduct(Product product) {
          Connection connection = DataBaseConnection.getDatabaseConnection();
          String query = "select insert_product(?, ?, ?, ?, ?, ?, ?, ?)";
          try {
               PreparedStatement pstmt = connection.prepareStatement(query);
               pstmt.setString(1, product.productType);
               pstmt.setFloat(2, product.price);
               pstmt.setString(3, product.colour);
               pstmt.setString(4, product.condition);
               pstmt.setString(5, product.status);
               pstmt.setString(6, product.productName);
               pstmt.setInt(7, product.seller);
               pstmt.setString(8, product.date);
               ResultSet rs = pstmt.executeQuery();
               int id = -1;
               if (rs.next()) {
                    id = rs.getInt(1);
               }
               rs.close();
               pstmt.close();
               connection.close();
               return id;
          } catch (SQLException e) {
               throw new RuntimeException(e);
          }
     }

    /**
     * @param s json object
     * @param userID
     * @return product object as json string
     */
    @Override
    public String doExecute(String s, int userID) {
        ProductType product = UnmarshallHandler.unmarshall(s, ProductType.class);
        product.payload.seller = userID;
        product.payload.status = "available";
        product.payload.productId = addProduct(product.payload);

        try {
            return new ObjectMapper().writeValueAsString(product.payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
