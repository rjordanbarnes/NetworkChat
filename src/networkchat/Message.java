/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

import java.io.Serializable;

/**
 *
 * @author rjord
 */
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
    
}
