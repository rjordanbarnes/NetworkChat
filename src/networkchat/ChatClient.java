package networkchat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
import javafx.concurrent.Task;
import static networkchat.Message.messageType.*;

public class ChatClient extends ChatObject {
    ChatScreenController controller;
    
    Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    
    String username;
            
    public ChatClient(String ip, int port, String username) throws Exception {
        socket = new Socket(ip, port);
        this.username = username;
        // Output
        outStream = new ObjectOutputStream(socket.getOutputStream());
        // Input
        inStream = new ObjectInputStream(socket.getInputStream());
        announceConnection();
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
                controller.addLine(message.getText());
                break;
            case CHAT_MESSAGE:
                controller.addLine(message.getUsername() + ": " + message.getText());
                break;
        }
    }
    
    public void announceConnection() {
        Message message = new Message(NOTIFICATION, username, username + " has connected.");
        sendChatMessage(message);
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
        try {
        outStream.writeObject(message);
        outStream.flush();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
