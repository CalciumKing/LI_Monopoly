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
    private int[] dice;
    private int turn, gameID, playerTurns;
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized;
    // endregion Variables
    
    // region Initialization Methods
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
//    private void printPlayers() {
//        for (Player player : players) {
//            System.out.println(player.getGame());
//            System.out.println(player.getId());
//            System.out.println(player.getBalance());
//            System.out.println(player.getPosition());
//            for (Property property : player.getOwned()) {
//                System.out.println(property.getId());
//                System.out.println(property.getName());
//                System.out.println(property.getOwner().getId());
//            }
//            System.out.println();
//        }
//    }
    
    private void initPlayerInfo() {
        Player curr = players[turn];
        dice = new int[]{0, 0};
        updateUI();
        checkCell();
    }
    
    private void initProperties() {
        for(Player player : players)
            for(Property property : player.getOwned())
                ((Property) properties[property.getId() - 1]).setOwner(player);
    }
    // endregion
    
    // region FXML Methods
    @FXML
    private void addHouseHotel() {
        Player player = players[turn];
        Property property = (Property) properties[player.getPosition()];
        int cost = calcHouseHotelCost(property), balance = player.getBalance();
        
        if (balance - cost >= 0) {
            Optional<ButtonType> optionSelected = Utils.confirmAlert(
                Alert.AlertType.CONFIRMATION,
                "Purchase House/Hotel?",
                "Would You Like To Purchase A House/Hotel For " + cost + "?",
                "You will have " + (balance - cost) + " remaining.",
                "Yes", "No"
            );
            
            if (optionSelected.isPresent() && optionSelected.get().getText().equals("Yes")) {
                player.removeBalance(cost);
                property.addStage();
                
                Utils.normalAlert(
                    Alert.AlertType.INFORMATION,
                    "House/Hotel Bought",
                    "Successfully Purchased House/Hotel",
                    "Remaining balance: " + player.getBalance() +
                            ", Property Stage: " + property.getStage()
                );
                
                playerMoney.setText("$" + player.getBalance());
                stage.setText("Stage: " + property.getStage());
                if(property.getStage() == 5)
                    addHouseHotelBtn.setVisible(false);
            }
        } else
            Utils.normalAlert(
                Alert.AlertType.INFORMATION,
                "Cannot Afford House/Hotel",
                "You Do Not Have Enough Money To Purchase A House/Hotel On This Property",
                "House/Hotel Cost: " + cost +
                        ", Player Balance: " + balance +
                        ", Difference: " + (cost - balance)
            );
    }
    
    @FXML
    private void buyProperty() {
        Player player = players[turn];
        Property property = (Property) properties[player.getPosition()];
        int cost = property.getPrice(), balance = player.getBalance();
        
        if (balance - cost >= 0) {
            Optional<ButtonType> optionSelected = Utils.confirmAlert(
                Alert.AlertType.CONFIRMATION,
                "Purchase Property?",
                "Would You Like To Purchase " + property.getName() + " For " + cost + "?",
                "You will have " + (balance - cost) + " remaining.",
                "Yes", "No"
            );
            
            if (optionSelected.isPresent() && optionSelected.get().getText().equals("Yes")) {
                player.removeBalance(cost);
                property.addStage();
                
                Utils.normalAlert(
                    Alert.AlertType.INFORMATION,
                    "Property Bought",
                    "Successfully Purchased " + property.getName(),
                    "Remaining balance: " + player.getBalance()
                );
                
                player.addProperty(property);
                property.setOwner(player);
                
                playerMoney.setText("$" + player.getBalance());
                stage.setText("Stage: 1");
                addHouseHotelBtn.setVisible(true);
                buyBtn.setVisible(false);
                sellBtn.setVisible(true);
                
                updateUI();
            }
        } else
            Utils.normalAlert(
                Alert.AlertType.INFORMATION,
                "Cannot Afford Property",
                "You Do Not Have Enough Money To Purchase " + property.getName(),
                "Property Cost: " + cost +
                        ", Player Balance: " + balance +
                        ", Difference: " + (cost - balance)
            );
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
            } else { // player goes to jail of doubles rolled 3 times in a row
                players[turn].goToJail();
                playerTurns = 0;
            }
        }
        turn = (turn + 1) % players.length;
        takeTurn();
    }
    // endregion
    
    // region Helper Methods
    private int calcHouseHotelCost(Property prop) {
        int id = prop.getId(), cost = (id > 40) ? 999999999 : ((id - 1) / 10) * 50 + 50;
        return cost * prop.getStage();
    }
    
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
        
        dice = new int[]{rollDie(), rollDie()}; // rolling 2 dice, 2 numbers 1-6, total between 2-12
        
        if (optionSelected.isPresent()) { // if player is in jail (not visiting)
            if (optionSelected.get().getText().equals("Roll For Doubles") && dice[0] == dice[1]) {
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
        ArrayList<Property> owned = currentPlayer.getOwned();
        
        playerName.setText("Player ID: " + currentPlayer.getId());
        playerMoney.setText("$" + currentPlayer.getBalance());
        rolled.setText("Player Rolled: " + dice[0] + " + " + dice[1]);
        landed.setText("Player Landed: " + properties[players[turn].getPosition()].getName());
        
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
        return (int) (Math.random() * 6) + 1;
    }
    
    private void checkCell() {
        Player currentPlayer = players[turn];
        Cell currentCell = properties[currentPlayer.getPosition()];
        
        switch (currentCell.getType()) {
            case PROPERTY: // non-action, purchasable cells (properties)
                Property property = (Property) currentCell;
                stage.setVisible(true);
                stage.setText("Stage: " + property.getStage());
                
                if (property.getOwner() == null) { // purchasable
                    buyBtn.setVisible(true);
                    sellBtn.setVisible(false);
                    addHouseHotelBtn.setVisible(false);
                } else if (property.getOwner().equals(currentPlayer)) { // current player is owner
                    buyBtn.setVisible(false);
                    sellBtn.setVisible(true);
                    
                    if(property.getStage() != 5 && property.getStage() != 0) { // property isn't a hotel and is owned by player
                        addHouseHotelBtn.setVisible(true);
                        for (Property prop : getPropertiesOfColor(property.getColor())) {
                            if (prop.getOwner() != currentPlayer) { // player doesn't own all similar colored properties
                                addHouseHotelBtn.setVisible(false);
                                break;
                            }
                        }
                    } else // property is a hotel and is owned by the player
                        addHouseHotelBtn.setVisible(false);
                } else { // owned by another player
                    hideButtons();
                    stage.setVisible(true);
                    currentPlayer.removeBalance(property.getRent());
                    playerMoney.setText("$" + currentPlayer.getBalance());
                    
                    if(currentPlayer.getBalance() < 0) { // player is broke, player is eliminated
                        removePlayer(currentPlayer.getId());
                        turn = (turn + 1) % players.length;
                        takeTurn();
                    } else { // player continues
                        Utils.normalAlert(
                                Alert.AlertType.INFORMATION,
                                "Player Successfully Paid Rent",
                                "Player Landed On a Property And Paid Rent Successfully",
                                "Player paid: " + property.getRent() + ", Remaining Balance: " + currentPlayer.getBalance()
                        );
                    }
                }
                break;
            case GO_TO_JAIL, TAX, CHEST, CHANCE: // non-purchasable cells with actions
                hideButtons();
                ((Action) currentCell).execute(currentPlayer);
                updateUI();
                break;
            case FREE_PARKING, JAIL, GO: // non-purchasable cells without actions or actions executed elsewhere
                hideButtons();
                break;
        }
    }
    
    private void removePlayer(int id) {
        players[turn] = null;
        
        Utils.normalAlert(
            Alert.AlertType.ERROR,
            "Player " + id + "Eliminated",
            "Player Cannot Afford Property Rent",
            "Player " + id + " is eliminated from the game"
        );
        
        Player victor = null;
        int playerCount = 0;
        
        for(Player player : players) {
            if (player != null) {
                playerCount++;
                victor = player; // only used if 1 player remains
            }
        }
        
        if(playerCount == 1) { // one player remaining, game has been won, game ends
            Utils.normalAlert(
                    Alert.AlertType.INFORMATION,
                    "Game Won",
                    "One Player Remains, Game Finished",
                    "Victorious Player: " + victor.getId()
            );
            
            // delete game data
            
            changeScene();
        }
    }
    
    private void changeScene() {
        Utils.changeScene("start.fxml", -1, alreadyMaximized);
        page.getScene().getWindow().hide();
    }
    
    private ArrayList<Property> getPropertiesOfColor(String color) {
        ArrayList<Property> colored = new ArrayList<>();
        for(Cell cell : properties)
            if(cell instanceof Property property && property.getColor().equals(color))
                colored.add(property);
        return colored;
    }
    
    private void hideButtons() {
        buyBtn.setVisible(false);
        sellBtn.setVisible(false);
        addHouseHotelBtn.setVisible(false);
        stage.setVisible(false);
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