package com.example.monopoly_li.Square;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Holds information for each Cell of the board,
    accessed by multiple classes and child classes,
    cell data is initialized each game by SQL stored data, properties
    without action are of this data type only (ex: free parking, go).
    Parent class of Action, Card, and Property.
*/

public class Cell {
    private final String name;
    private final Type type;
    private final int id;
    
    // default constructor, only used for cards without cells
    public Cell() {
        name = "";
        type = null;
        id = -1;
    }
    
    // overload constructor for cards without squares
    public Cell(String name, Type type) {
        this.name = name;
        this.type = type;
        id = -1;
    }
    
    // overload constructor for properties and cards with squares
    public Cell(String name, Type type, int id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }
    
    // region Getters
    public String getName() {
        return name;
    }
    public Type getType() {
        return type;
    }
    public int getId() {
        return id;
    }
    // endregion
}