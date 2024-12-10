package report.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import report.models.user.User;
import report.service.UserService;
import report.utils.StaticStorage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField userName;

    @FXML
    private PasswordField passWord;

    @FXML
    private Button login;

    @FXML
    private Button quit;

    @FXML
    private Label tipLabel;

    private UserService userService = new UserService();

    public LoginController(){

    };

    @FXML
    private void initialize(){

        tipLabel.setVisible(false);

        login.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                tipLabel.setVisible(false);
                String name = userName.getText().trim();
                String pwd = passWord.getText().trim();
                User user = new User(name,pwd);
                user = userService.getUser(user);
                if(user!=null){
                    StaticStorage.loginUser = user;
                    showStageByUser(user);
                }else{
                    tipLabel.setVisible(true);
                }

            }
        });

        quit.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        });

    }

    private void showStageByUser(User user){
        URL resource;
        if(user.getUserRoleId()==1){
            resource = getClass().getClassLoader().getResource("fxml/Manager.fxml");
        }
        else{
            resource = getClass().getClassLoader().getResource("fxml/User.fxml");
        }

        AnchorPane root = null;
        try {
            root = FXMLLoader.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        StaticStorage.mainStage.hide();
        StaticStorage.mainStage.setScene(scene);
        StaticStorage.mainStage.show();
    }


}
