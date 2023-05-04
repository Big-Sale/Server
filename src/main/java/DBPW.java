import java.sql.Connection;
import java.sql.DriverManager;

public class DBPW {
    public final static String pw = "zhupgiu8";


    public static Connection getDatabaseConnection() {
        String url = "jdbc:postgresql://pgserver.mau.se:5432/ak0856";
        String user = "ak0856";
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, pw);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
