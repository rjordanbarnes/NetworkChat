/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author Jordan
 */
public class ChatClient {
    ChatScreenController controller;
    
    Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    
    String username;
    boolean knowServerUsername = false;
            
    public ChatClient(String ip, int port, String username) throws Exception {
        socket = new Socket(ip, port);
        this.username = username;
        
        // Output
        outStream = new ObjectOutputStream(socket.getOutputStream());
        // Input
        inStream = new ObjectInputStream(socket.getInputStream());

        listen();
    }
    
    public void listen() {
        // Listens for chat and add it to the screen.
        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Message input;
                while ((input = (Message)inStream.readObject()) != null) {
                    final Message message = input;
                    Platform.runLater(new Runnable() {
                        
                        // Method repeats many times a second.
                        @Override
                        public void run() {
                            handleMessage(message);
                        }
                    });
                }
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    public void handleMessage(Message message) {
        switch(message.getType()) {
            case NOTIFICATION:
                break;
            case CHAT_MESSAGE:
                break;
        }
    }

    public void setController(ChatScreenController controller) {
        this.controller = controller;
    }
    
    public void sendChatMessage(Message message) {
        controller.addLine(username + ": " + message);
        try {
        outStream.writeObject(message);
        outStream.flush();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
