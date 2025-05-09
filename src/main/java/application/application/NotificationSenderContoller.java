package application.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NotificationSenderContoller implements Initializable {

    @FXML
    private Button DeconnexionButton;

    @FXML
    private ImageView LogoImage;

    @FXML
    private ComboBox<Evenement> eventComboBox; 

    @FXML
    private TextField messageTextField;

    // Classe interne Evenement
    private static class Evenement {
        private int id;
        private String titre;

        public Evenement(int id, String titre) {
            this.id = id;
            this.titre = titre;
        }

        public int getId() {
            return id;
        }

        public String getTitre() {
            return titre;
        }

        @Override
        public String toString() {
            return titre;
        }
    }

    // Méthode appelée automatiquement au chargement
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEvents();
    }

    // Chargement des événements dans la ComboBox
    private void loadEvents() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT id, titre FROM evenement";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String titre = rs.getString("titre");
                eventComboBox.getItems().add(new Evenement(id, titre));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Envoi de la notification
    @FXML
    private void sendNotification() {
        Evenement selectedEvent = eventComboBox.getValue();
        String message = messageTextField.getText();

        if (selectedEvent == null || message == null || message.trim().isEmpty()) {
            // afficher une alerte si nécessaire
            System.out.println("Veuillez sélectionner un événement et saisir un message.");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String insertQuery = "INSERT INTO notification (evenement_id, message) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, selectedEvent.getId());
            stmt.setString(2, message);
            stmt.executeUpdate();

            System.out.println("Notification envoyée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Déconnexion (retour à la page Login)
    @FXML
    private void Logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) DeconnexionButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Redirection vers l'accueil
    @FXML
    private void goToHome(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/Home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) LogoImage.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
