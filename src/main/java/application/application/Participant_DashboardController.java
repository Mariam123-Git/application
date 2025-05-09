package application.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.Label;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Participant_DashboardController {

    @FXML
    private FlowPane eventContainer;
    @FXML
    private Button DeconnexionButton;
    @FXML
    private ImageView LogoImage;
    

    public void initialize() {
        loadUserEvents();
    }

    private void loadUserEvents() {
        String query = "SELECT id_evenement FROM inscription WHERE id_user = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Session.idUser);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idEvenement = rs.getInt("id_evenement");

                String eventQuery = "SELECT * FROM evenement WHERE id_evenement = ?";
                try (PreparedStatement eventStmt = conn.prepareStatement(eventQuery)) {
                    eventStmt.setInt(1, idEvenement);
                    ResultSet eventRs = eventStmt.executeQuery();

                    if (eventRs.next()) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/EventCard.fxml"));
                        VBox eventCard = loader.load();
                        EventCardController controller = loader.getController();
                        
                        String organiserQuery = "Select nom, prenom From administrateur Where id_admin = ?";
                        PreparedStatement organiserStmt = conn.prepareStatement(organiserQuery);
                        int id_admin = eventRs.getInt("id_admin");
                        organiserStmt.setInt(1, id_admin);
                        ResultSet organiserRs = organiserStmt.executeQuery();
                        if (organiserRs.next()) {
                        	String orgName = organiserRs.getString("prenom")+ " " +organiserRs.getString("nom");
                        	controller.setEventData(
                                    idEvenement,
                                    eventRs.getString("titre"),
                                    eventRs.getString("date_debut"),
                                    eventRs.getString("date_fin"),
                                    eventRs.getString("lieu"),
                                    orgName,
                                    eventRs.getString("image_url")
                                );
                        }
                        

                        eventCard.setUserData(idEvenement);
                        eventContainer.getChildren().add(eventCard);
                        controller.setFlowPane(eventContainer);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Couldn't load user events\n");
        }
    }
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
