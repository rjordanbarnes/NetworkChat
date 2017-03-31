/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
    
    // Make an array of ChatClients
    ArrayList<ChatClient> clients;

    TextArea chatWindow;
    ChatScreenController controller;
    
    
    int port;
    String username;
    boolean knowClientUsername;

    public ChatServer(int port, String username) throws Exception {
        serverSocket = new ServerSocket(port);
        this.port = port;
        this.username = username;
        clients = new ArrayList<ChatClient>();
    }

    // Waits for a connection to be made.
    public void waitForConnection() throws Exception {
        // Continuously waits for new connections with new sockets.
        final Task<Socket> task = new Task<Socket>() {
            @Override
            protected Socket call() throws Exception {
                    // Blocks while waiting for connection
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    return socket;
            }
        };
        
        // Start waiting for new connection.
        new Thread(task).start();

        // Called once connected. Creates a new ChatClient and adds it to the clients array.
       task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
           new EventHandler<WorkerStateEvent>() {
           @Override
           public void handle(WorkerStateEvent t) {
               try {
                   Socket socket = task.getValue();
                   clients.add(new ChatClient(socket, username, controller));
                   
                   // Wait for another connection on a new thread.
                   waitForConnection();
               } catch (Exception e) {
                   System.out.println(e);
               }
           }
       });
    }

    @Override
    public void setController(ChatScreenController controller) {
        this.controller = controller;
    }
    
    @Override
    public void sendChatMessage(Message message) {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).sendChatMessage(message);
        }
    }
}