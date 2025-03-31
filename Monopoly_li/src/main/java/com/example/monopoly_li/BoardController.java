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

import java.util.ArrayList;
import java.util.Optional;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Board page controller, handles all the functionality
    of the board page, accesses other files.
*/

public class BoardController {
    // region Variables
    @FXML
    private Button addHouseHotelBtn, buyBtn, sellBtn;
    @FXML
    private AnchorPane page;
    @FXML
    private Label rolled, landed, playerName, playerMoney, playerProperties, otherPlayersList, tabText, stage;
    
    private Player[] players;
    private Cell[] properties;
    private int[] dice = {0, 0};
    private int turn, gameID, playerTurns;
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized;
    // endregion Variables
    
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
        initProperties();
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
        Player curr = players[turn];
        dice = new int[]{0, 0};
        updateUI();
        rolled.setText("Player Rolled: -1");
        curr.setPrevPosition(curr.getPosition());
    }
    
    private void initProperties() {
        for(Player player : players)
            for(Property property : player.getOwned())
                ((Property) properties[property.getId() - 1]).setOwner(player);
    }
    
    @FXML
    private void addHouseHotel() {
        // method to add a house or hotel to a property based on the property's stage
        Property property = (Property) properties[players[turn].getPosition()];
        int cost = calcCost(property);
        int balance = players[turn].getBalance();
        
        if (balance - cost >= 0) {
            Optional<ButtonType> optionSelected = Utils.confirmAlert(
                    Alert.AlertType.CONFIRMATION,
                    "Purchase Property?",
                    "Would You Like To Purchase A House/Hotel For " + cost + "?",
                    "You will have " + (balance - cost) + " remaining.",
                    "Yes", "No"
            );
            
            if (optionSelected.isPresent() && optionSelected.get().getText().equals("Yes")) {
                players[turn].removeBalance(cost);
                balance = players[turn].getBalance();
                property.addStage();
                
                Utils.normalAlert(
                        Alert.AlertType.INFORMATION,
                        "House/Hotel Bought",
                        "Successfully Purchased House/Hotel",
                        "Remaining balance: " + balance
                );
                
                updateUI();
            }
            
            updateUI();
        } else
            Utils.normalAlert(
                    Alert.AlertType.INFORMATION,
                    "Cannot Afford House/Hotel",
                    "You Do Not Have Enough Money To Purchase A House/Hotel On This Property",
                    "House/Hotel Cost: " + cost + ", Player Balance: " + balance
            );
    }
    
    private int calcCost(Property prop) {
        int id = prop.getId();
        int cost = (id / 10) * 50;
        
//        if(id <= 10)
//            cost = 50;
//        else if (id <= 20)
//            cost = 100;
//        else if (id <= 30)
//            cost = 150;
//        else if (id <= 40)
//            cost = 200;
//        else
//            cost = -1;

        return cost * prop.getStage();
    }
    
    @FXML
    private void buyProperty() {
        // method to buy a property with no houses
        // removing player money based on how much the property costs
    }
    
    @FXML
    private void sellProperty() {
        // method to sell a property that a player owns
        // gives player amount based on stage of property
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
        updateUI();
        checkCell();
    }
    
    private void updateUI() {
        Player currentPlayer = players[turn];
        playerName.setText("Player ID: " + currentPlayer.getId());
        playerMoney.setText("$" + currentPlayer.getBalance());
        rolled.setText("Player Rolled: " + dice[0] + " + " + dice[1]);
        landed.setText("Player Landed: " + properties[players[turn].getPosition()].getName());
        
        ArrayList<Property> owned = currentPlayer.getOwned();
        
        if(owned.isEmpty())
            playerProperties.setText("None");
        else {
            StringBuilder text = new StringBuilder();
            owned.forEach(property -> text.append(property.getName()).append("\n"));
            playerProperties.setText(text.toString());
        }
        
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
        
        switch (currentCell.getType()) {
            case PROPERTY: // non-action, purchasable cells
                Property property = (Property) currentCell;
                stage.setVisible(true);
                stage.setText("Stage: " + property.getStage());
                
                if (property.getStage() == 5)
                    hideButtons();
                else if (property.getOwner() == null) { // purchasable
                    buyBtn.setVisible(true);
                    sellBtn.setVisible(false);
                    addHouseHotelBtn.setVisible(false);
                } else if (property.getOwner().equals(currentPlayer)) { // current player is owner
                    buyBtn.setVisible(false);
                    sellBtn.setVisible(true);
                    addHouseHotelBtn.setVisible(true);
                } else // owned by another player
                    hideButtons();
                break;
            case GO_TO_JAIL, TAX, CHEST, CHANCE: // non-purchasable cells with actions
                System.out.println(currentCell.getType());
                Action action = (Action) currentCell;
                action.execute(currentPlayer);
                System.out.println(action.getName() + "," + action.getDescription());
                hideButtons();
                updateUI();
                stage.setVisible(false);
                break;
            case FREE_PARKING, JAIL, GO:
                hideButtons();
                stage.setVisible(false);
                break;
        }
    }
    
    private void hideButtons() {
        buyBtn.setVisible(false);
        sellBtn.setVisible(false);
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