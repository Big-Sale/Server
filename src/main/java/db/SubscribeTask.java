package db;

import beans.SubscribeType;
import marshall.UnmarshallHandler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SubscribeTask extends DBtask {
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

    public static boolean hasPendingOrders(int id) {
        String test = "{call check_pending_orders(?)}";
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             CallableStatement stm = con.prepareCall(test)) {
            stm.setInt(1, id);
            try {
                stm.registerOutParameter(1, Types.BOOLEAN);
                stm.execute();
                return stm.getBoolean(1);
            } catch (SQLException e) {
                stm.close();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    protected String doExecute(String s, int userID) {
        SubscribeType type = UnmarshallHandler.unmarshall(s, SubscribeType.class);
        subscribe(type.payload, userID);
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
