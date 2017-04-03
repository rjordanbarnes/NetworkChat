package networkchat;

import java.io.IOException;
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
import javafx.scene.paint.Color;
import static networkchat.Message.messageType.*;


public class ChatServer extends ChatObject {
    private String username;
    private Color usernameColor;

    private ServerSocket serverSocket;
    private Socket socket;
    
    // Make an array of ChatClients and track the next userID.
    private static ArrayList<Connection> clients;
    private static int nextUserID;

    private TextArea chatWindow;
    private ChatScreenController controller;


    public ChatServer(int port, String username, Color usernameColor) throws Exception {
        serverSocket = new ServerSocket(port);
        this.username = username;
        this.usernameColor = usernameColor;
        
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
                    } catch (IOException e) {
                        System.out.println(e);
                    }

                    return socket;
            }
        };
        
        // Start waiting for new connection.
        new Thread(task).start();

        // Called once a client connects.
       task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, (WorkerStateEvent t) -> {
            try {
                // Creates a new connection and adds it to the clients list.
                Socket socket1 = task.getValue();
                clients.add(new Connection(socket1, nextUserID));
                nextUserID++;
                // Wait for another connection on a new thread.
                waitForConnection();
            }catch (Exception e) {
                System.out.println(e);
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
    public void handleDisconnect(int userID, String username, Color usernameColor) {
        Message message = new Message(DISCONNECT, username, usernameColor);
        sendChatMessage(message);
        clients.set(userID, null);
    }
    
    @Override
    public void setController(ChatScreenController controller) {
        this.controller = controller;
    }

    @Override
    public void sendChatMessage(String message) {
        Message newMessage = new Message(CHAT_MESSAGE, username, usernameColor, message);
        sendChatMessage(newMessage);
    }

    @Override
    public void sendChatMessage(Message message) {
        controller.addLine(message.getMessageForDisplay());
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
        private Color usernameColor = null;

        Connection(Socket socket, int userID) {
            this.socket = socket;
            this.userID = userID;
            
            try {
            // Output
            outStream = new ObjectOutputStream(socket.getOutputStream());
            // Input
            inStream = new ObjectInputStream(socket.getInputStream());
            listen();
            } catch (IOException e) {
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
                            
                            // Remembers the client's username color.
                            if (usernameColor == null) {
                                String rgbColor = message.getRGBColor(usernameColor);
                                usernameColor = message.getUsernameColor(rgbColor);
                            }

                            Platform.runLater(() -> {
                                // Broadcasts the received message whenever one is received.
                                sendChatMessage(message);
                            });
                        } catch (IOException | ClassNotFoundException e) {
                            // Client disconnected.
                            Platform.runLater(() -> {
                                // Close the socket and handle the disconnect.
                                try {
                                    socket.close();
                                } catch (IOException e1) {
                                    System.out.println(e1);
                                }
                                handleDisconnect(userID, username, usernameColor);
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
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}