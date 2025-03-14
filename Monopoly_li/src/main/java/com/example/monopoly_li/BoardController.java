package com.example.monopoly_li;

import com.example.monopoly_li.Square.*;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.Optional;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Board page controller, handles all the functionality
    of the board page, accesses other files.
*/

public class BoardController {
    @FXML
    private Button addHouseHotelBtn, buySellBtn;
    @FXML
    private AnchorPane page;
    @FXML
    private Label rolled, landed, playerName, playerMoney, playerProperties, otherPlayersList, tabText;
    
    private Player[] players;
    private Cell[] properties;
    private int[] dice = {0, 0};
    private int turn, gameID, playerTurns;
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized;
    
    // run from start page, setting up board
    public void initialize(int gameID, Player[] players, boolean alreadyMaximized) {
        turn = SQLUtils.getTurn(gameID);
        properties = SQLUtils.getAllProperties(gameID);
        tabText.setText("Monopoly Application | Game ID: " + gameID);
        if (alreadyMaximized) windowMaximize();
        
        this.players = players;
        this.gameID = gameID;
        this.alreadyMaximized = alreadyMaximized;
//        printPlayers();
        initPlayerInfo();
    }
    
    // method used only for seeing all player info during development
    private void printPlayers() {
        for (Player player : players) {
            System.out.println(player.getGame());
            System.out.println(player.getId());
            System.out.println(player.getBalance());
            System.out.println(player.getPosition());
            for (Property property : player.getOwned()) {
                System.out.println(property.getId());
                System.out.println(property.getName());
                System.out.println(property.getOwner().getId());
            }
            System.out.println();
        }
    }
    
    private void initPlayerInfo() {
        Player currentPlayer = players[turn];
        dice = new int[]{0, 0};
        updateUI(currentPlayer);
        rolled.setText("Player Rolled: -1");
        currentPlayer.setPrevPosition(currentPlayer.getPosition());
    }
    
    @FXML
    private void addHouseHotel() {
        // method to add a house or hotel to a property based on the property's stage
    }
    
    @FXML
    private void buyOrSell() {
        // method to buy a property with no houses or sell a property
        // giving a player money based on how many houses/hotel is on it
    }
    
    @FXML
    private void endTurn() {
        if (dice[0] == dice[1]) { // continue player turn if rolled doubles
            if (playerTurns < 3) {
                takeTurn();
                return;
            } else {
                players[turn].goToJail();
                playerTurns = 0;
            }
        }
        turn = (turn + 1) % players.length;
        takeTurn();
    }
    
    // region Helper Methods
    private void takeTurn() {
        Player player = players[turn];
        Optional<ButtonType> optionSelected = (player.isInJail()) ? Utils.confirmAlert(
                Alert.AlertType.INFORMATION,
                "Player In Jail",
                "Player is in Jail, Roll Or Pay",
                "Would you like to roll dice for a chance to " +
                        "get doubles and escape prison, or pay $200 to escape",
                "Roll For Doubles",
                "Pay $200"
        ) : Optional.empty();
        
        player.setPrevPosition(player.getPosition());
        dice = new int[]{rollDie(), rollDie()}; // rolling 2 dice, 2 numbers 1-6, total between 2-12
        
        if (optionSelected.isPresent()) { // if player is in jail (not visiting)
            if (optionSelected.get().getText().equals("Roll For Doubles")) {
                if (dice[0] == dice[1])
                    player.setInJail(false);
            } else {
                player.removeBalance(200);
                player.setInJail(false);
            }
        } else // player is not in jail or is visiting
            player.move(dice[0] + dice[1]);
        
        playerTurns++;
        updateUI(player);
        checkCell();
    }
    
    private void updateUI(Player currentPlayer) {
        playerName.setText("Player ID: " + currentPlayer.getId());
        playerMoney.setText("$" + currentPlayer.getBalance());
        rolled.setText("Player Rolled: " + dice[0] + " + " + dice[1]);
        landed.setText("Player Landed: " + properties[players[turn].getPosition()].getName());
        
        playerProperties.setText("");
        for (Property property : currentPlayer.getOwned())
            playerProperties.setText(playerProperties.getText() + property.getName() + "\n");
        
        otherPlayersList.setText("");
        for (int i = 0; i < players.length; i++) {
            if (i == turn) continue;
            
            Player curr = players[i];
            otherPlayersList.setText(
                    otherPlayersList.getText() +
                            curr.getId() +
                            " $" + curr.getBalance() +
                            ", " + properties[curr.getPosition()].getName()
            );
        }
    }
    
    private int rollDie() {
        return ((int) (Math.random() * 6) + 1);
    }
    
    private void checkCell() {
        Player currentPlayer = players[turn];
        Cell currentCell = properties[currentPlayer.getPosition()];
        Action action;
        
        switch (currentCell.getType()) {
            case PROPERTY: // non-action, purchasable cells
                Property property = (Property) currentCell;
                if (property.getOwner() == null) { // purchasable
                    buySellBtn.setVisible(true);
                    addHouseHotelBtn.setVisible(false);
                } else if (property.getOwner().equals(currentPlayer)) { // current player is owner
                    buySellBtn.setVisible(false);
                    addHouseHotelBtn.setVisible(true);
                } else // owned by another player
                    hideButtons();
                break;
            case GO_TO_JAIL, TAX, CHEST, CHANCE: // non-purchasable cells with actions
                System.out.println(currentCell.getType());
                action = (Action) currentCell;
                action.execute(currentPlayer);
                System.out.println(action.getName() + "," + action.getDescription());
                hideButtons();
                updateUI(currentPlayer);
                break;
            case FREE_PARKING, JAIL, GO:
                hideButtons();
                break;
        }
    }
    
    private void hideButtons() {
        buySellBtn.setVisible(false);
        addHouseHotelBtn.setVisible(false);
    }
    // endregion
    
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
            windowMaximize();
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