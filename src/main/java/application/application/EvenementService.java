package application.application;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import application.application.Evenement;
import application.application.DBConnection;
public class EvenementService {
	/**
     * Récupère tous les événements à venir de la base de données
     * @return Liste des événements à venir
     */
    public List<Evenement> getUpcomingEvents() {
        List<Evenement> events = new ArrayList<>();
        String query = "SELECT e.*, c.NOM as CATEGORIE_NOM FROM EVENEMENT e " +
                       "LEFT JOIN CATEGORIE c ON e.ID_CATEGORIE = c.ID_CATEGORIE " +
                       "WHERE e.DATE_DEBUT > NOW() " +
                       "ORDER BY e.DATE_DEBUT ASC";
                       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Evenement event = new Evenement();
                event.setId(rs.getInt("ID_EVENEMENT"));
                event.setTitre(rs.getString("TITRE"));
                event.setDateDebut(rs.getTimestamp("DATE_DEBUT").toLocalDateTime());
                event.setDateFin(rs.getTimestamp("DATE_FIN").toLocalDateTime());
                event.setVille(rs.getString("VILLE"));
                event.setAdresse(rs.getString("ADRESSE"));
                event.setDescription(rs.getString("DESCRIPTION"));
                event.setNbPlacesMax(rs.getInt("NB_PLACES_MAX"));
                event.setCategorieNom(rs.getString("CATEGORIE_NOM"));
                // Si vous avez besoin de l'image, vous pouvez également la récupérer
                // event.setImage(rs.getBlob("IMAGE"));
                
                events.add(event);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return events;
    }
    
    /**
     * Récupère les événements auxquels un utilisateur est inscrit
     * @param userId ID de l'utilisateur
     * @return Liste des événements auxquels l'utilisateur est inscrit
     */
    public List<Evenement> getUserRegisteredEvents(int userId) {
        List<Evenement> events = new ArrayList<>();
        String query = "SELECT e.*, c.NOM as CATEGORIE_NOM, i.STATUT FROM EVENEMENT e " +
                       "LEFT JOIN CATEGORIE c ON e.ID_CATEGORIE = c.ID_CATEGORIE " +
                       "JOIN INSCRIPTION i ON e.ID_EVENEMENT = i.ID_EVENEMENT " +
                       "WHERE i.ID_USER = ? " +
                       "ORDER BY e.DATE_DEBUT ASC";
                       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Evenement event = new Evenement();
                    event.setId(rs.getInt("ID_EVENEMENT"));
                    event.setTitre(rs.getString("TITRE"));
                    event.setDateDebut(rs.getTimestamp("DATE_DEBUT").toLocalDateTime());
                    event.setDateFin(rs.getTimestamp("DATE_FIN").toLocalDateTime());
                    event.setVille(rs.getString("VILLE"));
                    event.setAdresse(rs.getString("ADRESSE"));
                    event.setDescription(rs.getString("DESCRIPTION"));
                    event.setNbPlacesMax(rs.getInt("NB_PLACES_MAX"));
                    event.setCategorieNom(rs.getString("CATEGORIE_NOM"));
                    event.setStatutInscription(rs.getString("STATUT"));
                    
                    events.add(event);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return events;
    }}