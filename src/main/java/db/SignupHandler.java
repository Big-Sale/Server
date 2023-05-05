package db;

import beans.SignupType;
import beans.SignupUser;
import marshall.UnmarshallHandler;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignupHandler extends DBtask {
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
            // TODO gör procedure
            return true;
        }
        return false;
    }

    @Override
    public String doExecute(String s) {
        SignupType type = UnmarshallHandler.unmarshall(s, SignupType.class);
        SignupUser user = type.payload;
        String success;
        if(signup(user)) {
            success = "true";
        } else {
            success = "false";
        }
        return "{\"type\":\"signup\",\"payload\":{\"success\":" + success + "}}";
    }

}
