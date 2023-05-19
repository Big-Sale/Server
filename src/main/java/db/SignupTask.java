package db;

import beans.SignupType;
import beans.SignupUser;

import java.sql.*;
import marshall.UnmarshallHandler;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignupTask extends DBtask {
    private static boolean validateSignup(SignupUser user) {
        try (Connection con = DataBaseConnection.getDatabaseConnection();
             PreparedStatement stm = con.prepareStatement("SELECT * FROM users WHERE username = ? OR email = ?")) {
            stm.setString(1, user.username);
            stm.setString(2, user.email);
            try (ResultSet rs = stm.executeQuery()) {
                return !rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static int signup(SignupUser user) {
        if (validateSignup(user)) {
            try (Connection con = DataBaseConnection.getDatabaseConnection();
                 PreparedStatement stm = con.prepareStatement("select signup_user(?, ?, ?, ?, ?, ?);")) {
                stm.setString(1, user.firstName);
                stm.setString(2, user.lastName);
                stm.setDate(3, Date.valueOf(user.dateOfBirth));
                stm.setString(4, user.email);
                stm.setString(5, user.username);
                stm.setString(6, user.pw);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return -1;
    }

    @Override
    protected String doExecute(String s, int userID) {
        SignupType type = UnmarshallHandler.unmarshall(s, SignupType.class);
        SignupUser user = type.payload;
        String success;
        int id = signup(user);
        return String.valueOf(id);
    }

}
