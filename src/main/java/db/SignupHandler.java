package db;

import beans.SignupUser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            String query = "select signup_user('" + user.firstName + "', '" + user.lastName + "', '" + user.dateOfBirth + "', '" + user.email + "', '" + user.username + "', '" + user.pw + "');";
            Statement stm;
            try {
                stm = con.createStatement();
                ResultSet rs = stm.executeQuery(query);
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
