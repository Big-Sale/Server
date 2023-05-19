package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubscribeTask extends DBtask {

    //TODO behöver vi dessa?
    public static void alert(String productName) {
        Connection connection = DataBaseConnection.getDatabaseConnection();
        String query = "select userid from subscription where productname = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
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

    public static boolean hasNotification(int id) {
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement("SELECT * FROM notifications WHERE userid = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasPending(int id) {
        try (Connection con = DataBaseConnection.getDatabaseConnection();
        PreparedStatement stm = con.prepareStatement("SELECT * FROM pending_orders WHERE userid = ?")) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String doExecute(String s, int userID) {
        subscribe(s, userID);
        return null;
    }

    private void subscribe(String s, int userID) {
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("INSERT INTO subscriptions VALUES (?, ?)")) {
            stm.setInt(1, userID);
            stm.setString(2, s);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
