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
import static networkchat.Message.messageType.CHAT_MESSAGE;

/**
 *
 * @author Jordan
 */
public class ChatServer extends ChatObject {
    String username = "Server";

    private ServerSocket serverSocket;
    private Socket socket;
    
    // Make an array of ChatClients
    private ArrayList<Connection> clients;

    private TextArea chatWindow;
    private ChatScreenController controller;


    public ChatServer(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        
        // Creates a list of client connections and creates the first one as the server itself.
        clients = new ArrayList<Connection>();
        
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
            System.out.println("Broadcasting");
            clients.get(i).sendMessage(message);
        }
    }
    
    public void setController(ChatScreenController controller) {
        this.controller = controller;
    }

    @Override
    void sendChatMessage(String message) {
        Message newMessage = new Message(CHAT_MESSAGE, username, message);
        sendChatMessage(newMessage);
    }

    @Override
    void sendChatMessage(Message message) {
        switch(message.getType()) {
            case NOTIFICATION:
                controller.addLine(message.getText());
                break;
            case CHAT_MESSAGE:
                controller.addLine(message.getUsername() + ": " + message.getText());
                break;
        }
        broadcastMessage(message);
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
                            // Broadcasts the received message whenever one is received.
                            @Override
                            public void run() {
                                System.out.println("Message received.");
                                sendChatMessage(message);
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