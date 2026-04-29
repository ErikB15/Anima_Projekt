package Model;

public class Board {
    private Card[] playerOneSlots;
    private Card[] playerTwoSlots;

    /**
     * Det viktiga är att board är rätt dum och simpel, vi vill att board ska ha koll på korten på plan.
     * Det är allt.
     * Constructor är alltså väldigt simpel!
     * Den skapar "slots" dvs platserna för spelare 1 och spelare 2 på planen.
     * @author Jim Ström
     */
    public Board(){
        playerOneSlots = new Card[4];
        playerTwoSlots = new Card[4];
    }

    /**
     * En simpel metod för att placera korten, finns en "safety" check som inte BORDE vara nödvändig.
     * Men är bra att ha!
     * Först hämtas alla de olika korten för spelaren.
     * @param player - Enum:et som säger vilken spelare är spelare 1 och vilken är spelare 2.
     * @param boardIndex Indexet på "brädet" där kortet ska bort.
     * @param card - Kortet som vill placeras. (Hämtas antagligen från "Player" klassens hand.
     * @return - returnerar en boolean om det gick eller inte.
     * @author Jim Ström
     */
    public boolean placeCard(PlayerID player, int boardIndex, Card card) {
        Card[] slots = getSlotsForPlayer(player);

        if (slots[boardIndex] != null) {
            return false;
        }

        slots[boardIndex] = card;
        return true;
    }

    /**
     * Metoden tar bort ett kort genom att använda sig av indexen på kortet som ska bort.
     * Samt enum:et för att veta vilken spelares sida kortet ska bort från.
     * @param player - Enum:et som säger vilken spelare är spelare 1 och vilken är spelare 2.
     * @param boardIndex - Indexet på "brädet" där kortet ska bort.
     * @return - Ger tillbaka det förstörda kortet (för hantering i controllern).
     * @author Jim Ström
     */
    public Card removeCard(PlayerID player, int boardIndex) {
        Card[] slots = getSlotsForPlayer(player);
        Card removedCard = slots[boardIndex];
        slots[boardIndex] = null;
        return removedCard;
    }

    /**
     * Hämtar den sidan som spelaren äger, genom att använda enum:et "PlayerID" så blir det rätt lättläsligt.
     * @param player - Enum:et som säger vilken spelare är spelare 1 och vilken är spelare 2.
     * @return - Ger tillbaka Card array:en som har dem olika korten på plan.
     * @author Jim Ström
     */
    public Card[] getSlotsForPlayer(PlayerID player){
        return player == PlayerID.PLAYER_ONE ? playerOneSlots : playerTwoSlots;

    }

    /**
     * Hämtar motståndarens array av kort. Används främst utanför denna klassen, för GUI:et och controllern.
     * @param player - Enum:et som säger vilken spelare är spelare 1 och vilken är spelare 2.
     * @return - Ger tillbaka card array:en som har motståndarens kort på plan.
     * @author Jim Ström
     */
    public Card[] getOpponentSlots(PlayerID player) {
        return player == PlayerID.PLAYER_ONE ? playerTwoSlots : playerOneSlots;
    }

    /**
     * Hämtar ett specifikt kort på spelplanen
     * @param player - Enum:et som säger vilken spelare är spelare 1 och vilken är spelare 2.
     * @param boardIndex - Indexet på "brädet" där kortet ska bort.
     * @return - Kortet som behövde hämtas.
     * @author Jim Ström
     */
    public Card getCard(PlayerID player, int boardIndex) {
        return getSlotsForPlayer(player)[boardIndex];
    }
}
