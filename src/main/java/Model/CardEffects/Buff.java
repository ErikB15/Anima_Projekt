package Model.CardEffects;

import Controller.GameController;
import Model.Card;
import Model.Player;
import Model.PlayerID;

import java.util.ArrayList;

public class Buff extends Effect{
    private GameController gameController;

    /**
     * Denna effekt gör så att alla spelarens kort på brädan gör 1 extra skada varje gång det är spelarens tur.
     * Effekten tar slut när kortet dödas. Effekten gäller också på detta kortet.
     */
    public Buff(){

    }

    @Override
    public void activateEffekt(GameController gameController, PlayerID playerID, Player player, boolean isPlayersTurn) {

        while(isPlayersTurn) {

            ArrayList<Card> cards = gameController.getCardsOnSide(playerID);

            for (Card c : cards) {
                c.setCardAD(c.getCardAD() + 1);
            }

            isPlayersTurn=false;
        }
    }
}
