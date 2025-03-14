package com.example.monopoly_li.Square;

import java.util.ArrayList;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: Holds information for each Card, accessed by multiple
    classes, card data is initialized each game by SQL stored data.
    Used for spaces that draw cards (ex: chance, chest).
    Inherits from Cell class.
*/

public class Card extends Cell {
    private final Action card;
    
    public Card(boolean isChest) {
        super(checkChest(isChest), checkType(isChest));
        
        ArrayList<Action> cards = new ArrayList<>();
        
        // region Good Cards
        cards.add(new Action(
                "Advance to GO",
                player -> {
                    player.setPosition(0);
                    player.passGo();
                }
        ));
        
        cards.add(new Action(
                "Bank pays you $50",
                player -> player.addBalance(50)
        ));
        
        cards.add(new Action(
                "Advance to Boardwalk",
                player -> player.setPosition(39)
        ));
        
        cards.add(new Action(
                "You inherit $100",
                player -> player.addBalance(100)
        ));
        
        cards.add(new Action(
                "From sale of stock you get $45",
                player -> player.addBalance(45)
        ));
        // endregion
        
        // region Bad Cards
        cards.add(new Action(
                "Go directly to Jail. Do not pass GO, do not collect $200.",
                player -> player.setInJail(true)
        ));
        
        cards.add(new Action(
                "Doctor’s fees. Pay $50",
                player -> player.removeBalance(50)
        ));
        // endregion
        
        card = cards.get((int) (Math.random() * ((isChest) ? 4 : cards.size())));
    }
    
    public Action getCard() {
        return card;
    }
    
    // region super() helper methods
    private static String checkChest(boolean isChest) {
        return (isChest) ? "Community Chest" : "Chance Card";
    }
    
    private static Type checkType(boolean isChest) {
        return (isChest) ? Type.CHEST : Type.CHANCE;
    }
    // endregion
}