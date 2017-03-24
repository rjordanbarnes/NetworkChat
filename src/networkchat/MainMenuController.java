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
    
    @FXML
    private void clickHostToggle(ActionEvent event) {
        ipBox.setDisable(true);
        startButton.setText("Start");
    }
    
    @FXML
    private void clickJoinToggle(ActionEvent event) {
        ipBox.setDisable(false);
        startButton.setText("Connect");
    }
    
    // Clicking the Start button
    @FXML
    private void clickStartButton(ActionEvent event) {
        boolean started = false;

        if (startButton.getText().equals("Connect")) {
            // Connect to a server
            try {
                String ip = ipBox.getText();
                int port = Integer.parseInt(portBox.getText());
                ChatClient client = new ChatClient(ip, port);
                started = true;
            } catch(Exception e) {
                System.out.println(e);
            }
        } else {
            // Host a server
            try {
                int port = Integer.parseInt(portBox.getText());
                ChatServer server = new ChatServer(port);
                server.waitForConnection();
                started = true;
            } catch(Exception e) {
                System.out.println(e);
            }
        }
        
        // Switch scene if connected.
        if (started) {
            try {
            Stage stage = (Stage)startButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("ChatScreen.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    
    //// INITIALIZE METHOD ////
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
