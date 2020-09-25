package Lab.Service;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Authorizator {
    private static String username=null;
    private static String password=null;
    private static boolean authorized = false;
    public static boolean authorize(){
        Stage window = new Stage();
        VBox box = new VBox(15);
        box.setAlignment(Pos.BASELINE_CENTER);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setFocusTraversable(false);
        usernameField.setMaxWidth(200);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setFocusTraversable(false);
        passwordField.setMaxWidth(200);
        Button button = new Button("OK");
        button.setOnAction(ae->{
            username=usernameField.getText();
            password=passwordField.getText();
            if(username.length()!=0&&password.length()!=0) {
                window.close();
                authorized=true;
            }
        });
        window.setResizable(false);
        box.getChildren().addAll(usernameField,passwordField,button);
        window.setTitle("Authorization");
        window.setScene(new Scene(box,240,130));
        window.showAndWait();
        return authorized;
    }

    public static String getUsername(){
        return username;
    }
    public static String getPassword(){
        return password;
    }
}
