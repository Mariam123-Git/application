package application.application;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;



import java.io.IOException;

public class HomeController {

    @FXML
    private StackPane welcomeBanner;
    
    @FXML
    private HBox eventsToggleGroup;
    
    @FXML 
    private VBox homeSection;
    @FXML 
    private VBox eventSection;
    @FXML 
    private VBox contactSection;
    @FXML 
    private VBox aboutSection;
    @FXML 
    private ScrollPane scrollPane; 
    @FXML
    private ComboBox<String> subjectComboBox;
 
    
    // ID de l'utilisateur actuellement connecté (à remplacer par votre système d'authentification)
  // Utilisez une valeur par défaut pour les tests
    private int currentUserId;
    private String currentUserEmail;
    @FXML
    private Button mes_inscriptions;
    @FXML
    private Button mes_notifications;
    

    public void setUserData(int userId, String userEmail) {
        this.currentUserId = userId;
        this.currentUserEmail = userEmail;
    }
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
            Parent root = FXMLLoader.load(getClass().getResource("/vues/events.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) welcomeBanner.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page des événements.");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void gotoschedule(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vues/ScheduleViewer.fxml"));
            Scene scene = new Scene(root);
            // Cette ligne évite les problèmes d'injection FXML
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible de charger la page du planning.");
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
            Parent root = FXMLLoader.load(getClass().getResource("/vues/contact.fxml"));
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
     * try
     */
    @FXML
    public void showEventDetails(ActionEvent event) {
        try {
            Button sourceButton = (Button) event.getSource();
            
            // Récupérer l'ID comme String d'abord, puis convertir
            String eventIdStr = (String) sourceButton.getUserData();
            int eventId = Integer.parseInt(eventIdStr);
            
            System.out.println("eventId= "+eventId);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/details.fxml"));
            Parent root = loader.load();
            
            EventDetailController controller = loader.getController();
            controller.initData(eventId, currentUserId, currentUserEmail);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "ID d'événement invalide");
            e.printStackTrace();
        } catch (IOException e) {
            showErrorAlert("Erreur", "Impossible de charger les détails de l'événement");
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour extraire l'ID de l'événement à partir de la carte d'événement
     * Ceci est une approche simplifiée, vous devrez l'adapter à votre structure réelle
     */
    private int getEventIdFromCard(Node eventCard) {
        // Pour les tests, utilisons des ID statiques basés sur l'ordre des cartes
        int index = eventsToggleGroup.getChildren().indexOf(eventCard);
        
        // ID d'événement par défaut basé sur l'index (1, 2, 3...)
        return index + 1;
        
        // Méthode alternative: si vous stockez l'ID dans userData
        // return (int) eventCard.getUserData();
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
    	subjectComboBox.getItems().addAll(
    	        "Question générale",
    	        "Support technique",
    	        "Partenariat",
    	        "Suggestion",
    	        "Autre");
        
        // Attacher les ID d'événements aux cartes
        setupEventCards();
        
        System.out.println("HomeController initialisé");
    }
    
    /**
     * Configurer les cartes d'événements avec leurs IDs respectifs
     */
    private void setupEventCards() {
        // Vérifier si des cartes d'événements existent
        if (eventsToggleGroup != null && !eventsToggleGroup.getChildren().isEmpty()) {
            // Pour chaque carte d'événement, assigner un ID
            for (int i = 0; i < eventsToggleGroup.getChildren().size(); i++) {
                Node card = eventsToggleGroup.getChildren().get(i);
                // Stocker l'ID d'événement (i+1) dans userData
                card.setUserData(i + 1);
            }
        }
    }
    
    private void scrollToNode(Node node) {
        scrollPane.layout(); // force le layout pour avoir les coordonnées
        double y = node.getBoundsInParent().getMinY();
        double height = scrollPane.getContent().getBoundsInLocal().getHeight();
        scrollPane.setVvalue(y / height);
    }
    @FXML
    private void scrollToHome() {
        scrollToNode(homeSection);
    }

    @FXML
    private void scrollToEvent() {
        scrollToNode(eventSection);
    }

    @FXML
    private void scrollToAbout() {
        scrollToNode(aboutSection);
    }

    @FXML
    private void scrollToContact() {
		scrollToNode(contactSection);
    }

    
    @FXML
    private void scrollToInscriptions() {
    	try {
	        Parent root = FXMLLoader.load(getClass().getResource("/vues/participant_dashboard.fxml"));
	        App.stage.setScene(new Scene(root));
	        App.stage.sizeToScene();  
	        App.stage.centerOnScreen();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
    
    @FXML
    private void scrollToNotifications() {
    	try {
	        Parent root = FXMLLoader.load(getClass().getResource("/vues/Mes_Notifications.fxml"));
	        App.stage.setScene(new Scene(root));
	        App.stage.sizeToScene();  
	        App.stage.centerOnScreen();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
    
}