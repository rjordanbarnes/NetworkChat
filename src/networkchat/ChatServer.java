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

    public ChatServer(int port) throws Exception {
        System.out.println("Starting server on port " + port);
        serverSocket = new ServerSocket(port);
    }

    public void waitForConnection() throws Exception {
        
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
                    connectionEstablished();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }
    
    public void setController(ChatScreenController controller) {
        this.controller = controller;
    }
    
    public void connectionEstablished() {
        controller.addLine("Client has connected.");
        
        // Task that runs and 
        final Task<Socket> task = new Task<Socket>() {
            @Override
            protected Socket call() throws Exception {
                String output;
                while ((output = BR.readLine()) != null) {
                    final String value = output;
                    Platform.runLater(new Runnable() {
                        
                        @Override
                        public void run() {
                            // Repeats 
                            controller.addLine(value);
                        }
                    });
                }
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    public void sendMessage(String message) {
        PS.println(message);
    }
}