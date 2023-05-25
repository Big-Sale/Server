package db;

import beans.LoginType;
import marshall.UnmarshallHandler;

public class LoginTask extends DBtask {

    @Override
    protected String doExecute(String s, int userID) {
        LoginType user = UnmarshallHandler.unmarshall(s, LoginType.class);
        int id = ValidateUserTask.validate(user.payload.username, user.payload.pw);
        return String.valueOf(id);
    }
}
