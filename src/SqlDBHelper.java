import java.sql.*;

public class SqlDBHelper{

    String userName = "root";
    String password = "12345";
    Connection connection;

    public Connection getConnection(){
        try {
            connection= DriverManager.getConnection("jdbc:mysql://@localhost:3306/studentclub?" +
                    "user="+userName+"&password="+password);
        } catch (SQLException ex) {
            printErrors(ex);
        }
        return connection;
    }

    public void printErrors(SQLException ex){
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());

    }


}
