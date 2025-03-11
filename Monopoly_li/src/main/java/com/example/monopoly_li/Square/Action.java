package com.example.monopoly_li.Square;

import com.example.monopoly_li.Player;
import java.util.function.Consumer;

public class Action extends Cell {
    private final String description;
    private Consumer<Player> effect;
    private Consumer<Card> cardEffect;
    
    // constructor for cards
    public Action(String description, Consumer<Player> effect) {
        super();
        this.description = description;
        this.effect = effect;
    }
    
    // constructor for action spaces ex: go, go to jail, free parking, etc.
    public Action(String name, Type type, int id, Consumer<Player> effect) {
        super(name, type, id);
        description = "";
        this.effect = effect;
    }
    
    // constructor for card spaces ex: community chest, chance
    public Action(String name, Type type, int id, Consumer<Card> cardEffect, boolean overload) {
        super(name, type, id);
        description = "";
        this.cardEffect = cardEffect;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void execute(Player player) {
        effect.accept(player);
    }
}