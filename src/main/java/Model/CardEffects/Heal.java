package Model.CardEffects;

import Controller.GameController;
import Model.Card;
import Model.Player;
import Model.PlayerID;

import java.util.ArrayList;

public class Heal extends Effect{
    /**
     * Denna effekt gör så att när kortet placeras på brädan ska den heala alla spelarens kort på spelplanen med 5Hp och healar spelarens egna hp med 1.
     * Effekten aktiveras varenda gång i början av spelarens runda.
     *
     * @author Erik
     */
    public Heal(){

    }
    @Override
    public void activateEffekt(GameController gameController, PlayerID playerID, Player player, boolean isPlayersTurn) {

        while(isPlayersTurn) {

            ArrayList<Card> cards = gameController.getCardsOnSide(playerID);

            for (Card c : cards) {
                c.setCardCurrentHP(c.getCardCurrentHP() + 7);
            }

            player.setHp(player.getHp() + 1);

            isPlayersTurn=false;
        }
    }
}
