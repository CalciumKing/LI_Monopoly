package com.example.monopoly_li;
public class Cell {
    protected String name = "";
    
    public Cell() {}
    
    public Cell(String name) {
        this.name = name;
    }
    
    protected String getName() {
        return name;
    }
}