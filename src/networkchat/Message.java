package networkchat;


import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import static networkchat.Message.messageType.*;

public class Message implements Serializable {
    
    public static enum messageType {
        NOTIFICATION, DISCONNECT, CONNECT, CHAT_MESSAGE, USERLIST
    }
    private messageType type;
    private User user;
    private String text;
    private ArrayList<User> users;
    
    Message(messageType type, String username, Color usernameColor, String text) {
        this.type = type;
        this.user = new User(username, usernameColor);
        this.text = text;
    }
    
    Message(messageType type, String username, Color usernameColor) {
        this(type, username, usernameColor, "");
    }
    
    Message(messageType type, User user) {
        this(type, user.username, Color.web(user.usernameColor), "");
    }
    
    Message(messageType type, ArrayList<User> users) {
        this(type, "", Color.BLACK, "");
        this.users = users;
    }
    
    
    public messageType getType() {
        return type;
    }
    
    public User getUser() {
        return user;
    }
    
    public String getText() {
        return text;
    }
    
    public ArrayList<User> getUsers() {
        return users;
    }
    
    
    
    // Builds an array of Text objects for display in the chat window.
    public ArrayList<Text> getMessageForDisplay() {
        ArrayList<Text> fullMessage = new ArrayList<>();
        Text usernameText = new Text(user.username);
        usernameText.setFill(Color.web(user.usernameColor));
        
        switch(type) {
            case NOTIFICATION:
                fullMessage.add(new Text(text));
                break;
            case DISCONNECT:
                fullMessage.add(usernameText);
                Text disconnectNotice = new Text(" has disconnected.");
                disconnectNotice.setFill(Color.web(user.usernameColor));
                fullMessage.add(disconnectNotice);
                break;
            case CONNECT:
                fullMessage.add(usernameText);
                Text connectNotice = new Text(" has connected.");
                connectNotice.setFill(Color.web(user.usernameColor));
                fullMessage.add(connectNotice);
                break;
            case CHAT_MESSAGE:
                fullMessage.add(usernameText);
                fullMessage.add(new Text(": " + text));
                break;
        }
        
        return fullMessage;
    }
    
}
