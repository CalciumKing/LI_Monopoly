package com.example.monopoly_li.Square;

import com.example.monopoly_li.Player;

public class Property extends Cell {
    private final String color;
    private final int price;
    private final int[] rent = new int[6];
    private int stage;
    private Player owner;
    
    public Property(String name, int id, int price,
                    int[] rent, String color, int stage) {
        super(name, Type.PROPERTY, id);
        this.price = price;
        this.rent[0] = rent[0];
        this.rent[1] = rent[1];
        this.rent[2] = rent[2];
        this.rent[3] = rent[3];
        this.rent[4] = rent[4];
        this.rent[5] = rent[5];
        this.color = color;
        this.stage = stage;
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