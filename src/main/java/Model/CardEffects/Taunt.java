package Model.CardEffects;

import Controller.GameController;
import Model.Player;
import Model.PlayerID;

public class Taunt extends  Effect{
    private GameController gameController;
    private PlayerID playerID;
    /**
     * Denna effekt ska göra så att motståndarens kort endast för attackera detta kort först,
     * inte förens detta kortet är död (Hp = 0) så får motståndarens kort attackera spelarens andra kort på brädan.
     * Gälller även att motståndaren inte kan attackera spelarna egna hp heller.
     */
    public Taunt(){

    }
    @Override
    public void activateEffekt(GameController gameController, PlayerID owner, Player player, boolean isPlayersTurn) {

    }
}
