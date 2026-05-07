package Model.CardEffects;

import Controller.GameController;
import Model.Card;
import Model.Player;
import Model.PlayerID;

import java.util.ArrayList;

public class Poison extends Effect{
    private GameController gameController;

    /**
     * Effekten gör att varje gång det är spelarens tur att spela så tar alla motståndarens kort 1 skada
     * tills spelarnas kort med denna effekt dödas.
     */
    public Poison(){

    }

    @Override
    public void activateEffect(GameController gameController, PlayerID playerID, Player player, boolean isPlayersTurn) {
        while(isPlayersTurn) {

            ArrayList<Card> cards = gameController.getCardsOnSide(playerID);

            for (Card c : cards) {
                c.setCardCurrentHP(c.getCardCurrentHP() - 1);
            }

            isPlayersTurn=false;
        }
    }
}
