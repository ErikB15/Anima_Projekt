package Model.CardEffects;

import Controller.GameController;
import Model.Player;
import Model.PlayerID;

/**
 * Rent krast kommer klassen antagligen inte användas så där jätte jätte mycket. Men den är här för att-
 * göra det enklare att applicera polymorphism och enkelt använda effekterna.
 * Genom att ha denna klassen kan vi ha en metod här som säger activateEffekt().
 * Detta tillåter oss att spara alla effekter på samma ställe men endå lyckas utnyttja alla effekter effektivt.
 */
public abstract class Effect {
    private int id;

    public abstract void activateEffekt(GameController gameController, PlayerID owner, Player player, boolean isPlayersTurn);

    public void setId(int id) {
        this.id = id;
    }
}
