package application.application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MesNotificationsController {

    @FXML
    private Button DeconnexionButton;

    @FXML
    private ImageView LogoImage;

    @FXML
    private VBox notificationContainer;

    // Classe interne représentant une notification avec envoyeur
    public static class Notification {
        private final String message;
        private final String envoyeur;

        public Notification(String message, String envoyeur) {
            this.message = message;
            this.envoyeur = envoyeur;
        }

        public String getMessage() {
            return message;
        }

        public String getEnvoyeur() {
            return envoyeur;
        }
    }

    /**
     * Charge les notifications depuis la base de données pour l'utilisateur donné.
     * @param utilisateurId ID de l'utilisateur connecté
     */
    public void loadNotifications(int utilisateurId) {
        ObservableList<Notification> notifications = FXCollections.observableArrayList();

        String sql = "SELECT n.contenu, u.username AS nom_envoyeur " +
                     "FROM notification n " +
                     "JOIN inscription i ON n.id_evenement = i.id_evenement " +
                     "JOIN evenement e ON n.id_evenement = e.id_evenement " +
                     "JOIN users u ON e.id_admin = u.id " +
                     "WHERE i.id_participant = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String message = rs.getString("contenu");
                    String envoyeur = rs.getString("nom_envoyeur");
                    notifications.add(new Notification(message, envoyeur));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mise à jour de l'interface
        notificationContainer.getChildren().clear();

        for (Notification notif : notifications) {
            Label notifLabel = new Label("De " + notif.getEnvoyeur() + " : " + notif.getMessage());
            notifLabel.setStyle(
                "-fx-padding: 9;" +
                "-fx-background-color: lightgray;" +
                "-fx-border-color: gray;" +
                "-fx-border-radius: 5;" +
                "-fx-font-size: 12px;" 
            );
            notifLabel.setPrefWidth(600);
            notifLabel.setWrapText(true);
            notificationContainer.getChildren().add(notifLabel);
        }

    }

    /**
     * Appelée automatiquement lors de l'initialisation de la vue.
     */
    public void initialize() {
        int utilisateurId = Session.idUser;
        loadNotifications(utilisateurId);
    }

    /**
     * Déconnecte l'utilisateur et redirige vers la page de login.
     */
    @FXML
    private void Logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) DeconnexionButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Redirige vers la page d'accueil.
     */
    @FXML
    private void goToHome(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) LogoImage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
