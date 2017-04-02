package networkchat;


import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.text.Text;
import static networkchat.Message.messageType.*;

public class Message implements Serializable {
    
    public static enum messageType {
        NOTIFICATION, CHAT_MESSAGE
    }
    private messageType type;
    private String username;
    private String text;
    
    
    Message(messageType type, String username, String text) {
        this.type = type;
        this.username = username;
        this.text = text;
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
    
    // Builds an array of Text objects for display in the chat window.
    public ArrayList<Text> getMessageForDisplay() {
        ArrayList<Text> fullMessage = new ArrayList<Text>();
        
        switch(type) {
            case NOTIFICATION:
                fullMessage.add(new Text(text));
                break;
            case CHAT_MESSAGE:
                fullMessage.add(new Text(username));
                fullMessage.add(new Text(": " + text));
                break;
        }
        
        return fullMessage;
    }
    
}
