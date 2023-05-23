package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindSellerTask extends DBtask {

    @Override
    protected String doExecute(String s, int userId)  {
        return findSeller(Integer.parseInt(s));

    }

    private String findSeller(int productId) {
        int seller = -1;
        try (Connection connection = DataBaseConnection.getDatabaseConnection()) {
            String query = "SELECT seller FROM products WHERE productid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                seller = rs.getInt("seller");
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return String.valueOf(seller);
    }
}
