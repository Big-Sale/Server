package db;

import beans.SignupType;
import beans.SignupUser;

import java.sql.*;
import marshall.UnmarshallHandler;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignupHandler extends DBtask {
    private static boolean validateSignup(SignupUser user) {
        Connection con = DataBaseConnection.getDatabaseConnection();
        String query = "select * from users where username = ? or email = ?;";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, user.username);
            stm.setString(2, user.email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                stm.close();
                rs.close();
                con.close();
                return false;
            } else {
                stm.close();
                rs.close();
                con.close();
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static int signup(SignupUser user) {
        if (validateSignup(user)) {
            Connection con = DataBaseConnection.getDatabaseConnection();
            String query = "select signup_user(?, ?, ?, ?, ?, ?);";
            PreparedStatement stm;
            try {
                stm = con.prepareStatement(query);
                stm.setString(1, user.firstName);
                stm.setString(2, user.lastName);
                stm.setDate(3, Date.valueOf(user.dateOfBirth));
                stm.setString(4, user.email);
                stm.setString(5, user.username);
                stm.setString(6, user.pw);
                ResultSet rs = stm.executeQuery();
                rs.next();
                int id = rs.getInt(1);
                rs.close();
                stm.close();
                con.close();
                return id;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return -1;
    }

    @Override
    public String doExecute(String s) {
        SignupType type = UnmarshallHandler.unmarshall(s, SignupType.class);
        SignupUser user = type.payload;
        String success;
        if(signup(user) != -1) {
            success = "true";
        } else {
            success = "false";
        }
        return "{\"type\":\"signup\",\"payload\":{\"success\":" + success + "}}";
    }

}
