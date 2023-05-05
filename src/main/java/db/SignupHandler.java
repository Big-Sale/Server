package db;

import beans.SignupUser;
import com.fasterxml.jackson.databind.JsonNode;

public class SignupHandler extends DBtask {
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

    @Override
    public JsonNode doExecute(JsonNode node) {
    return null;
    }
}
