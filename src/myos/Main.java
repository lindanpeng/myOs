package myos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by lindanpeng on 2017/12/29.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/os.fxml"));
        primaryStage.setTitle("我的操作系统");
        primaryStage.setScene(new Scene(root,800,600));
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {

               launch(args);


    }
}
