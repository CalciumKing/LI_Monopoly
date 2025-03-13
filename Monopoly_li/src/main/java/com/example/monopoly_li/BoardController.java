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

public class BoardController {
    @FXML
    private Button addHouseHotelBtn, buySellBtn;
    @FXML
    private AnchorPane page;
    @FXML
    private Label rolled, landed, playerName, playerMoney, playerProperties, otherPlayersList;
    
    private Player[] players;
    private Cell[] properties;
//    , currentPos;
    private int[] dice = new int[1];
    private int turn = -1, gameID, playerTurns;
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized = false;
    
    public void initialize(int gameID, Player[] players) {
        turn = SQLUtils.getTurn(gameID);
        properties = SQLUtils.getAllProperties(gameID);
        this.players = players;
//        currentPos = new Cell[]{properties[players[turn].getPosition()]}
        this.gameID = gameID;
        playerTurns = 0;
//        printPlayers();
        initPlayerInfo();
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
//        currentPos[turn] = properties[currentPlayer.getPosition()];
        dice = new int[]{0, 0};
        updateUI(currentPlayer);
        rolled.setText("Player Rolled: -1");
        currentPlayer.setPrevPosition(currentPlayer.getPosition());
    }
    
    private void updateUI(Player currentPlayer) {
        playerName.setText("Player ID: " + currentPlayer.getId());
        playerMoney.setText("$" + currentPlayer.getBalance());
        rolled.setText("Player Rolled: " + dice[0] + " + " + dice[1]);
//        landed.setText("Player Landed: " + currentPos[turn].getName());
        landed.setText("Player Landed: " + properties[players[turn].getPosition()].getName());
        
        playerProperties.setText("");
        for (Property property : currentPlayer.getOwned())
            playerProperties.setText(playerProperties.getText() + property.getName() + "\n");
        
        otherPlayersList.setText("");
        for (int i = 0; i < players.length; i++) {
            if(i == turn) continue;
            
            Player curr = players[i];
            otherPlayersList.setText(
                    otherPlayersList.getText() +
                    curr.getId() +
                    " $" + curr.getBalance() +
                    ", " + properties[players[i].getPosition()].getName()
            );
        }
    }
    
    @FXML
    private void hideButtons() {
        buySellBtn.setVisible(false);
        addHouseHotelBtn.setVisible(false);
    }
    
    @FXML
    private void addHouseHotel() {
    
    }
    
    @FXML
    private void buyOrSell() {
    
    }
    
    private void drawCard(boolean isChest, Player player) {
        Card card = new Card(isChest);
        card.getCard().execute(player);
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
        
        player.setPrevPosition(player.getPosition());
        dice = new int[]{((int) (Math.random() * 6) + 1), ((int) (Math.random() * 6) + 1)};
        
        if (optionSelected.isPresent()) {
            if(optionSelected.get().getText().equals("Roll For Doubles")) {
                if(dice[0] == dice[1])
                    player.setInJail(false);
            } else {
                player.removeBalance(200);
                player.setInJail(false);
            }
        } else {
            player.move(dice[0] + dice[1]);
//        currentPos[turn] = properties[player.getPosition()];
        }
        
        playerTurns++;
        updateUI(player);
        checkCell();
    }
    
    private void checkCell() {
        Player currentPlayer = players[turn];
        Cell currentCell = properties[currentPlayer.getPosition()];
        Action action;
        switch (currentCell.getType()) {
            case PROPERTY: // non-action, purchasable cells
                Property property = (Property) currentCell;
                if (property.getOwner() == null) {
                    buySellBtn.setVisible(true);
                    addHouseHotelBtn.setVisible(false);
                } else if (property.getOwner().equals(currentPlayer)) {
                    buySellBtn.setVisible(false);
                    addHouseHotelBtn.setVisible(true);
                } else
                    hideButtons();
                break;
//                currentCell[turn] = properties[currentPlayer.getPosition()];
            case GO_TO_JAIL, TAX: // cells with actions
                action = (Action) currentCell;
                action.execute(currentPlayer);
                System.out.println(action.getName() + "," + action.getDescription());
                hideButtons();
                updateUI(players[turn]);
                break;
            case CHEST, CHANCE: // merge with above
                System.out.println(currentCell.getType());
                action = (Action) currentCell;
                action.execute(currentPlayer);
                System.out.println(action.getName() + "," + action.getDescription());
                hideButtons();
                updateUI(players[turn]);
                break;
            case FREE_PARKING, JAIL:
                hideButtons();
                break;
            case GO: // merge with above
                System.out.println("On Go");
                hideButtons();
                break;
        }
    }
    
    @FXML
    private void endTurn() {
        if(dice[0] == dice[1]) {
            if(playerTurns < 3) {
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