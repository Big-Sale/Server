package db;

import beans.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marshall.UnmarshallHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class CheckNotificationTask extends DBtask {

    @Override
    protected String doExecute(String s, int userId) {
        Product product = UnmarshallHandler.unmarshall(s, Product.class);
        LinkedList<Integer> userIds = getSubscribeUserIds(product);
        if (userIds != null) {
            addNotifications(product, userIds);
        }
        try {
            return new ObjectMapper().writeValueAsString(userIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedList<Integer> getSubscribeUserIds(Product product) {
        LinkedList<Integer> userIds = new LinkedList<>();
        try (Connection conn = DataBaseConnection.getDatabaseConnection()) {
            String query = "select userid from subscriptions where productname = ?;";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, product.productType);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                userIds.add(id);
            }
            rs.close();
            stm.close();
            return userIds;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private void addNotifications(Product product, LinkedList<Integer> userIds) {
        String q = "call add_notification(?, ?);";
        try (Connection conn = DataBaseConnection.getDatabaseConnection()) {
            PreparedStatement st = conn.prepareStatement(q);
            st.setArray(1, conn.createArrayOf("integer", userIds.toArray()));
            st.setInt(2, product.productId);
            st.execute();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
