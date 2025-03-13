package com.example.monopoly_li;

import com.example.monopoly_li.Square.Property;

import java.util.ArrayList;

public class Player {
    private final int id, game;
    private int balance, position, prevPosition;
    private ArrayList<Property> owned;
    
    // constructor for making a new player for a new game
    public Player(int id, int game) {
        this.id = id;
        this.game = game;
        balance = 1500;
        position = 0;
        owned = new ArrayList<>();
    }
    
    // constructor for loading an existing player
    public Player(int id, int game, int balance,
                  int position, ArrayList<Property> owned) {
        this.id = id;
        this.game = game;
        this.balance = balance;
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
    public void setOwned(ArrayList<Property> owned) {
        this.owned = owned;
    }
    public int getPrevPosition() {
        return prevPosition;
    }
    public void setPrevPosition(int prevPosition) {
        this.prevPosition = prevPosition;
    }
    // endregion
    
    // region Other Methods
    public void addBalance(int amount) {
        balance += amount;
        System.out.println("added:" + amount + ", balance:" + balance);
    }
    public void removeBalance(int amount) {
        balance -= amount;
        System.out.println("removed:" + amount + ", balance:" + balance);
    }
    public void payTax(double percent) {
        balance = (int) ((1 - percent) * balance);
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
        if(position >= 40) {
            position %= 40;
            passGo();
        }
    }
    // endregion
}