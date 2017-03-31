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
    
    public enum messageType {
        NOTIFICATION, CHAT_MESSAGE
    }
    private messageType type;
    private int userID;
    private String text;
    
    Message(messageType type, int userID, String text) {
        this.type = type;
        this.userID = userID;
        this.text = text;
    }
    
    public messageType getType() {
        return type;
    }
    
}
