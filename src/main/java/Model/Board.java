package Model;

import java.util.ArrayList;

public class Board {
    private Card[] playerOneSlots;
    private Card[] playerTwoSlots;

    public Board(){
        playerOneSlots = new Card[4];
        playerTwoSlots = new Card[4];
    }

    public void attack(int attackingCard, int defendingCard){
        int defCardHP = playerTwoSlots[defendingCard].getCardCurrentHP();
        int atkCardHP = playerOneSlots[attackingCard].getCardCurrentHP();
        int defCardNewHP = (defCardHP - playerOneSlots[attackingCard].getCardAD());
        int atkCardNewHP = (atkCardHP - playerTwoSlots[defendingCard].getCardAD());
        playerTwoSlots[defendingCard].setCardCurrentHP(defCardNewHP);
        playerOneSlots[attackingCard].setCardCurrentHP(atkCardNewHP);

        if(defCardNewHP <= 0){
            sendCardToGraveyard(playerTwoSlots[defendingCard]);
        }
        if(atkCardNewHP <= 0){
            sendCardToGraveyard(playerOneSlots[attackingCard]);
        }
    }


    public void sendCardToGraveyard(Card card){

    }
}
