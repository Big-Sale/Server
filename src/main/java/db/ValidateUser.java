package db;

import java.sql.*;

Lagpublic class ValidateUser extends DBtask {
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
    public String doExecute(String s) {
        return null;
    }
}
