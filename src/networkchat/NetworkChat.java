package networkchat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class NetworkChat extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Network Chat");
        stage.setScene(scene);
        stage.show();
        // Clear focus
        root.requestFocus();
        
        // Make sure program and threads close entirely when X is pressed.
        stage.setOnCloseRequest((WindowEvent e) -> {
            try {
                Platform.exit();
                System.exit(0);
            } catch (Exception e1) {
                System.out.println(e1);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
