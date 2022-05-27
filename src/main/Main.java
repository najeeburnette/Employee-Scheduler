package main;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;

/**
 * The Main Class
 *
 * @author Najee Burnette
 */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/Login Screen.fxml"));
        primaryStage.setTitle("Customer Scheduler");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {

        JDBC.openConnection();
        //Language tester
       // Locale.setDefault(new Locale("fr"));
        launch(args);
        JDBC.closeConnection();
    }
}
