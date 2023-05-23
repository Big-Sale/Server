package db;

import beans.LoginType;
import marshall.UnmarshallHandler;
import java.sql.*;

public class ValidateUserTask extends DBtask {

    public static int validate(String username, String pw) {
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("SELECT pw, userid FROM users WHERE username = ?")) {
            stm.setString(1, username);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    String dbPw = rs.getString("pw");
                    if (dbPw.equals(pw)) {
                        return rs.getInt("userid");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    @Override
    protected String doExecute(String s, int userID) {
        LoginType user = UnmarshallHandler.unmarshall(s, LoginType.class);
        int id = ValidateUserTask.validate(user.payload.username, user.payload.pw);
        String toReturn;
        if (id != -1) {

            toReturn = "{\"type\":\"login\",\"payload\":{\"id\":" + id + "}}";
        } else {
            toReturn = "{\"type\":\"login\",\"payload\":{\"id\":-1}}";
        }
        return toReturn;
    }
}
