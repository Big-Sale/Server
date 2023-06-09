package db;

import beans.BuyProductType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marshall.UnmarshallHandler;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BuyProductTask extends DBtask {

    @Override
    protected String doExecute(String s, int userId) {
        BuyProductType productType = UnmarshallHandler.unmarshall(s, BuyProductType.class);
        Integer[] products = productType.payload;
        buyProduct(userId, products);

        try {
            return new ObjectMapper().writeValueAsString(products);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buyProduct(int userId, Integer[] products) {
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("CALL insert_into_pending_orders(?, ?)")) {
            Array productArray = con.createArrayOf("integer", products);
            stm.setInt(1, userId);
            stm.setArray(2, productArray);
            stm.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
