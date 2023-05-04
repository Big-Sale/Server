package db;

import beans.SignupUser;

public class SignupHandler {
    private static boolean validateSignup(SignupUser user) {
        //validate username is unique
        return true;
    }

    public static boolean signup(SignupUser user) {
        if (validateSignup(user)) {
            //add user to database
            return true;
        }
        return false;
    }

}
