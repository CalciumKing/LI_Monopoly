package com.example.monopoly_li.Square;

import com.example.monopoly_li.Player;
import java.util.ArrayList;

public class Card extends Cell {
    private final ArrayList<Action> cards;
    
    public Card(boolean isChest) {
        super(checkChest(isChest), checkType(isChest));
        cards = new ArrayList<>();
        
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
                Player::goToJail
        ));
        
        cards.add(new Action(
                "Doctor’s fees. Pay $50",
                player -> player.removeBalance(50)
        ));
        // endregion
    }
    
    private static String checkChest(boolean isChest) {
        return (isChest) ? "Community Chest" : "Chance Card";
    }
    
    private static Type checkType(boolean isChest) {
        return (isChest) ? Type.CHEST : Type.CHANCE;
    }
    
    public Action drawCard() {
        return cards.get((int)(Math.random() * ((getType() == Type.CHEST) ? 6 : cards.size())));
    }
}