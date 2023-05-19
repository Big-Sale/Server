package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindSellerTask extends DBtask {
    @Override
    public String doExecute(String s, int userID) {
        return findSeller(Integer.parseInt(s));
    }

    private String findSeller(int productID) {
        int seller = -1;
        try (Connection connection = DataBaseConnection.getDatabaseConnection()) {
            String query = "SELECT seller FROM products WHERE productid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, productID);
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
