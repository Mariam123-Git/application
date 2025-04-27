package application.application;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class RegistrationController implements Initializable{ 

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmpasswordField;

    @FXML
    private Button registerButton;

 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // registerButton.setOnAction(e -> handleRegister(null));
    }

    @FXML
    private void handleRegister( MouseEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmpasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please fill all fields.");
        } else if (!password.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Error", "Passwords do not match.");
        } else {
        	AuthService.register(username, email, confirmPassword);
        	goToHome();
            showAlert(AlertType.INFORMATION, "Success", "Registration successful!");
            // Ajoute ici la logique de sauvegarde ou autre
        }
    }

    private void goToHome() {
    	// TODO Auto-generated method stub
   	 try {
   	        Parent root = FXMLLoader.load(getClass().getResource("/vues/Home.fxml"));
   	        App.stage.setScene(new Scene(root));
   	    } catch (IOException e) {
   	        e.printStackTrace(); 
   	    }
	}


	@FXML
    private void close_app(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void back_to_menu(MouseEvent event) throws IOException {
        // Ajoute ici la logique de retour au menu
       /* System.out.println("Retour au menu principal...");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vues/Login.fxml")); // modifie le chemin si besoin
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        } */  
    	  System.out.println("Bouton cliqué, tentative de retour au menu...");

    	    if (App.stage != null && MetaData.parent != null) {
    	    	
    	    	 Parent root = FXMLLoader.load(getClass().getResource("/vues/Login.fxml"));
    	         App.stage.getScene().setRoot(root);
    	        System.out.println("Retour réussi !");
    	    } else {
    	        System.out.println("Erreur : stage ou MetaData.parent est null");
    	    }

    	}
    	

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
