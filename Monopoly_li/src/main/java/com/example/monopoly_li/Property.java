package com.example.monopoly_li;

public class Property extends Cell {
    private final int id, price;
    private final int[] rent = new int[6];
    private int stage;
    private final String color;
    private Player owner;
    
    public Property(String name, int id, int price,
                    int[] rent, String color, int stage) {
        super(name);
        this.id = id;
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
    
    public int getId() {
        return id;
    }
    public int getPrice() {
        return price;
    }
    public int getRent() {
        return rent[stage];
    }
    public String getName() {
        return name;
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
}