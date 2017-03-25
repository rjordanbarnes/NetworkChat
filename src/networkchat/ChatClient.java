/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author Jordan
 */
public class ChatClient extends ChatEntity {
    ChatScreenController controller;
    PrintStream PS;
    InputStreamReader IR;
    BufferedReader BR;
    
    String username;
    boolean knowServerUsername = false;
            
    public ChatClient(String ip, int port, String username) throws Exception {
        Socket socket = new Socket(ip, port);
        this.username = username;
        
        // Output
        PS = new PrintStream(socket.getOutputStream());
        // Input
        IR = new InputStreamReader(socket.getInputStream());
        BR = new BufferedReader(IR);
        
        // Send username to server and begin listening.
        PS.println(username);
        listenForChat();
    }
    
    public void listenForChat() {
        // Listens for chat and add it to the screen.
        final Task<Socket> task = new Task<Socket>() {
            @Override
            protected Socket call() throws Exception {
                String output;
                while ((output = BR.readLine()) != null) {
                    final String value = output;
                    Platform.runLater(new Runnable() {
                        
                        // Method repeats many times a second.
                        @Override
                        public void run() {
                            // Server will always send their username as first message.
                            if (knowServerUsername) {
                                controller.addLine(value);
                            } else {
                                controller.addLine("You've connected to " + value + ".", false);
                                knowServerUsername = true;
                            }
                        }
                    });
                }
                return null;
            }
        };
        
        new Thread(task).start();
    }

    @Override
    public void setController(ChatScreenController controller) {
        this.controller = controller;
    }
    
    @Override
    public void sendMessage(String message) {
        controller.addLine(username + ": " + message);
        PS.println(username + ": " + message);
    }
}
