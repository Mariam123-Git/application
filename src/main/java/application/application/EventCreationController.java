package application.application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.*;

public class EventCreationController {

    @FXML private TextField eventTitle;
    @FXML private ComboBox<String> eventCategory;
    @FXML private ComboBox<String> eventCity;
    @FXML private ComboBox<String> eventAddress;
    @FXML private DatePicker eventStartDate;
    @FXML private TextField eventStartTime;
    @FXML private DatePicker eventEndDate;
    @FXML private TextField eventEndTime;
    @FXML private TextArea eventDescription;
    @FXML private TextField eventMaxPlaces;
    @FXML private Button uploadImageButton;
    @FXML private Label imageFileName;
    @FXML private Button createEventButton;
    
    private byte[] imageData;

    @FXML
    private void initialize() {
        // Initialiser les ComboBox
        eventCity.setItems(FXCollections.observableArrayList());
        eventAddress.setItems(FXCollections.observableArrayList());
        eventCategory.setItems(FXCollections.observableArrayList());
        
        // Charger les données
        loadCategories();
        loadCities();
        
        // Écouteur pour charger les adresses quand une ville est sélectionnée
        eventCity.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadAddressesForCity(newVal);
            }
        });
        
        // Dates par défaut
        eventStartDate.setValue(LocalDate.now());
        eventEndDate.setValue(LocalDate.now());
    }

    private void loadCategories() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT NOM FROM CATEGORIE")) {
            
            while (rs.next()) {
                eventCategory.getItems().add(rs.getString("NOM"));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les catégories");
        }
    }

    private void loadCities() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT VILLE FROM EVENEMENT WHERE VILLE IS NOT NULL")) {
            
            while (rs.next()) {
                eventCity.getItems().add(rs.getString("VILLE"));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les villes");
        }
    }

    private void loadAddressesForCity(String city) {
        eventAddress.getItems().clear();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT DISTINCT ADRESSE FROM EVENEMENT WHERE VILLE = ? AND ADRESSE IS NOT NULL")) {
            
            pstmt.setString(1, city);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                eventAddress.getItems().add(rs.getString("ADRESSE"));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les adresses");
        }
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                imageData = fis.readAllBytes();
                imageFileName.setText(file.getName());
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de la lecture du fichier");
            }
        }
    }

    @FXML
    private void handleCreateEventButtonAction() {
        saveEventDataToDatabase();
    }
    
    private void saveEventDataToDatabase() {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                showAlert("Erreur", "Impossible de se connecter à la base de données");
                return;
            }

            // Validation
            if (eventTitle.getText().isEmpty() || eventCategory.getValue() == null || 
                eventStartDate.getValue() == null || eventStartTime.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires");
                return;
            }

            // Dates
            LocalDateTime startDateTime = LocalDateTime.of(
                eventStartDate.getValue(), 
                LocalTime.parse(eventStartTime.getText())
            );
            
            LocalDateTime endDateTime = eventEndDate.getValue() != null && !eventEndTime.getText().isEmpty()
                ? LocalDateTime.of(eventEndDate.getValue(), LocalTime.parse(eventEndTime.getText()))
                : startDateTime;

            // Insertion
            try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO EVENEMENT (ID_CATEGORIE, ID_USER, TITRE, DATE_DEBUT, DATE_FIN, " +
                "VILLE, ADRESSE, DESCRIPTION, NB_PLACES_MAX, IMAGE, DATE_CREATION) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                
                pstmt.setInt(1, getCategoryId(eventCategory.getValue()));
                pstmt.setInt(2, getCurrentUserId());
                pstmt.setString(3, eventTitle.getText());
                pstmt.setTimestamp(4, Timestamp.valueOf(startDateTime));
                pstmt.setTimestamp(5, Timestamp.valueOf(endDateTime));
                pstmt.setString(6, eventCity.getValue());
                pstmt.setString(7, eventAddress.getValue());
                pstmt.setString(8, eventDescription.getText());
                pstmt.setInt(9, Integer.parseInt(eventMaxPlaces.getText()));
                pstmt.setBytes(10, imageData);
                pstmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                
                pstmt.executeUpdate();
                showAlert("Succès", "Événement créé avec succès!");
                clearForm();
                refreshData();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur: " + e.getMessage());
        }
    }

    private int getCategoryId(String categoryName) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT ID_CATEGORIE FROM CATEGORIE WHERE NOM = ?")) {
            
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("ID_CATEGORIE") : -1;
        }
    }

    private int getCurrentUserId() {
        // À remplacer par votre système d'authentification
        return 1;
    }

    private void clearForm() {
        eventTitle.clear();
        eventCategory.getSelectionModel().clearSelection();
        eventStartDate.setValue(LocalDate.now());
        eventStartTime.clear();
        eventEndDate.setValue(null);
        eventEndTime.clear();
        eventCity.getSelectionModel().clearSelection();
        eventAddress.getItems().clear();
        eventDescription.clear();
        eventMaxPlaces.clear();
        imageData = null;
        imageFileName.setText("");
    }

    private void refreshData() {
        loadCities();
        if (eventCity.getValue() != null) {
            loadAddressesForCity(eventCity.getValue());
        }
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }
}