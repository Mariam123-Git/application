package application.application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class EventCardController {

    @FXML private Label titlelabel;
    @FXML private Label DateLabel;
    @FXML private Label LieuLabel;
    @FXML private Label OrganisateurLabel;
    @FXML private Button DesinscriptionButton;
    @FXML private ImageView EventImage;
    
    private FlowPane eventContainer;
    public void setFlowPane(FlowPane flowPane) {
        this.eventContainer = flowPane;
    }
    
    private int eventId;

    public void setEventData(int eventId, String title, String date_debut, String date_fin, String location, String organizer, String image_url) {
        this.eventId = eventId;

        // Vérifie si aucune information n'est fournie (ex: aucune inscription)
        if ((title == null || title.isEmpty()) &&
            (date_debut == null || date_debut.isEmpty()) &&
            (date_fin == null || date_fin.isEmpty()) &&
            (location == null || location.isEmpty()) &&
            (organizer == null || organizer.isEmpty())) {
            
            titlelabel.setText("Aucune inscription trouvée");
            DateLabel.setText("");
            LieuLabel.setText("");
            OrganisateurLabel.setText("");
            
            try {
                String defaultPath = getClass().getResource("/images/event.jpg").toExternalForm();
                Image defaultImage = new Image(defaultPath);
                EventImage.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Impossible de charger l'image par défaut.");
            }

            return; // on quitte la méthode
        }

        // Si les données sont présentes
        titlelabel.setText(title);
        DateLabel.setText("Du " + date_debut + " au " + date_fin);
        LieuLabel.setText(location);
        OrganisateurLabel.setText(organizer);

        try {
            String resourcePath = getClass().getResource("/images/" + image_url).toExternalForm();
            Image image = new Image(resourcePath);
            EventImage.setImage(image);
        } catch (Exception e) {
            try {
                String defaultPath = getClass().getResource("/images/event.jpg").toExternalForm();
                Image defaultImage = new Image(defaultPath);
                EventImage.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Impossible de charger l'image par défaut.");
            }
        }
    }


    @FXML
    private void handleUnsubscribe() {
        String query = "DELETE FROM inscription WHERE id_participant = ? AND id_evenement = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Session.idUser);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();

            eventContainer.getChildren().removeIf(node -> {
                if (node.getUserData() != null && node.getUserData().equals(eventId)) {
                    return true;
                }
                return false;
            });

            eventContainer.setPrefWidth(eventContainer.getWidth());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
