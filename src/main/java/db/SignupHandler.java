package db;

import beans.SignupUser;

import java.sql.*;

public class SignupHandler {
    private static boolean validateSignup(SignupUser user) {
        Connection con = DataBaseConnection.getDatabaseConnection();
        String query = "select * from users where username = '" + user.username + "' or email = '" + user.email + "';";
        try {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(query);
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

}
