package networkchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import static networkchat.Message.messageType.*;


public class ChatClient extends ChatObject {
    ChatScreenController controller;
    
    Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    
    String username;
    Color usernameColor;
            
    public ChatClient(String ip, int port, String username, Color usernameColor) throws Exception {
        socket = new Socket(ip, port);
        this.username = username;
        this.usernameColor = usernameColor;
        
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
                    Platform.runLater(() -> {
                        // Handles any new messages received.
                        handleMessage(message);
                    });
                }
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    public void handleMessage(Message message) {
        controller.addLine(message.getMessageForDisplay());
    }
    
    public void announceConnection() {
        Message message = new Message(CONNECT, username, usernameColor);
        sendChatMessage(message);
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
        try {
        outStream.writeObject(message);
        outStream.flush();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
}
