package com.example.monopoly_li;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardController {
    @FXML
    private Button addHouseHotelBtn, buySellBtn;
    @FXML
    private AnchorPane page;
    @FXML
    private Label rolled, landed, playerName, playerMoney, playerProperties, otherPlayersList;
    
    private Player[] players;
    private Cell[] properties;
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized = false;
    private int turn = -1, gameID;
    
    public void initialize(int gameID, Player[] players) {
        turn = SQLUtils.getTurn(gameID);
        properties = SQLUtils.getAllProperties(gameID);
        this.players = players;
        this.gameID = gameID;
        printPlayers();
        initPlayerInfo();
        initOtherPlayerInfo();
    }
    
    private void printPlayers() {
        for (Player player : players) {
            System.out.println(player.getGame());
            System.out.println(player.getId());
            System.out.println(player.getBalance());
            System.out.println(player.getPosition());
            for(Property property : player.getOwned()) {
                System.out.println(property.getId());
                System.out.println(property.getName());
                System.out.println(property.getOwner().getId());
            }
            System.out.println();
        }
    }
    
    @FXML
    private void initPlayerInfo() {
        Player currentPlayer = players[turn];
        playerName.setText("Player ID:" + currentPlayer.getId());
        playerMoney.setText("$" + currentPlayer.getBalance());
        //player properties
        // player rolled
        landed.setText(properties[currentPlayer.getPosition()].getName());
    }
    
    @FXML
    private void initOtherPlayerInfo() {
        //loop through all players
        //player names, balances, positions
    }
    
    @FXML
    private void addHouseHotel() {
    
    }
    
    @FXML
    private void buyOrSell() {
    
    }
    
    @FXML
    private void endTurn() {
        turn = (turn + 1) % players.length;
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