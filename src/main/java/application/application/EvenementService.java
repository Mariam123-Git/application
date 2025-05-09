package application.application;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvenementService {
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/eventmannagement", "root", "");
    }

    public List<Evenement> getAllEvents() {
        List<Evenement> events = new ArrayList<>();
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
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du filtrage des événements: " + e.getMessage());
        }
        return events;
    }
}
