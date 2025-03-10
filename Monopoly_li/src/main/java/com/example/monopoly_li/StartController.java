package com.example.monopoly_li;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StartController {
    @FXML
    private AnchorPane page, startPage, gameSelectPage;
    @FXML
    private Button newGameBtn, gameSelectBtn, backBtn;
    @FXML
    private TextField gameIDField;
    @FXML
    private PasswordField gamePasswordField;
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized = false;
    
    @FXML
    private void newGame() {
    
    }
    
    @FXML
    private void changePage(ActionEvent event) {
        startPage.setVisible(false);
        gameSelectPage.setVisible(false);
        
        Button button = (Button)event.getSource();
        AnchorPane pageToShow = null;
        
        if(button.equals(newGameBtn)) {
            return;
        } else if (button.equals(gameSelectBtn)) {
            pageToShow = gameSelectPage;
        } else if(button.equals(backBtn)) {
            pageToShow = startPage;
        }
        
        if (pageToShow != null)
            pageToShow.setVisible(true);
    }
    
    @FXML
    private void enterGame() {
        int gameID = Utils.safeParseInt(gameIDField.getText());
        if (gameID == -1) return;
        if (SQLUtils.gameExists(gameID, gamePasswordField.getText())) {
            changeScene(gameID);
        } else {
            Utils.errorAlert(
                    Alert.AlertType.ERROR,
                    "Error In enterGame",
                    "That Game Does Not Exist",
                    "Please enter the correct game id and password and try again."
            );
        }
    }
    
    private void changeScene(int gameID) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("board.fxml"));
            Parent root = fxmlLoader.load();
            
            BoardController boardController = fxmlLoader.getController();
            boardController.setPlayers(SQLUtils.getPlayers(gameID));
            
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Monopoly | Game ID: " + gameID);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            Utils.errorAlert(
                    Alert.AlertType.ERROR,
                    "Scene Error",
                    "Error Changing Scene",
                    "There was an error changing scenes, please try again"
            );
            e.printStackTrace();
        }
    }
    
    // region Window Settings
    @FXML
    private void windowMinimize(ActionEvent event) {
        Utils.windowMinimize(event);
    }
    
    @FXML
    private void windowClose() {
        Utils.windowClose();
    }
    
    @FXML
    private void windowClick(MouseEvent event) {
        Utils.windowClick(event);
    }
    
    @FXML
    private void windowDrag(MouseEvent event) {
        if(alreadyMaximized)
            windowMaximize();
        Utils.windowDrag(event, page);
    }
    
    @FXML
    private void windowMaximize() {
        if (!alreadyMaximized) {
            Scene scene = page.getScene();
            double initWidth = scene.getWidth();
            double initHeight = scene.getHeight();
            
            defaultWidth = (defaultWidth == 0) ? scene.getWidth() : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? scene.getHeight() : defaultHeight;
            
            Utils.windowMaximize(page, initWidth, initHeight, false);
        } else
            Utils.windowMaximize(page, defaultWidth, defaultHeight, true);
        
        alreadyMaximized = !alreadyMaximized;
    }
    // endregion
}