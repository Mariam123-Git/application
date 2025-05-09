package application.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AuthService {
	public static boolean userExists(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            System.out.print("User Exists\n");
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Couldn't fund user\n");

            return false;
        }
    }
	public static boolean authenticate(String email, String password) {
	    String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, email);
	        stmt.setString(2, password); // à crypter plus tard
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            int id = rs.getInt("id");
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
        String query = "INSERT INTO users(username,email, password) VALUES (?,?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
        	stmt.setString(1, username);
        	stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	

}