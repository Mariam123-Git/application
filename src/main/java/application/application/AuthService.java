package application.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
<<<<<<< HEAD
	public static boolean userExists(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
=======

    public static User authenticate(String email, String password) {
        String sql = "SELECT ID_USER, USERNAME, EMAIL, ROLE FROM user WHERE EMAIL = ? AND PASSWORD = ?";
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password); // À remplacer par un hash en production

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("ID_USER"),
                    rs.getString("USERNAME"),
                    rs.getString("EMAIL"),
                    rs.getString("ROLE")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean userExists(String email) {
        String query = "SELECT ID_USER FROM user WHERE EMAIL = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
<<<<<<< HEAD
	public static boolean authenticate(String email, String password) {
	    String sql = "SELECT * FROM user WHERE email = ? AND password = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, email);
	        stmt.setString(2, password); // à crypter plus tard
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            int id = rs.getInt("id_user");
	            String userName = rs.getString("username");
	            Session.idUser = id;
	            Session.UserName = userName;
	            return true;
	        } else {
	            return false;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public static void register(String username,String email, String password) {
        String query = "INSERT INTO user(username,email, password) VALUES (?,?, ?)";
=======

    public static boolean register(String username, String email, String password) {
        String query = "INSERT INTO user(USERNAME, EMAIL, PASSWORD, ROLE, DATE_CREATION) VALUES (?, ?, ?, ?, NOW())";
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password); // À hasher en production
            stmt.setString(4, "user");   // Rôle par défaut

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
