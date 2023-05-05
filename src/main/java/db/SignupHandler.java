package db;

import beans.SignupUser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignupHandler {
    private static boolean validateSignup(SignupUser user) {
        Connection con = DataBaseConnection.getDatabaseConnection();
        String query = "select * from users where username = " + user.username + " or email = " + user.email + ";";
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

    public static boolean signup(SignupUser user) {
        if (validateSignup(user)) {
            Connection con = DataBaseConnection.getDatabaseConnection();
            return true;
        }
        return false;
    }

}
