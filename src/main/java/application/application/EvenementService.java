package application.application;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EvenementService {
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/eventmannagement", "root", "");
    }

    public List<Evenement> getAllEvents() {
        List<Evenement> events = new ArrayList<>();
        String sql = "SELECT e.id_evenement, e.titre, e.description, e.date_debut, e.date_fin, " +
                    "e.lieu, e.adresse, e.nb_places_max, e.image_url, c.nom as categorie, e.statut " +
                    "FROM evenement e " +
                    "JOIN categorie c ON e.id_categorie = c.id_categorie";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDateTime debut = rs.getTimestamp("date_debut").toLocalDateTime();
                LocalDateTime fin = rs.getTimestamp("date_fin").toLocalDateTime();
                
                events.add(new Evenement(
                    rs.getInt("id_evenement"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    debut.toLocalDate(),
                    debut.toLocalTime(),
                    fin.toLocalTime(),
                    rs.getString("lieu"),
                    rs.getString("adresse"),
                    rs.getInt("nb_places_max"),
                    rs.getString("image_url"),
                    rs.getString("categorie"),
                    rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des événements: " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT nom FROM categorie";

        try (Connection conn = connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories: " + e.getMessage());
        }
        return categories;
    }

    public List<Evenement> getFilteredEvents(LocalDate dateDebut, LocalDate dateFin, String categorie) {
        List<Evenement> events = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT e.id_evenement, e.titre, e.description, e.date_debut, e.date_fin, " +
            "e.lieu, e.adresse, e.nb_places_max, e.image_url, c.nom as categorie, e.statut " +
            "FROM evenement e " +
            "JOIN categorie c ON e.id_categorie = c.id_categorie " +
            "WHERE e.date_debut BETWEEN ? AND ?"
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
                LocalDateTime debut = rs.getTimestamp("date_debut").toLocalDateTime();
                LocalDateTime fin = rs.getTimestamp("date_fin").toLocalDateTime();
                
                events.add(new Evenement(
                    rs.getInt("id_evenement"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    debut.toLocalDate(),
                    debut.toLocalTime(),
                    fin.toLocalTime(),
                    rs.getString("lieu"),
                    rs.getString("adresse"),
                    rs.getInt("nb_places_max"),
                    rs.getString("image_url"),
                    rs.getString("categorie"),
                    rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du filtrage des événements: " + e.getMessage());
        }
        return events;
    }
}