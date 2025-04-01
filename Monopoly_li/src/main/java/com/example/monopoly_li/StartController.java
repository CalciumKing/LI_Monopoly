package com.example.monopoly_li;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Start page controller, handles all functionality
    on the start page. Manages basic scene changing, input boxes, and
    page changing.
*/

public class StartController {
    // region Variables
    @FXML
    private AnchorPane page, startPage, gameSelectPage, newGamePage;
    @FXML
    private Button newGameBtn, gameSelectBtn, backBtn, backBtn1;
    @FXML
    private TextField gameIDField;
    @FXML
    private PasswordField gamePasswordField, setPasswordField;
    @FXML
    private Spinner<Integer> numPlayersSpinner;
    
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized;
    // endregion
    
    // region FXML Methods
    @FXML
    private void changePage(ActionEvent event) {
        startPage.setVisible(false);
        newGamePage.setVisible(false);
        gameSelectPage.setVisible(false);
        
        Button button = (Button) event.getSource();
        AnchorPane pageToShow = null;
        
        if (button.equals(newGameBtn)) {
            pageToShow = newGamePage;
            numPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 8, 2, 1));
        } else if (button.equals(gameSelectBtn)) {
            pageToShow = gameSelectPage;
        } else if (button.equals(backBtn) || button.equals(backBtn1)) {
            pageToShow = startPage;
        }
        
        if (pageToShow != null)
            pageToShow.setVisible(true);
    }
    
    @FXML
    private void createGame() {
        int gameID = SQLUtils.createNewGame(setPasswordField.getText(), numPlayersSpinner.getValue());
        if(gameID == -1) return;
        
        changeScene(gameID);
    }
    
    @FXML
    private void enterGame() {
        int gameID = Utils.safeParseInt(gameIDField.getText());
        if (gameID == -1) return;
        
        if (SQLUtils.gameExists(gameID, gamePasswordField.getText()))
            changeScene(gameID);
        else
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error In enterGame",
                    "That Game Does Not Exist",
                    "Please enter the correct game id and password and try again."
            );
    }
    // endregion
    
    private void changeScene(int gameID) {
        Utils.changeScene("board.fxml", gameID, alreadyMaximized);
        page.getScene().getWindow().hide();
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
        if (alreadyMaximized)
            windowMaximize(); // undoing maximization
        Utils.windowDrag(event, page);
    }
    
    @FXML
    private void windowMaximize() {
        if (!alreadyMaximized) {
            Scene scene = page.getScene();
            double initWidth = scene.getWidth(),
                    initHeight = scene.getHeight();
            
            defaultWidth = (defaultWidth == 0) ? scene.getWidth() : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? scene.getHeight() : defaultHeight;
            
            Utils.windowMaximize(page, initWidth, initHeight, false);
        } else
            Utils.windowMaximize(page, defaultWidth, defaultHeight, true);
        
        alreadyMaximized = !alreadyMaximized;
    }
    // endregion
}