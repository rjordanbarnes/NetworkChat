package networkchat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class ChatScreenController implements Initializable {
    
    // Chat Window
    @FXML
    private TextFlow chatWindow;
    @FXML
    private TextField chatBox;
    @FXML
    private ScrollPane scroll;
    
    // The Chat object
    ChatObject chatObject;
    
    
    //// EVENT METHODS ////
    
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
            String message = chatBox.getText();
            chatObject.sendChatMessage(message);
            chatBox.setText("");
        }
    }
    
    // Adds the line of text to the chat window.
    public void addLine(String input) {
        // Doesn't add a newline if there aren't any messages.
        if (chatWindow.getChildren().size() < 1) {
            chatWindow.getChildren().add(new Text(input));
        } else {
            chatWindow.getChildren().add(new Text("\n" + input));
        }
    }
    
    // Tells the controller what chat object it's connected to.
    public void setChatObject(ChatObject chatObject) {
        this.chatObject = chatObject;
    }
    
    
    //// INITIALIZE METHOD ////
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Makes the scrollbar stay at the bottom of the window.
        scroll.vvalueProperty().bind(chatWindow.heightProperty());
    }    
    
}
