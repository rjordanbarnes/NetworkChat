package networkchat;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
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
    @FXML
    private TextFlow userList;
    
    // The Chat object
    ChatObject chatObject;
    
    // The user list
    ArrayList<Text> users = new ArrayList<>();
    
    
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
    
    // Receives an array of Text objects to add to the chat window.
    public void addLine(ArrayList<Text> input) {
        // Adds a new line if there is already text in the window.
        if (chatWindow.getChildren().size() > 0) {
            chatWindow.getChildren().add(new Text("\n"));
        }
        
        // Adds each Text object to the chat window.
        for (int i = 0; i < input.size(); i++) {
            chatWindow.getChildren().add(input.get(i));
        }
    }
    
    // Tells the controller what chat object it's connected to.
    public void setChatObject(ChatObject chatObject) {
        this.chatObject = chatObject;
    }
    
    public void createUserList(ArrayList<User> users) {
        // Create Text objects for users
        for (int i = 0; i < users.size(); i++) {
            Text user = new Text(users.get(i).username);
            user.setFill(Color.web(users.get(i).usernameColor));
            this.users.add(i, user);
        }
        
        refreshUserList();
    }
    
    public void addUser(String username, String usernameColor) {
        Text user = new Text(username);
        user.setFill(Color.web(usernameColor));
        users.add(user);
        
        refreshUserList();
    }
    
    public void removeUser(String username) {
        for (int i = users.size() - 1; i >= 0; i--) {
            if (users.get(i).getText().equals(username)) {
                users.remove(i);
                break;
            }
        }
        
        refreshUserList();
    }
    
    public void refreshUserList() {
        userList.getChildren().clear();
        userList.getChildren().add(new Text("Users [" + users.size() + "]"));
        for (int i = 0; i < users.size(); i++) {
            userList.getChildren().add(new Text("\n"));
            userList.getChildren().add(users.get(i));
        }
    }
    
    //// INITIALIZE METHOD ////
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Makes the scrollbar stay at the bottom of the window.
        scroll.vvalueProperty().bind(chatWindow.heightProperty());
    }    
    
}
