package networkchat;


import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import static networkchat.Message.messageType.*;

public class Message implements Serializable {
    
    public static enum messageType {
        NOTIFICATION, DISCONNECT, CONNECT, CHAT_MESSAGE
    }
    private messageType type;
    private String username;
    private String usernameColor;
    private String text;
    
    
    Message(messageType type, String username, Color usernameColor, String text) {
        this.type = type;
        this.username = username;
        this.text = text;
        this.usernameColor = getRGBColor(usernameColor);
    }
    
    Message(messageType type, String username, Color usernameColor) {
        this(type, username, usernameColor, "");
    }
    
    public messageType getType() {
        return type;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getText() {
        return text;
    }
    
    public String getUsernameColor() {
        return usernameColor;
    }
    
    public Color getUsernameColor(String color) {
        return Color.web(usernameColor);
    }
    
    public String getRGBColor(Color color) {
        return String.valueOf(color);
    }
    
    // Builds an array of Text objects for display in the chat window.
    public ArrayList<Text> getMessageForDisplay() {
        ArrayList<Text> fullMessage = new ArrayList<Text>();
        Text usernameText = new Text(username);
        usernameText.setFill(Color.web(usernameColor));
        
        switch(type) {
            case NOTIFICATION:
                fullMessage.add(new Text(text));
                break;
            case DISCONNECT:
                fullMessage.add(usernameText);
                fullMessage.add(new Text (" has disconnected."));
                break;
            case CONNECT:
                fullMessage.add(usernameText);
                fullMessage.add(new Text (" has connected."));
                break;
            case CHAT_MESSAGE:
                fullMessage.add(usernameText);
                fullMessage.add(new Text(": " + text));
                break;
        }
        
        return fullMessage;
    }
    
}
