package com.example.monopoly_li.Square;

import com.example.monopoly_li.Player;
import com.example.monopoly_li.Utils;
import javafx.scene.control.Alert;

import java.util.function.Consumer;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Holds information for each Action cell, accessed by multiple
    classes, Action data is initialized each game by SQL stored data.
    Used for cells that cannot be purchased and have actions (ex: go to jail).
    Inherits from Cell class.
*/

public class Action extends Cell {
    private final String description;
    private final Consumer<Player> effect;
    
    // constructor for cards
    public Action(String description, Consumer<Player> effect) {
        super();
        this.description = description;
        this.effect = effect;
    }
    
    // constructor for action spaces ex: go to jail, chance, chest, and taxes
    public Action(String name, String description,
                  Type type, int id, Consumer<Player> effect) {
        super(name, type, id);
        this.description = description;
        this.effect = effect;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void execute(Player player) {
        if (effect != null) {
            effect.accept(player);
            Utils.normalAlert(
                    Alert.AlertType.INFORMATION,
                    "Action Card Executed",
                    getName(),
                    description
            );
        } else
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error In execute",
                    "Cannot Execute Because 'effect' Is Null",
                    "Please Try Again"
            );
    }
}