package wikispeak.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import wikispeak.Bash;
import wikispeak.Wikit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NewCreationsController {

    @FXML
    private TextField searchField;
    @FXML
    private Text errorMsg;
    @FXML
    private AnchorPane newCreationPage;
    @FXML
    private ImageView searchingGif;

    @FXML
    private void initialize() {
        searchingGif.setVisible(false);
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();

        if (searchTerm == null || searchTerm.equals("")) {
            errorMsg.setText("Please enter a valid search term");

        } else {
            searchingGif.setVisible(true);

            Thread searchThread = new Thread(new WikitSearch<Void>());
            searchThread.start();
        }
    }

    private void loadFinishCreationsPage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/FinishCreationPage.fxml"));
        try {
            AnchorPane finishCreationPage = loader.load();
            newCreationPage.getChildren().clear();
            newCreationPage.getChildren().add(finishCreationPage);

        } catch (IOException e) {
            //TODO: handle exception
        }

    }

    private class WikitSearch<Void> extends Task<Void> {

    	private boolean successful;
    	private String wikitOut;
    	private String message;
    	
        @Override
        protected Void call() throws Exception {
           
//        	String searchTerm = searchField.getText();
//            
//        	Process process = Bash.execute("wikit", searchTerm);
//            
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String output = "";
//            String line = "";
//            while ((line = reader.readLine()) != null) { output += line + "\n"; }
//            int exitCode = process.waitFor();
//            
//            successful = true;
//            if (exitCode != 0) {
//            	successful = false;
//            	message = "An error occured with wikit";
//            } else if (output.contains(" not found :^(")) {
//            	successful = false;
//            	message = output;
//            }
//            return null;
        	
        	String output = Wikit.getInstance().search(searchField.getText());
        	
        	if (output.equals("")) {
        		message = "There was an error with wikit :L";
        		successful = false;
        	} else if (output.contains("not found :^(")) {
        		message = output;
        		successful = false;
        	} else {
        		wikitOut = output;
        		successful = true;
        	}
        	
        	return null;
        	
        }

        @Override
        protected void done() {
            
        	if (successful) {
        		Platform.runLater(() -> {
        			Wikit.getInstance().setArticle(wikitOut);
        			loadFinishCreationsPage();
        		});
        	} else {
        		Platform.runLater(() -> {
        			errorMsg.setText(message);
        			searchingGif.setVisible(false);
        		});
        	}
        }
    }
}
