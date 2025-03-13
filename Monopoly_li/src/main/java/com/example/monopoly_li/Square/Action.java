package com.example.monopoly_li.Square;

import com.example.monopoly_li.Player;
import com.example.monopoly_li.Utils;
import javafx.scene.control.Alert;

import java.util.function.Consumer;

public class Action extends Cell {
    private final String description;
    private final Consumer<Player> effect;
//    private final Consumer<Card> cardEffect;
    
    // constructor for cards
    public Action(String description, Consumer<Player> effect) {
        super();
        this.description = description;
        this.effect = effect;
//        cardEffect = null;
    }
    
    // constructor for action spaces ex: go, go to jail, free parking, etc.
    public Action(String name, String description, Type type, int id, Consumer<Player> effect) {
        super(name, type, id);
        this.description = description;
        this.effect = effect;
//        cardEffect = null;
    }
    
    // constructor for card spaces ex: community chest, chance
//    public Action(String name, Type type, int id, Consumer<Card> cardEffect, boolean overload) {
//        super(name, type, id);
//        description = "";
//        effect = null;
//        this.cardEffect = cardEffect;
//    }
    
    public String getDescription() {
        return description;
    }
    
    public void execute(Player player) {
        if(effect != null)
            effect.accept(player);
        else
            Utils.errorAlert(
                    Alert.AlertType.ERROR,
                    "Error In execute",
                    "Cannot Execute Because 'effect' Is Null",
                    "Please Try Again"
            );
    }
//    public void execute(Action action) {
//        if(cardEffect != null)
//            cardEffect.accept(action);
//        else
//            Utils.errorAlert(
//                    Alert.AlertType.ERROR,
//                    "Error In execute(Action)",
//                    "Cannot Execute Because 'cardEffect' Is Null",
//                    "Try Execute(Player)"
//            );
//    }
}