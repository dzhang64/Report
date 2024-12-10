package report.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import report.models.user.Role;
import report.models.user.User;
import report.service.UserService;
import report.utils.StaticStorage;
import report.utils.CommonTools;


/**
 * @Description:
 * @Author:daye.zhang
 * @Date:Create in 3:12 2021/12/8 0008
 */
public class ManagerController {

    @FXML
    private Label lpLabel1;

    @FXML
    private Label lpLabel2;

    @FXML
    private Label lpLabel3;

    @FXML
    private Button lpButton;

    @FXML
    private TextField userID;

    @FXML
    private TextField userName;

    @FXML
    private TextField loginName;

    @FXML
    private TextField pwd;

    @FXML
    private TextField userRoleId;


    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button modifyButton;

    @FXML
    private Button findButton;


    @FXML
    private Label tips;

    private UserService userService = new UserService();

    public ManagerController() {
    }

    @FXML
    private void initialize(){
        lpLabel1.setText(StaticStorage.loginUser.getUserName());
        lpLabel2.setText(Role.getRole(StaticStorage.loginUser.getUserRoleId()).getRoleStr());
        lpLabel3.setText(CommonTools.getTime());

        lpButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        });

        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                String tempUserName = userName.getText().trim();
                String tempLoginName = loginName.getText().trim();
                String tempPwd = pwd.getText().trim();
                int tempUserRoleId = Integer.parseInt(userRoleId.getText().trim());
                User user = new User(tempUserName,tempLoginName,tempPwd,tempUserRoleId);
                userService.insertUser(user);
                tips.setText("Add Complete!");

            }
        });


        findButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                String tempLoginName = loginName.getText().trim();
                User user = new User(tempLoginName);
                user = userService.getUserByloginName(user);
                if(user!=null){
                    userName.setText(user.getUserName());
                    userID.setText(String.valueOf(user.getUid()));
                    userRoleId.setText(String.valueOf(user.getUserRoleId()));
                    pwd.setText(user.getPwd());
                    tips.setText("Find Complete!");
                }else {
                    tips.setText("User not exist!");
                }
            }
        });

        deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {
                String tempLoginName = loginName.getText().trim();
                User user = new User(tempLoginName);
                userService.deleteUserByloginName(user);
                tips.setText("Delete Complete!");
            }
        });

        modifyButton.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent event) {

                String tempLoginName = loginName.getText().trim();
                User user = new User(tempLoginName);
                user = userService.getUserByloginName(user);
                if(user!=null){
                    String tempUserName = userName.getText().trim();
                    String tempPwd = pwd.getText().trim();
                    int tempUserRoleId = Integer.parseInt(userRoleId.getText().trim());
                    user = new User(tempUserName,tempLoginName,tempPwd,tempUserRoleId);
                    user.setUid(Integer.parseInt(userID.getText().trim()));
                    userService.updateUser(user);
                    tips.setText("Update Complete!");
                }else{
                    tips.setText("User not exist!");
                }
            }
        });


    }
}
