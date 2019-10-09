package wikispeak;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WikiSpeakApp extends Application {
	
	/**
	 * Start method: Loads the home page on the stage
	 */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/HomePage.fxml"));
        Parent layout = loader.load();
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("WikiSpeak");
        primaryStage.show();
    }

    
    /**
     * Main method: launches GUI
     */
    public static void main(String[] args) {
        launch(args);
    }
}
