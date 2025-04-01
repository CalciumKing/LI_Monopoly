package com.example.monopoly_li;

import com.example.monopoly_li.Square.Property;
import javafx.scene.control.Alert;

import java.util.ArrayList;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Holds information for each player, accessed by multiple
    classes, player data is initialized each game by SQL stored data.
*/

public class Player {
    private final ArrayList<Property> owned;
    private final int id, game;
    private int balance, position;
    private boolean inJail;
    
    // constructor for making a new player for a new game
    public Player(int id, int game) {
        this.id = id;
        this.game = game;
        balance = 1500;
        position = 0;
        owned = new ArrayList<>();
        inJail = false;
    }
    
    // overload constructor for loading an existing player
    public Player(int id, int game, int balance, boolean inJail,
                  int position, ArrayList<Property> owned) {
        this.id = id;
        this.game = game;
        this.balance = balance;
        this.inJail = inJail;
        this.position = position;
        this.owned = owned;
    }
    
    // region Getters/Setters
    public int getId() {
        return id;
    }
    public int getGame() {
        return game;
    }
    public int getBalance() {
        return balance;
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public ArrayList<Property> getOwned() {
        return owned;
    }
    public boolean isInJail() {
        return inJail;
    }
    public void setInJail(boolean inJail) {
        this.inJail = inJail;
        goToJail();
    }
    // endregion
    
    // region Helper Methods
    public void addBalance(int amount) { // replace print statements for stack pane notification
        Utils.normalAlert(
                Alert.AlertType.INFORMATION,
                "Amount Added To Balance",
                amount + " Added To Player " + id + "'s Balance",
                "Old Balance: " + balance + ", New Balance: " + (balance += amount)
        );
//        System.out.print("old balance:" + balance);
//        balance += amount;
//        System.out.println(" added:" + amount + ", new balance:" + balance);
    }
    public void removeBalance(int amount) { // replace print statements for stack pane notification
        Utils.normalAlert(
                Alert.AlertType.INFORMATION,
                "Amount Removed From Balance",
                amount + " Removed From Player " + id + "'s Balance",
                "Old Balance: " + balance + ", New Balance: " + (balance -= amount)
        );
//        System.out.print("old balance:" + balance);
//        balance -= amount;
//        System.out.println(" removed:" + amount + ", new balance:" + balance);
    }
    public void payTax(double percent) {
        removeBalance((int) (balance * percent));
    }
    public void addProperty(Property property) {
        owned.add(property);
    }
    public void removeProperty(Property property) {
        owned.remove(property);
    }
    public void goToJail() {
        setPosition(10);
    }
    public void passGo() {
        addBalance(200);
    }
    public void move(int spaces) {
        position += spaces;
        if (position >= 40) {
            position %= 40;
            passGo();
        }
    }
    // endregion
}