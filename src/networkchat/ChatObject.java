/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

/**
 *
 * @author rjord
 */
public abstract class ChatObject {

    abstract void sendChatMessage(String message);
    
    abstract void sendChatMessage(Message message);

    abstract void setController(ChatScreenController controller);
    
}
