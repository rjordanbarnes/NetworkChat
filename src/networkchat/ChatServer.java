/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;

/**
 *
 * @author Jordan
 */
public class ChatServer extends ChatEntity {

    ServerSocket serverSocket;
    Socket socket;
    InputStreamReader IR;
    BufferedReader BR;
    TextArea chatWindow;
    ChatScreenController controller;
    PrintStream PS;
    
    int port;
    String username;
    boolean knowClientUsername;

    public ChatServer(int port, String username) throws Exception {
        serverSocket = new ServerSocket(port);
        this.port = port;
        this.username = username;
        
    }

    // Waits for a connection to be made.
    public void waitForConnection() throws Exception {
        controller.addLine("Starting server on port " + port, false);
        final Task<Socket> task = new Task<Socket>() {
            @Override
            protected Socket call() throws Exception {
                
                // Blocks while waiting for connection
                Socket socket = serverSocket.accept();
                return socket;
            }
        };
        
        // Start connection task
        new Thread(task).start();
        
        // Once connected, setup socket and buffers.
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
            new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                try {
                    socket = task.getValue();
                    IR = new InputStreamReader(socket.getInputStream());
                    BR = new BufferedReader(IR);
                    PS = new PrintStream(socket.getOutputStream());
                    
                    // Send username to client and begin listening.
                    PS.println(username);
                    listenForChat();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    // Listens for chat and adds new messages to the screen.
    public void listenForChat() {
        
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
                            // Client will always send their username as first message.
                            if (knowClientUsername) {
                                controller.addLine(value);
                            } else {
                                controller.addLine(value + " has connected.");
                                knowClientUsername = true;
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