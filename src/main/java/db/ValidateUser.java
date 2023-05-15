package db;

import java.sql.*;

public class ValidateUser {
    public static int validate(String username, String pw) {
        Connection con = DataBaseConnection.getDatabaseConnection();
        String query = "select pw, userid from users where username = ?;";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String dbPw = rs.getString("pw");
                if (dbPw.equals(pw)) {
                    int id = rs.getInt("userid");
                    stm.close();
                    rs.close();
                    con.close();
                    return id;
                }
            }
            stm.close();
            rs.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
}
