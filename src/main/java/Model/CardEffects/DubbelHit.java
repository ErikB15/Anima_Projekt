package Model.CardEffects;

import Controller.GameController;
import Model.Player;
import Model.PlayerID;

public class DubbelHit extends Effect{
    private GameController gameController;
    private PlayerID playerID;

    /**
     * Denna effekt ska möjligöra att kortet med denna effekt kan få attackera 2 gånger per runda istället för det vanliga 1 attack per runda.
     */
    public DubbelHit(){

    }
    @Override
    public void activateEffect(GameController gameController, PlayerID owner, Player player, boolean isPlayersTurn) {

    }
}
