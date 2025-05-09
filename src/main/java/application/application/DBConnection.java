// DBConnection.java
package application.application;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/eventmannagement";
<<<<<<< HEAD
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 
=======
    private static final String USER = "root"; // à adapter
    private static final String PASSWORD = ""; // à adapter
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
