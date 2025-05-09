package application.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Insets;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EventDetailController {

    // Composants FXML
    @FXML private ImageView eventImage;
    @FXML private Text eventTitle;
    @FXML private Label dateValue;
    @FXML private Label lieuValue;
    @FXML private Button registerBtn;
    @FXML private Label statusBadge;
    @FXML private Label lieuDetailValue;
    @FXML private Label adresseValue;
    @FXML private Label orgValue;
    @FXML private Label capacityValue;
    @FXML private Label creationValue;
    @FXML private Label placesValue;
    @FXML private TextArea descArea;
    // Ajoutez ces nouveaux composants FXML
    @FXML private Button editEventBtn;
    @FXML private Button deleteEventBtn;

    private int organizerId; // Pour stocker l'ID de l'organisateur de l'événement
    private int eventId;
    private int currentUserId = -1; // Initialisation avec valeur par défaut
    private String currentUserEmail = "";

    // Méthode initData
    public void initData(int eventId, int userId, String userEmail) {
        this.eventId = eventId;
        this.currentUserId = userId;
        this.currentUserEmail = userEmail;
        
        // Ajouter un log pour vérifier les valeurs
        System.out.println("DEBUG - initData: eventId=" + eventId + ", userId=" + userId + ", email=" + userEmail);
        
        Platform.runLater(() -> {
            loadEventDetails();
        });
    }

    // Méthode pour gérer l'état du bouton d'inscription
    private void updateRegisterButtonState() {
        // Vérifier si l'utilisateur est connecté
        if (currentUserId <= 0 || currentUserEmail == null || currentUserEmail.isEmpty()) {
            registerBtn.setText("Connexion requise");
            registerBtn.setDisable(false);
            return;
        }
        
        // Vérifier si l'utilisateur est déjà inscrit
        if (isAlreadyRegistered()) {
            registerBtn.setText("Déjà inscrit");
            registerBtn.setDisable(true);
            return;
        }
        
        // Vérifier s'il reste des places
        int placesDisponibles = getAvailablePlaces();
        if (placesDisponibles <= 0) {
            registerBtn.setText("Complet");
            registerBtn.setDisable(true);
            return;
        }
        
        // Tout est OK, l'utilisateur peut s'inscrire
        registerBtn.setText("S'inscrire à l'événement");
        registerBtn.setDisable(false);
    }

    @FXML
    private void testMethod() {
        System.out.println("✅ Bouton test cliqué !");
    }

    @FXML
    private void registerForEvent(ActionEvent event) {
        // Vérifier si l'utilisateur est connecté
        if (currentUserId <= 0 || currentUserEmail == null || currentUserEmail.isEmpty()) {
            showAlert("Connexion requise", "Veuillez vous connecter pour vous inscrire", Alert.AlertType.WARNING);
            try {
                // Rediriger vers la page de connexion
                Parent root = FXMLLoader.load(getClass().getResource("/vues/Login.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Vérifier si l'utilisateur est déjà inscrit
        if (isAlreadyRegistered()) {
            showAlert("Déjà inscrit", "Vous êtes déjà inscrit à cet événement", Alert.AlertType.INFORMATION);
            registerBtn.setText("Déjà inscrit");
            registerBtn.setDisable(true);
            return;
        }

        // Vérifier les places disponibles
        System.out.println("DEBUG - registerForEvent called");
        System.out.println("Current user ID: " + currentUserId);
        System.out.println("Current user email: " + currentUserEmail);
        int placesDisponibles = getAvailablePlaces();
        System.out.println("DEBUG - Places disponibles: " + placesDisponibles);
        if (placesDisponibles <= 0) {
            showAlert("Complet", "Désolé, cet événement est complet", Alert.AlertType.WARNING);
            registerBtn.setDisable(true);
            updateEventStatus("clôturé");
            return;
        }

        // Directement créer l'inscription sans demander des informations supplémentaires
        createInscription();
    }
    
    private void loadEventDetails() {
        // Requête corrigée pour correspondre à la structure de la BDD
        String sql = "SELECT e.*, u.USERNAME AS organisateur FROM evenement e " +
                   "JOIN user u ON e.ID_USER = u.ID_USER " +
                   "WHERE e.ID_EVENEMENT = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                eventTitle.setText(rs.getString("TITRE"));
                dateValue.setText(formatDate(rs.getTimestamp("DATE_DEBUT")));
                lieuValue.setText(rs.getString("Ville"));
                lieuDetailValue.setText(rs.getString("Ville"));
                adresseValue.setText(rs.getString("ADRESSE"));
                orgValue.setText(rs.getString("organisateur"));
                capacityValue.setText(rs.getInt("NB_PLACES_MAX") + " places");
                creationValue.setText(formatDate(rs.getTimestamp("date_creation")));
                descArea.setText(rs.getString("DESCRIPTION"));
                
                byte[] imageData = rs.getBytes("IMAGE");
                if (imageData != null && imageData.length > 0) {
                    Image image = new Image(new ByteArrayInputStream(imageData));
                    eventImage.setImage(image);
                } else {
                    eventImage.setImage(new Image(getClass().getResourceAsStream("/images/default-event.png")));
                }
                
                updateAvailablePlaces();
                
                // Pour le statut, on va simplement utiliser une valeur par défaut car la colonne n'existe pas
                updateStatusDisplay("publié");
                
                if (currentUserId > 0 && isAlreadyRegistered()) {
                    registerBtn.setText("Déjà inscrit");
                    registerBtn.setDisable(true);
                }
                updateRegisterButtonState();
            } else {
                showAlert("Erreur", "Événement non trouvé", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les détails de l'événement: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "Date non spécifiée";
        return timestamp.toLocalDateTime().format(java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/yyyy à HH:mm"));
    }

    private void updateAvailablePlaces() {
        // Requête corrigée pour correspondre à la structure de la BDD
        String sql = "SELECT e.NB_PLACES_MAX - COUNT(i.ID_USER) AS places_restantes " +
                   "FROM evenement e LEFT JOIN inscription i ON e.ID_EVENEMENT = i.ID_EVENEMENT " +
                   "WHERE e.ID_EVENEMENT = ? AND (i.STATUT = 'confirmée' OR i.STATUT IS NULL) " +
                   "GROUP BY e.ID_EVENEMENT, e.NB_PLACES_MAX";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int placesRestantes = rs.getInt("places_restantes");
                    placesValue.setText(placesRestantes + " places disponibles");
                    
                    System.out.println("Places restantes: " + placesRestantes);
                    
                    if (placesRestantes <= 0) {
                        registerBtn.setDisable(true);
                        updateEventStatus("clôturé");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la vérification des places disponibles: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateEventStatus(String status) {
        // Comme il n'y a pas de colonne "statut" dans la table evenement, on va juste mettre à jour l'affichage
        updateStatusDisplay(status);
    }

    private void updateStatusDisplay(String status) {
        System.out.println("Status de l'événement: " + status);
        
        if (status == null) {
            statusBadge.setVisible(false);
            return;
        }
        
        switch (status.toLowerCase()) {
            case "clôturé":
                statusBadge.setText("COMPLET");
                statusBadge.setVisible(true);
                registerBtn.setDisable(true);
                break;
            case "annulé":
                statusBadge.setText("ANNULÉ");
                statusBadge.setVisible(true);
                registerBtn.setDisable(true);
                break;
            case "brouillon":
                statusBadge.setText("BROUILLON");
                statusBadge.setVisible(true);
                registerBtn.setDisable(true);
                break;
            case "publié":
                statusBadge.setVisible(false);
                // Ne pas désactiver le bouton ici, cela sera géré par updateAvailablePlaces()
                break;
            default:
                statusBadge.setText(status.toUpperCase());
                statusBadge.setVisible(true);
        }
    }

    private int getAvailablePlaces() {
        // Requête corrigée pour correspondre à la structure de la BDD
        String sql = "SELECT (e.NB_PLACES_MAX - COUNT(i.ID_USER)) AS places_disponibles " +
                   "FROM evenement e LEFT JOIN inscription i ON e.ID_EVENEMENT = i.ID_EVENEMENT " +
                   "AND i.STATUT = 'confirmée' " +
                   "WHERE e.ID_EVENEMENT = ? " +
                   "GROUP BY e.ID_EVENEMENT, e.NB_PLACES_MAX";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("places_disponibles") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void createInscription() {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Vérifier d'abord si une inscription existe déjà
            if (inscriptionExists(conn)) {
                // L'inscription existe déjà, mettre à jour le statut si nécessaire
                String updateSql = "UPDATE inscription SET STATUT = 'confirmée' " +
                                 "WHERE ID_EVENEMENT = ? AND ID_USER = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, eventId);
                    stmt.setInt(2, currentUserId);
                    stmt.executeUpdate();
                }
            } else {
                // Créer une nouvelle inscription
                String insertSql = "INSERT INTO inscription (ID_USER, ID_EVENEMENT, DATE_INSCRITPTION, STATUT) " +
                                 "VALUES (?, ?, ?, 'confirmée')";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setInt(1, currentUserId);
                    stmt.setInt(2, eventId);
                    stmt.setString(3, java.time.LocalDate.now().toString());
                  
                    
                    stmt.executeUpdate();
                }
            }
            
            conn.commit();
            
            showAlert("Succès", "Inscription réussie! Vous recevrez bientôt une notification.", Alert.AlertType.INFORMATION);
            registerBtn.setText("Déjà inscrit");
            registerBtn.setDisable(true);
            updateAvailablePlaces();
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showAlert("Erreur", "Erreur lors de l'inscription: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean inscriptionExists(Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM inscription WHERE ID_EVENEMENT = ? AND ID_USER = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, currentUserId);
            
            return stmt.executeQuery().next();
        }
    }

    private boolean isAlreadyRegistered() {
        // Requête corrigée pour correspondre à la structure de la BDD
        String sql = "SELECT 1 FROM inscription " +
                   "WHERE ID_EVENEMENT = ? AND ID_USER = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            stmt.setInt(2, currentUserId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @FXML
    private void goBack(ActionEvent event) {
        try {
            // 1. Charger le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/Home.fxml"));
            Parent root = loader.load();
            
            // 2. Récupérer le contrôleur de Home.fxml et lui transmettre les données
            HomeController homeController = loader.getController();
            homeController.setUserData(currentUserId, currentUserEmail); 
            
            // 3. Afficher la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à l'accueil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
private void viewParticipants(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/ParticipantsList.fxml"));
        Parent root = loader.load();
        
        ParticipantsListController controller = loader.getController();
        controller.initData(eventId, eventTitle.getText());
        
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Participants List - " + eventTitle.getText());
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Failed to load participants view: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}
     
    
    // Dans la méthode initData, ajoutez la vérification si l'utilisateur est l'organisateur
    public void initData(int eventId, int userId, String userEmail) {
        this.eventId = eventId;
        this.currentUserId = userId;
        this.currentUserEmail = userEmail;
        
        Platform.runLater(() -> {
            loadEventDetails();
            checkIfUserIsOrganizer(); // Nouvelle méthode pour vérifier les droits
        });
    }
    
    // Méthode pour vérifier si l'utilisateur est l'organisateur
    private void checkIfUserIsOrganizer() {
        if (currentUserId <= 0) return;
        
        String sql = "SELECT ID_USER FROM evenement WHERE ID_EVENEMENT = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                organizerId = rs.getInt("ID_USER");
                boolean isOrganizer = (organizerId == currentUserId);
                
                // Afficher/masquer les boutons en fonction
                editEventBtn.setVisible(isOrganizer);
                deleteEventBtn.setVisible(isOrganizer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Méthode pour modifier l'événement
    @FXML
    private void editEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/EditEvent.fxml"));
            Parent root = loader.load();
            
            EditEventController controller = loader.getController();
            controller.initData(eventId, currentUserId, currentUserEmail);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur d'événement", Alert.AlertType.ERROR);
        }
    }
    
    // Méthode pour supprimer l'événement
    @FXML
    private void deleteEvent(ActionEvent event) {
        // Confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer cet événement ?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer définitivement cet événement ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Connection conn = null;
            try {
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false);
                
                // D'abord supprimer les inscriptions associées
                String deleteInscriptions = "DELETE FROM inscription WHERE ID_EVENEMENT = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteInscriptions)) {
                    stmt.setInt(1, eventId);
                    stmt.executeUpdate();
                }
                
                // Puis supprimer l'événement
                String deleteEvent = "DELETE FROM evenement WHERE ID_EVENEMENT = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteEvent)) {
                    stmt.setInt(1, eventId);
                    int affectedRows = stmt.executeUpdate();
                    
                    if (affectedRows == 0) {
                        throw new SQLException("Événement non trouvé");
                    }
                }
                
                conn.commit();
                
                // Retour à l'accueil après suppression
                goBack(event);
                
            } catch (SQLException e) {
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                showAlert("Erreur", "Échec de la suppression : " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
