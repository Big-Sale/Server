package db;

import beans.Product;

import java.sql.*;

public class ProductHandler {

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
}
