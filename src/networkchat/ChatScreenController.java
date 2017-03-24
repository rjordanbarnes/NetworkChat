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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class ChatScreenController implements Initializable {
    
    // Chat Window
    @FXML
    private TextArea chatWindow;
    @FXML
    private TextField chatBox;
    
    
    //// EVENT METHODS ////
    
    // Chat Window
    
    // Clicking the Submit Button
    @FXML
    private void clickSubmitButton(ActionEvent event) {
        sendMessage();
    }
    
    // Pressing a key stroke in the chat box.
    @FXML
    private void keyPressChatBox(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }
    
    
    //// HELPER METHODS ////
    
    // Sends the message in the chat box.
    public void sendMessage() {
        if (chatBox.getText().trim().length() > 0) {
            addLine("Jordan: " + chatBox.getText());
            chatBox.setText("");
        }
    }
    
    public void addLine(String input) {
        chatWindow.appendText("\n" + input);
    }
    
    
    //// INITIALIZE METHOD ////
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
