package report.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import report.utils.StaticStorage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 638, 420));
        primaryStage.setResizable(false);
        primaryStage.show();
        StaticStorage.mainStage = primaryStage;
    }


    public static void main(String[] args) {
        launch(args);


    }
}
