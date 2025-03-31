package com.example.monopoly_li;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Random utilities to be used around the application,
    a method is placed here if it is used in multiple scripts ex: window actions
*/

public class Utils {
    private static double xOffset, yOffset;
    
    // region Alert Methods
    public static void normalAlert(Alert.AlertType type, String title,
                                  String headerText, String contentText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        alert.showAndWait();
    }
    
    public static Optional<ButtonType> confirmAlert(Alert.AlertType type, String title,
                                                    String headerText, String contentText,
                                                    String aText, String bText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        ButtonType yes = new ButtonType(aText, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType(bText, ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);
        return alert.showAndWait();
    }
    
    private static Alert createAlert(Alert.AlertType type, String title,
                                     String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
    // endregion Alert Methods
    
    public static int safeParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Safe Parse Int Error",
                    "Error Parsing a String Value To A Int",
                    "Cannot parse " + value + " into an int, please try again."
            );
            e.printStackTrace();
            return -1;
        }
    }
    
    public static void changeScene(String sceneName, int gameID, boolean maximized) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(sceneName));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(new Scene(root));
            
            if (gameID != -1) { // entering game
                stage.setTitle("Monopoly Application | Game ID: " + gameID);
                stage.show();
                
                BoardController boardController = fxmlLoader.getController();
                boardController.initialize(gameID, SQLUtils.getPlayers(gameID), maximized);
            } else { // exiting game
                stage.setTitle("Monopoly Application | Game Select");
                stage.show();
            }
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Scene Error",
                    "Error Changing Scene",
                    "There was an error changing scenes, please try again"
            );
            e.printStackTrace();
        }
    }
    
    // region Window Settings
    public static void windowMinimize(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }
    
    public static void windowClose() {
        Platform.exit();
    }
    
    public static void windowClick(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    
    public static void windowDrag(MouseEvent event, AnchorPane pane) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
    
    public static void windowMaximize(AnchorPane pane, double width, double height, boolean alreadyMaximized) {
        Stage stage = (Stage) pane.getScene().getWindow();
        Scene scene = stage.getScene();
        
        stage.setMaximized(!alreadyMaximized);
        
        double ratio = width / height,
                newWidth = scene.getWidth(), newHeight = scene.getHeight(),
                scaleFactor = (newWidth / newHeight > ratio) ? newHeight / height : newWidth / width;
        boolean condition = scaleFactor >= 1;
        
        if (condition) {
            Scale scale = new Scale(scaleFactor, scaleFactor);
            scale.setPivotX(0);
            scale.setPivotY(0);
            scene.getRoot().getTransforms().setAll(scale);
        }
        
        pane.setPrefWidth((condition) ? newWidth / scaleFactor : Math.max(width, newWidth));
        pane.setPrefHeight((condition) ? newHeight / scaleFactor : Math.max(height, newHeight));
    }
    // endregion
}