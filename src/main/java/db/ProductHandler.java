package db;

import beans.ProductWithId;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductHandler {

    public static void addProduct(ProductWithId product) {
        System.out.println("hello");
        Connection connection = DataBaseConnection.getDatabaseConnection();
        String QUERY = "call addproduct(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(QUERY);
            pstmt.setString(1, product.productType);
            pstmt.setFloat(2, product.price);
            pstmt.setString(3, product.colour);
            pstmt.setString(4, product.condition);
            pstmt.setString(5, product.status);
            pstmt.setString(6, product.productName);
            pstmt.setInt(7, product.userId);
            pstmt.setString(8, product.date);
            pstmt.execute();
            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
