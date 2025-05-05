package application.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private StackPane welcomeBanner;
    
    @FXML
    private HBox eventsToggleGroup;

    /**
     * Naviguer vers la page d'accueil
     * @param event L'événement de clic
     */
    @FXML
    public void goToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/views/home.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            
            stage.show();
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page d'accueil.");
            e.printStackTrace();
        }
    }

    /**
     * Naviguer vers la page des événements
     * @param event L'événement de clic
     */
    @FXML
    public void goToEvents(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/views/events.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page des événements.");
            e.printStackTrace();
        }
    }

    /**
     * Naviguer vers la page À propos
     * @param event L'événement de clic
     */
    @FXML
    public void goToAbout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/views/about.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page À propos.");
            e.printStackTrace();
        }
    }

    /**
     * Naviguer vers la page de contact
     * @param event L'événement de clic
     */
    @FXML
    public void goToContact(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/views/contact.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page de contact.");
            e.printStackTrace();
        }
    }

    /**
     * Gérer l'inscription à un événement
     * @param event L'événement de clic
     */
    @FXML
    public void handleEventRegistration(ActionEvent event) {
        showInfoAlert("Inscription", "Vous êtes maintenant inscrit à cet événement!");
        // Logique d'inscription à implémenter
    }

    /**
     * Afficher les détails d'un événement
     * @param event L'événement de clic
     */
    @FXML
    public void showEventDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/event_details.fxml"));
            Parent root = loader.load();
            // Vous pouvez passer des données à la vue des détails si nécessaire
            // EventDetailsController controller = loader.getController();
            // controller.setEventData(eventData);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur", "Impossible de charger les détails de l'événement.");
            e.printStackTrace();
        }
    }

    /**
     * Annuler l'inscription à un événement
     * @param event L'événement de clic
     */
    @FXML
    public void cancelRegistration(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Annulation d'inscription");
        alert.setContentText("Êtes-vous sûr de vouloir annuler votre inscription à cet événement?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Logique d'annulation à implémenter
                showInfoAlert("Annulation", "Votre inscription a été annulée avec succès.");
            }
        });
    }

    /**
     * Afficher une alerte d'information
     * @param title Le titre de l'alerte
     * @param message Le message à afficher
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Afficher une alerte d'erreur
     * @param title Le titre de l'alerte
     * @param message Le message d'erreur à afficher
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Initialise le contrôleur
     * Cette méthode est appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        // Initialisation des composants ou chargement de données si nécessaire
        System.out.println("HomeController initialisé");
    }
}