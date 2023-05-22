package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveNotificationTask extends DBtask {
    @Override
    protected String doExecute(String s, int userId) {
        removeNotification(userId, Integer.parseInt(s));
        return null;
    }
    private void removeNotification(int userId, int productId) {
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("DELETE FROM notifications WHERE userid = ? AND productid = ?")) {
            stm.setInt(1, userId);
            stm.setInt(2, productId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
