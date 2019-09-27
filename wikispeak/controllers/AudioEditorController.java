package wikispeak.controllers;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wikispeak.Bash;
import wikispeak.Creator;
import wikispeak.Wikit;

public class AudioEditorController {
    
    @FXML
    private AnchorPane finishCreationPage;
    @FXML
    private TextArea wikitText;
    @FXML
    private TextField nameField;
    @FXML
    private Text errorMsg;

    
    /**
     * Initialize method: Sets progress bar and error message to invisible, sets text area to display wikit article,
     * and sets max value of slider to number of sentences in article.
     */
    @FXML
    private void initialize() {
        errorMsg.setVisible(false);
        wikitText.setText(Wikit.get().getFormattedArticle());
    }

    
    /**
     * Updates progress bar and message to display progress and status of creation process
     * @param message The message to display
     */
    private void updateMessage(String message) {
        errorMsg.setText(message);
    }

    
    /**
     * Executes when create button is pressed.
     * Checks if the creation name is valid and clear of invalid characters, and if it is, makes the creation.
     * If creation name is already in use, user is asked if they want to overwrite.
     */
    @FXML
    private void handleCreate() {
        String creationName = nameField.getText();
        
        if (creationName == null || creationName.equals("")) {
            errorMsg.setText("Please enter a valid name for your audio file");
            errorMsg.setVisible(true);
            
        } else if (Bash.hasInvalidChars(creationName, true)) {
        	errorMsg.setText("Audio file name contains invalid character(s)");
        	errorMsg.setVisible(true);
        		
        } else if (!(new File("./creations/" + creationName + ".mp4").exists())) {
        	generateAudio();
        			
        } else {
        	Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
            saveAlert.setTitle("Overwrite Warning");
            saveAlert.setHeaderText(creationName + " already exists!");
            saveAlert.setContentText("If you press \"OK\" you will overwrite this file.");
            saveAlert.showAndWait().ifPresent(response -> {
            	if (response == ButtonType.OK) {
            		generateAudio();
            	}
            });
        }
    }


    @FXML
    private void handleNext() {
        //Move on to the FinishCreations page.
    }
    
    /**
     * Method to run the new creation task in a new thread
     */
    private void generateAudio() {
        Thread creatorThread = new Thread(create);
        creatorThread.start();
    }

    
    /**
     * Subclass of Task to handle making a new creation.
     */
    private Task<Void> create = new Task<Void>() {

    	/**
    	 * Call method: Creates temp audio and video files and combines them, then deletes the temp files, using Creator class.
    	 */
        @Override
        protected Void call() throws Exception {
        	
        	String audio = "audioTemp";
        	String video = "videoTemp";
        	String creationName = nameField.getText();

            String chunk = wikitText.getSelectedText();
            Creator.get().makeAudio(chunk, audio);

            Creator.get().makeVideo(Wikit.get().getTerm(), video);

            Creator.get().combine(video, audio, creationName);

            Creator.get().cleanup(video, audio);

            return null;

        }

        
        /**
         * Done method: Loads the view creations page after creation is completed.
         */
        @Override
        protected void done() {
            Platform.runLater(() -> {
            	FXMLLoader loader = new FXMLLoader();
                loader.setLocation(this.getClass().getResource("/wikispeak/resources/CreationsPage.fxml"));
                try {
                    AnchorPane viewCreationPage = loader.load();
                    finishCreationPage.getChildren().clear();
                    finishCreationPage.getChildren().add(viewCreationPage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }); 
        }
    };
}
