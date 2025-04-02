package com.example.monopoly_li.Square;

import com.example.monopoly_li.Player;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Holds information for each property, accessed by multiple
    classes, property data is initialized each game by SQL stored data.
    Properties that can be bought are of this class.
    Inherits from Cell class.
*/

public class Property extends Cell {
    private final String color;
    private Player owner;
    private final int[] rent;
    private final int price;
    private int stage; // 0 purchasable, 1 bought, 2-4 houses, 5 hotel
    
    // constructor for both new and owned properties
    public Property(String name, int id, int price,
                    int[] rent, String color, int stage) {
        super(name, Type.PROPERTY, id);
        this.price = price;
        this.rent = rent;
        this.color = color;
        this.stage = stage;
        owner = null; // loading owned properties are assigned an owner later for sql reasons
    }
    
    // region Getters/Setters
    public int getPrice() {
        return price;
    }
    public int getRent() {
        return rent[stage];
    }
    public String getColor() {
        return color;
    }
    public int getStage() {
        return stage;
    }
    public void setStage(int stage) {
        this.stage = stage;
        if(stage == 0)
            owner = null;
    }
    public void addStage() {
        this.stage++;
    }
    public Player getOwner() {
        return owner;
    }
    public void setOwner(Player owner) {
        this.owner = owner;
    }
    // endregion
}