package myos;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import myos.controller.MainController;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by lindanpeng on 2017/12/29.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/os.fxml"));
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();
        MainController mainController= fxmlLoader.getController();
        System.out.println(mainController);
        OS os=OS.getInstance();
        os.setMainController(mainController);
        mainController.setOs(os);
        primaryStage.setTitle("我的操作系统");
        primaryStage.setScene(new Scene(root,1000,650));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                mainController.closeOs();
                System.exit(0);
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {

               launch(args);


    }
}
