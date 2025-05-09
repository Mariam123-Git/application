package application.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ParticipantsListController {

    @FXML private TableView<Participant> participantsTable;
    @FXML private TableColumn<Participant, Integer> idColumn;
    @FXML private TableColumn<Participant, String> nomColumn;
    @FXML private TableColumn<Participant, String> prenomColumn;
    @FXML private TableColumn<Participant, String> emailColumn;
    @FXML private Label eventTitleLabel;
    
    private int eventId;
    private String eventTitle;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    public void initData(int eventId, String eventTitle) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        eventTitleLabel.setText("Participants for: " + eventTitle);
        loadParticipants();
    }

    private void loadParticipants() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.ID_USER, u.USERNAME, u.EMAIL " +
                       "FROM USER u " +
                       "JOIN INSCRIPTION i ON u.ID_USER = i.ID_USER " +
                       "WHERE i.ID_EVENEMENT = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, eventId);
                ResultSet rs = stmt.executeQuery();
                
                participantsTable.getItems().clear();
                
                while (rs.next()) {
                    Participant participant = new Participant(
                        rs.getInt("ID_USER"),
                        rs.getString("USERNAME"),
                        "", // Prénom non disponible dans la table USER
                        rs.getString("EMAIL")
                    );
                    participantsTable.getItems().add(participant);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load participants: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goBack() {
        ((Stage) participantsTable.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}