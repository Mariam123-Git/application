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

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
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
   

    private int eventId;
    private int currentUserId = -1; // Initialisation avec valeur par défaut
    private String currentUserEmail = "";

    // UNE SEULE méthode initData
 // 1. Assurez-vous que l'initialisation des données utilisateur est correcte dans initData
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

    // 2. Ajouter une méthode pour gérer l'état du bouton d'inscription
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

    // 3. Modifier registerForEvent pour inclure plus de logs et vérifications
    @FXML
    private void registerForEvent(ActionEvent event) {
    	 System.out.println("🎉 Bouton cliqué !");
        System.out.println("DEBUG - Bouton cliqué! UserId=" + currentUserId + ", Email=" + currentUserEmail);
        
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

        // Continuer avec la logique existante...
        if (isAlreadyRegistered()) {
            showAlert("Déjà inscrit", "Vous êtes déjà inscrit à cet événement", Alert.AlertType.INFORMATION);
            registerBtn.setText("Déjà inscrit");
            registerBtn.setDisable(true);
            return;
        }

        int placesDisponibles = getAvailablePlaces();
        System.out.println("DEBUG - Places disponibles: " + placesDisponibles);
        if (placesDisponibles <= 0) {
            showAlert("Complet", "Désolé, cet événement est complet", Alert.AlertType.WARNING);
            registerBtn.setDisable(true);
            updateEventStatus("clôturé");
            return;
        }

        checkUserIsParticipant();
    }
    private void loadEventDetails() {
        String sql = "SELECT e.*, a.nom AS organisateur FROM evenement e " +
                   "JOIN administrateur a ON e.id_admin = a.id_admin " +
                   "WHERE e.id_evenement = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Texte et données simples
                eventTitle.setText(rs.getString("titre"));
                dateValue.setText(formatDate(rs.getTimestamp("date_debut")));
                lieuValue.setText(rs.getString("lieu"));
                lieuDetailValue.setText(rs.getString("lieu"));
                adresseValue.setText(rs.getString("adresse"));
                orgValue.setText(rs.getString("organisateur"));
                capacityValue.setText(rs.getInt("nb_places_max") + " places");
                creationValue.setText(formatDate(rs.getTimestamp("date_creation")));
                descArea.setText(rs.getString("description"));
                
                // Image BLOB
                byte[] imageData = rs.getBytes("image");
                if (imageData != null && imageData.length > 0) {
                    Image image = new Image(new ByteArrayInputStream(imageData));
                    eventImage.setImage(image);
                } else {
                    // Image par défaut si aucune image n'est disponible
                    eventImage.setImage(new Image(getClass().getResourceAsStream("/images/default-event.png")));
                }
                
                // Places disponibles
                updateAvailablePlaces();
                
                // Statut de l'événement
                updateStatusDisplay(rs.getString("statut"));
                
                // Vérifier si l'utilisateur est déjà inscrit
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
        String sql = "SELECT e.nb_places_max - COUNT(i.id_inscription) AS places_restantes " +
                   "FROM evenement e LEFT JOIN inscription i ON e.id_evenement = i.id_evenement " +
                   "WHERE e.id_evenement = ? AND (i.statut = 'confirmée' OR i.statut IS NULL) " +
                   "GROUP BY e.id_evenement, e.nb_places_max";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int placesRestantes = rs.getInt("places_restantes");
                    placesValue.setText(placesRestantes + " places disponibles");
                    
                    // Debug
                    System.out.println("Places restantes: " + placesRestantes);
                    
                    // Désactiver le bouton si plus de places
                    if (placesRestantes <= 0) {
                        registerBtn.setDisable(true);
                        // Mettre à jour le statut si complet
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
        String sql = "UPDATE evenement SET statut = ? WHERE id_evenement = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
            
            // Mettre à jour l'affichage
            updateStatusDisplay(status);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la mise à jour du statut: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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
        String sql = "SELECT (e.nb_places_max - COUNT(i.id_inscription)) AS places_disponibles " +
                   "FROM evenement e LEFT JOIN inscription i ON e.id_evenement = i.id_evenement " +
                   "AND i.statut = 'confirmée' " +
                   "WHERE e.id_evenement = ? " +
                   "GROUP BY e.id_evenement, e.nb_places_max";
        
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

    private void checkUserIsParticipant() {
        String sql = "SELECT id_participant FROM participant WHERE id_user = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, currentUserId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Utilisateur déjà participant
                int participantId = rs.getInt("id_participant");
                registerExistingParticipant(participantId);
            } else {
                // Nouveau participant
                showParticipantForm();
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur de vérification: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Ajout de la méthode manquante
    private void registerExistingParticipant(int participantId) {
        try (Connection conn = DBConnection.getConnection()) {
            createInscription(conn, participantId);
            
            showAlert("Succès", "Inscription réussie!", Alert.AlertType.INFORMATION);
            registerBtn.setText("Déjà inscrit");
            registerBtn.setDisable(true);
            updateAvailablePlaces();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'inscription: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showParticipantForm() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Compléter votre profil");
        dialog.setHeaderText("Veuillez compléter vos informations de participant");

        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField telephoneField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(telephoneField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty()) {
                showAlert("Erreur", "Le nom et prénom sont obligatoires", Alert.AlertType.ERROR);
                return;
            }
            createParticipantAndInscription(nomField.getText(), prenomField.getText(), telephoneField.getText());
        }
    }

    private void createParticipantAndInscription(String nom, String prenom, String telephone) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Créer le participant avec l'id_user
            int participantId = createParticipant(conn, nom, prenom, telephone);
            
            // 2. Créer l'inscription
            createInscription(conn, participantId);
            
            conn.commit();
            
            showAlert("Succès", "Inscription réussie!", Alert.AlertType.INFORMATION);
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
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int createParticipant(Connection conn, String nom, String prenom, String telephone) throws SQLException {
        String sql = "INSERT INTO participant (nom, prenom, email, telephone, date_inscription, id_user) " +
                   "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, currentUserEmail); // email de l'utilisateur
            stmt.setString(4, telephone);
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(6, currentUserId); // id_user de l'utilisateur connecté
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Échec de la création du participant");
        }
    }

    // Ajout de la méthode manquante pour créer l'inscription
    private void createInscription(Connection conn, int participantId) throws SQLException {
        String sql = "INSERT INTO inscription (id_evenement, id_participant, date_inscription, statut) " +
                  "VALUES (?, ?, ?, 'confirmée')";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, participantId);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
            stmt.executeUpdate();
        }
    }

    private boolean isAlreadyRegistered() {
        String sql = "SELECT 1 FROM inscription i " +
                   "JOIN participant p ON i.id_participant = p.id_participant " +
                   "WHERE i.id_evenement = ? AND p.id_user = ?";
        
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
            Parent root = FXMLLoader.load(getClass().getResource("/vues/Home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
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
}