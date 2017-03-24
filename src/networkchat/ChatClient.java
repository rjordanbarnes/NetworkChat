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
            
    public ChatClient(String ip, int port) throws Exception {
        Socket socket = new Socket(ip, port);
        // Output
        PrintStream PS = new PrintStream(socket.getOutputStream());
        PS.println("Hello to Server from Client");

        // Input
        //        InputStreamReader IR = new InputStreamReader(socket.getInputStream());
        //        BufferedReader BR = new BufferedReader(IR);
        //
        //        String message = BR.readLine();
        //        System.out.println(message);
    }
     
//     public void connect() throws Exception {
//        
//        final Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                
//                Socket socket = serverSocket.accept();
//                InputStreamReader IR = new InputStreamReader(socket.getInputStream());
//                BufferedReader BR = new BufferedReader(IR);
//                String message = BR.readLine();
//                
//                
//                System.out.println("Connected");
//                
//                
//          
//                while (message != null) {
//                    final String value = "herp";
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            System.out.println("Connected.");
//                        }
//                    });
//                }
//                return null;
//            }
//        };
//        
//        new Thread(task).start();
//    }
    public void setController(ChatScreenController controller) {
        this.controller = controller;
    }
}
