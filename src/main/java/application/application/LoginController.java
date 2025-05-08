package application.application;


import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.event.EventHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable{

	private double XOffset=0;
	private double YOffset=0;
   
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label forgetPasswordLabel;

    @FXML
    private Label signUpLabel;
    
    @FXML
    private Pane content_area;
    
    @FXML
    private AnchorPane parent;

    @FXML
    private void initialize() {
        // Initial setup can go here if needed
    }

    // Event handler for the Login button click
    @FXML
    private void handleLogin(MouseEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        User user = AuthService.authenticate(email, password);
        if (user != null) {
            showAlert("Succès", "Connexion réussie !");
            goToHome(user);
        } else {
            showAlert("Erreur", "Email ou mot de passe incorrect.");
        }
    }

    private void goToHome(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/Home.fxml"));
            Parent root = loader.load();
            
            HomeController homeController = loader.getController();
            homeController.setUserData(user.getId(), user.getEmail());
            
            App.stage.setScene(new Scene(root));
            App.stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Event handler for "Forget Password?" label click
    @FXML
    private void handleForgetPassword(MouseEvent event) {
        showAlert("Info", "Redirect to password recovery");
        // Implement password recovery functionality here
    }

    // Event handler for "Sign Up" label click (opens the registration page)
    @FXML
    private void open_registration(MouseEvent event) throws IOException {
        
            // Charger le fichier FXML d'inscription
           // FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/Registration.fxml"));
           Parent fxml = FXMLLoader.load(getClass().getResource("/vues/Registration.fxml"));
         	content_area.getChildren().removeAll();
			content_area.getChildren().setAll(fxml);
           /*  Scene registrationScene = new Scene(loader.load());

           // Créer une nouvelle fenêtre pour l'inscription
            Stage stage = new Stage();
            stage.setTitle("Sign Up");
            stage.setScene(registrationScene);
            stage.show();*/
       
    }

    // Utility method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Event handler to close the application
    @FXML
    private void close_app(MouseEvent event) {
       // Stage stage = (Stage) loginButton.getScene().getWindow();
       // stage.close();
    	System.exit(0);
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		makeStageDrageable();
	}

	public void makeStageDrageable() {
		// TODO Auto-generated method stub
		
		parent.setOnMousePressed(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				XOffset =event.getSceneX();
				YOffset =event.getSceneY();
			}});
	    parent.setOnMouseDragged(new EventHandler<MouseEvent>(){
	    	@Override
			public void handle(MouseEvent event) {
               App.stage.setX(event.getSceneX()-XOffset);
               App.stage.setY(event.getSceneY()-YOffset);
               App.stage.setOpacity(0.7f);
			}
		});
    
	}
}
