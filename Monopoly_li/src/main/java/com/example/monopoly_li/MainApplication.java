package com.example.monopoly_li;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Main applicaiton to start up the game select start.fxml file.
*/

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("start.fxml"));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Monopoly Application | Game Select");
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}