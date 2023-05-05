package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubscriptionHandler {

    public static void alert(String productName) {
        Connection connection = DataBaseConnection.getDatabaseConnection();
        String QUERY = "select userid from subscription where productname = ?";
        try {
            PreparedStatement pstmt = null;
            try {
                pstmt = connection.prepareStatement(QUERY);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            pstmt.setString(1, productName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("userid");

            }
            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}