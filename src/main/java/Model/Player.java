package Model;

import java.util.ArrayList;

public class Player {
    private int hp;
    private ArrayList<Card> cardDeck;

    public Player(){
        this.hp = 30; // PS. 30 Är bara test "value" just nu.
        this.cardDeck = new ArrayList<Card>();
    }

    public void addCard(Card addedCard){
        cardDeck.add(addedCard);
    }
}
