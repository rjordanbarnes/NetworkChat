/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkchat;

import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 *
 * @author rjord
 */
public class User implements Serializable {
    public String username;
    public String usernameColor;
    
    User(String username, String usernameColor) {
        this.username = username;
        this.usernameColor = usernameColor;
    }
    
    User(String username, Color usernameColor) {
        this.username = username;
        this.usernameColor = getRGBColor(usernameColor);
    }
    
    public final String getRGBColor(Color color) {
        return String.valueOf(color);
    }
}
