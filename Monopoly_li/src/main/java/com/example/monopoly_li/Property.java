package com.example.monopoly_li;
public class Property extends Cell {
    private final int id, price, rent, house1Rent, house2Rent, house3Rent, house4Rent, hotelRent;
    private int stage;
    private final String color;
    private Player owner;
    
    public Property(String name, int id, int price,
                    int rent, int house1Rent, int house2Rent,
                    int house3Rent, int house4Rent, int hotelRent,
                    String color, int stage) {
        super(name);
        this.id = id;
        this.price = price;
        this.rent = rent;
        this.house1Rent = house1Rent;
        this.house2Rent = house2Rent;
        this.house3Rent = house3Rent;
        this.house4Rent = house4Rent;
        this.hotelRent = hotelRent;
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
        return rent;
    }
    public int getHouse1Rent() {
        return house1Rent;
    }
    public int getHouse2Rent() {
        return house2Rent;
    }
    public int getHouse3Rent() {
        return house3Rent;
    }
    public int getHouse4Rent() {
        return house4Rent;
    }
    public int getHotelRent() {
        return hotelRent;
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
    public Player getOwner() {
        return owner;
    }
    public void setOwner(Player owner) {
        this.owner = owner;
    }
}