package com.example.monopoly_li.Square;

public class Cell {
    private final String name;
    private final Type type;
    private final int id;
    
    // default constructor
    public Cell() {
        name = "";
        type = null;
        id = -1;
    }
    
    // constructor for cards without squares
    public Cell(String name, Type type) {
        this.name = name;
        this.type = type;
        id = -1;
    }
    
    // constructor for properties and cards with squares
    public Cell(String name, Type type, int id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public Type getType() {
        return type;
    }
    public int getId() {
        return id;
    }
}