/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;

/**
 *
 * @author Jordan
 */
public class ChatServer {

    ServerSocket serverSocket;
    Socket socket;
    
    // Make an array of ChatClients
    ArrayList<Connection> clients;

    TextArea chatWindow;
    ChatScreenController controller;


    public ChatServer(int port, String username) throws Exception {
        serverSocket = new ServerSocket(port);
        socket = new Socket("localhost", port);
        
        // Creates a list of client connections and creates the first one as the server itself.
        clients = new ArrayList<Connection>();
        clients.add(new Connection(socket));
        
        // Starts waiting for a connection.
        waitForConnection();
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
                   clients.add(new Connection(socket));
                   
                   // Wait for another connection on a new thread.
                   waitForConnection();
               } catch (Exception e) {
                   System.out.println(e);
               }
           }
       });
    }
    
    public void broadcastMessage(Message message) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i) != null) {
                clients.get(i).sendMessage(message);
            }
        }
    }
    
    public class Connection {
        private Socket socket;
        private ObjectOutputStream outStream;
        private ObjectInputStream inStream;

        Connection(Socket socket) {
            this.socket = socket;
            try {
            // Output
            outStream = new ObjectOutputStream(socket.getOutputStream());
            // Input
            inStream = new ObjectInputStream(socket.getInputStream());
            listen();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        private void listen() {
             // Listens for chat and add it to the screen.
            final Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Message input;
                    while ((input = (Message)inStream.readObject()) != null) {
                        final Message message = input;
                        Platform.runLater(new Runnable() {
                            // Broadcasts the received message whenever one is received/
                            @Override
                            public void run() {
                                broadcastMessage(message);
                            }
                        });
                    }
                    return null;
                }
            };

            new Thread(task).start();
        }
        
        // Sends a message to this connection's outstream.
        public void sendMessage(Message message) {
            try {
            outStream.writeObject(message);
            outStream.flush();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}