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
<<<<<<< HEAD
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
=======
        String sql = "SELECT e.ID_EVENEMENT, e.TITRE, e.DESCRIPTION, e.DATE_DEBUT, e.DATE_FIN, " +
                     "e.Ville, e.ADRESSE, e.NB_PLACES_MAX, e.IMAGE, c.nom as categorie, e.date_creation " +
                     "FROM evenement e " +
                     "JOIN categorie c ON e.ID_CATEGORIE = c.id_categorie";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDateTime debut = rs.getTimestamp("DATE_DEBUT").toLocalDateTime();
                LocalDateTime fin = rs.getTimestamp("DATE_FIN").toLocalDateTime();
                LocalDateTime dateCreation = rs.getTimestamp("date_creation").toLocalDateTime();

                events.add(new Evenement(
                    rs.getInt("ID_EVENEMENT"),
                    rs.getString("TITRE"),
                    rs.getString("DESCRIPTION"),
                    debut.toLocalDate(),
                    debut.toLocalTime(),
                    fin.toLocalTime(),
                    rs.getString("Ville"),
                    rs.getString("ADRESSE"),
                    rs.getInt("NB_PLACES_MAX"),
                    rs.getBytes("IMAGE"),
                    rs.getString("categorie"),
                    dateCreation.toLocalDate()
                ));
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd
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
<<<<<<< HEAD
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
=======
        StringBuilder sql = new StringBuilder(
            "SELECT e.ID_EVENEMENT, e.TITRE, e.DESCRIPTION, e.DATE_DEBUT, e.DATE_FIN, " +
            "e.Ville, e.ADRESSE, e.NB_PLACES_MAX, e.IMAGE, c.nom as categorie, e.date_creation " +
            "FROM evenement e " +
            "JOIN categorie c ON e.ID_CATEGORIE = c.id_categorie " +
            "WHERE e.DATE_DEBUT BETWEEN ? AND ?"
        );

        if (categorie != null && !categorie.isEmpty()) {
            sql.append(" AND c.nom = ?");
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setTimestamp(1, Timestamp.valueOf(dateDebut.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(dateFin.atTime(23, 59, 59)));

            if (categorie != null && !categorie.isEmpty()) {
                ps.setString(3, categorie);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDateTime debut = rs.getTimestamp("DATE_DEBUT").toLocalDateTime();
                LocalDateTime fin = rs.getTimestamp("DATE_FIN").toLocalDateTime();
                LocalDateTime dateCreation = rs.getTimestamp("date_creation").toLocalDateTime();

                events.add(new Evenement(
                    rs.getInt("ID_EVENEMENT"),
                    rs.getString("TITRE"),
                    rs.getString("DESCRIPTION"),
                    debut.toLocalDate(),
                    debut.toLocalTime(),
                    fin.toLocalTime(),
                    rs.getString("Ville"),
                    rs.getString("ADRESSE"),
                    rs.getInt("NB_PLACES_MAX"),
                    rs.getBytes("IMAGE"),
                    rs.getString("categorie"),
                    dateCreation.toLocalDate()
                ));
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return events;
<<<<<<< HEAD
    }}
=======
    }
}
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd
