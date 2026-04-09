package Model;

import java.util.ArrayList;

public class Board {
    private ArrayList<Card> playerOneCards;
    private ArrayList<Card> playerTwoCards;
    private ArrayList<Card> playerOneDeadCards;
    private ArrayList<Card> playerTwoDeadCards;

    public Board(ArrayList<Card> playerOneCards, ArrayList<Card> playerTwoCards){
        this.playerOneCards = playerOneCards;
        this.playerTwoCards = playerTwoCards;
        this.playerOneDeadCards = new ArrayList<Card>();
        this.playerTwoDeadCards = new ArrayList<Card>();

    }
}
