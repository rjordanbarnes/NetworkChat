/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class MainMenuController implements Initializable {
    
    // Main Menu
    @FXML
    private TextField nameBox;
    @FXML
    private ToggleButton hostToggle;
    @FXML
    private ToggleButton joinToggle;
    @FXML
    private TextField ipBox;
    @FXML
    private TextField portBox;
    @FXML
    private Button startButton;
    
    //// EVENT METHODS ////
    
    // Main Menu
    
    // Clicking the Host toggle
    @FXML
    private void clickHostToggle(ActionEvent event) {
        ipBox.setDisable(true);
        startButton.setText("Start");
    }
    
    // Clicking the Join toggle
    @FXML
    private void clickJoinToggle(ActionEvent event) {
        ipBox.setDisable(false);
        startButton.setText("Connect");
    }
    
    // Disables Start button if Name, IP, or Port are empty.
    @FXML
    private void checkFields(KeyEvent event) {
        if (nameBox.getText().trim().length() < 1 ||
            ipBox.getText().trim().length() < 1 ||
            portBox.getText().trim().length() < 1) {
            startButton.setDisable(true);
        } else {
            startButton.setDisable(false);
        }
    }
    
    // Clicking the Start button
    @FXML
    private void clickStartButton(ActionEvent event) {
        
        boolean started = false;
        String ip = ipBox.getText();
        int port = Integer.parseInt(portBox.getText());
        String username = nameBox.getText();

        if (startButton.getText().equals("Connect")) {
            // Connect to a server
            try {
                ChatClient client = new ChatClient(ip, port, username);
                transitionToChat(client);
            } catch(Exception e) {
                System.out.println(e);
            }
        } else {
            // Host a server
            try {
                ChatServer server = new ChatServer(port, username);
                transitionToChat(server);
                server.waitForConnection();
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }
    
    // Sets the scene to the Chat Screen. Associates the Server/Client with the new controller.
    public void transitionToChat(ChatEntity chatEntity) {
        try {
            // Transition to new scene.
            Stage stage = (Stage)startButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatScreen.fxml"));
            Parent root = loader.load();
            ChatScreenController controller = (ChatScreenController)loader.getController();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Associate the server or client with the new controller.
            controller.setEntity(chatEntity);
            chatEntity.setController(controller);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //// INITIALIZE METHOD ////
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
