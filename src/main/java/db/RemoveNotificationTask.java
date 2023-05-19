package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveNotificationTask extends DBtask {
    @Override
    public String doExecute(String s, int userID) {
        removeNotification(userID, Integer.parseInt(s));
        return null;
    }
    private void removeNotification(int userID, int productID) {
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("DELETE FROM notifications WHERE userid = ? AND productid = ?")) {
            stm.setInt(1, userID);
            stm.setInt(2, productID);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
