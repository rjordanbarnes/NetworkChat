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
import static networkchat.Message.messageType.*;


public class ChatServer extends ChatObject {
    private String username;

    private ServerSocket serverSocket;
    private Socket socket;
    
    // Make an array of ChatClients and track the next userID.
    private static ArrayList<Connection> clients;
    private static int nextUserID;

    private TextArea chatWindow;
    private ChatScreenController controller;


    public ChatServer(int port, String username) throws Exception {
        serverSocket = new ServerSocket(port);
        this.username = username;
        
        // Creates a list of client connections and creates the first one as the server itself.
        clients = new ArrayList<Connection>();
        nextUserID = 0;
        
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

        // Called once a client connects.
       task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
           new EventHandler<WorkerStateEvent>() {
           @Override
           public void handle(WorkerStateEvent t) {
               try {
                   // Creates a new connection and adds it to the clients list.
                   Socket socket = task.getValue();
                   clients.add(new Connection(socket, nextUserID));
                   nextUserID++;
                   
                   // Wait for another connection on a new thread.
                   waitForConnection();
               } catch (Exception e) {
                   System.out.println(e);
               }
           }
       });
    }
    
    // Helper method used to send a message to each client.
    private void broadcastMessage(Message message) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i) != null) {
                clients.get(i).sendMessage(message);
            }
        }
    }
    
    // Set client to null and inform everyone that client disconnected.
    public void handleDisconnect(int userID, String username) {
        Message message = new Message(NOTIFICATION, username, username + " has disconnected.");
        sendChatMessage(message);
        clients.set(userID, null);
    }
    
    @Override
    public void setController(ChatScreenController controller) {
        this.controller = controller;
    }

    @Override
    public void sendChatMessage(String message) {
        Message newMessage = new Message(CHAT_MESSAGE, username, message);
        sendChatMessage(newMessage);
    }

    @Override
    public void sendChatMessage(Message message) {
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
    
    // An object that maintains a socket with a client. Has a userID which corresponds
    // with the Connection's index in the server's clients array.
    public class Connection {
        private Socket socket;
        private ObjectOutputStream outStream;
        private ObjectInputStream inStream;
        
        // Information about the client.
        private int userID;
        private String username = null;

        Connection(Socket socket, int userID) {
            this.socket = socket;
            this.userID = userID;
            
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
                    while (true) {
                        try {
                            input = (Message)inStream.readObject();
                            final Message message = input;

                            // Remembers the client's username.
                            if (username == null) {
                                username = message.getUsername();
                            }

                            Platform.runLater(new Runnable() {
                                // Broadcasts the received message whenever one is received.
                                @Override
                                public void run() {
                                    sendChatMessage(message);
                                }
                            });
                        } catch (Exception e) {
                            // Client disconnected.
                            Platform.runLater(new Runnable() {
                                // Close the socket and handle the disconnect.
                                @Override
                                public void run() {
                                    try {
                                    socket.close();
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                    handleDisconnect(userID, username);
                                }
                            });
                            // Break out of the loop once a client disconnects.
                            break;
                        }
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