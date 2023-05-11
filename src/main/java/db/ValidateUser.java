package db;

import java.sql.*;

public class ValidateUser {
    public static int validate(String username, String pw) {
        Connection con = DataBaseConnection.getDatabaseConnection();
        String query = "select pw, userid from users where username = '" + username +"';";
        try {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(query);
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
}
