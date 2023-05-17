package db;

import beans.ProductType;
import marshall.UnmarshallHandler;
import beans.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;

public class AddProductHandler extends DBtask {

     public void buyProduct(int userID, Integer[] products) {
          try (Connection con = DataBaseConnection.getDatabaseConnection();
          PreparedStatement stm = con.prepareStatement("CALL insert_into_pending_orders(?, ?)")) {
               Array productArray = con.createArrayOf("integer", products);
               stm.setInt(1, userID);
               stm.setArray(2, productArray);
               stm.execute();
          } catch (SQLException e) {
               throw new RuntimeException(e);
          }
     }

     public static int addProduct(Product product) {
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

    @Override
    public String doExecute(String s, String userID) {
        ProductType product = UnmarshallHandler.unmarshall(s, ProductType.class);
        product.payload.seller = Integer.parseInt(userID);
        product.payload.status = "available";
        String productID = String.valueOf(addProduct(product.payload));
        product.payload.productId = productID;

        return productID; //todo strängsplitta?
    }
}
