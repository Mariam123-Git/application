package application.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Hello world!
 * /vues/Login.fxml
 */
public class App extends Application
{ 

	protected static Stage stage = null;

	public void start(Stage stage) throws Exception {
	        /*FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vues/Login.fxml"));
	        Scene scene = new Scene(fxmlLoader.load());
	        primaryStage.setTitle("Gestion d'Événements");
	        primaryStage.setScene(scene);
	        MetaData.parent= fxmlLoader;
	        primaryStage.show();*/
		  
		    Parent root = FXMLLoader.load(getClass().getResource("/vues/Home.fxml"));
<<<<<<< HEAD
		   // Parent root = FXMLLoader.load(getClass().getResource("/vues/login.fxml"));
=======
>>>>>>> d47641f2dd0c6cabd2dba8baf6638aa4849800dd
	        Scene scene = new Scene(root);
	        stage.setTitle("Gestion d'Événements");
	        stage.initStyle(StageStyle.UNDECORATED);      
	      //  App.stage = stage;
	        stage.setScene(scene);
	        App.stage=stage;
	        App.stage.sizeToScene();   //pour center l'élément
	        App.stage.centerOnScreen();//pour centrer l'élément
	        MetaData.parent= root; 
	        stage.show();
		 
	    }

	    public static void main(String[] args) {
	        launch(args);
	    }
}
