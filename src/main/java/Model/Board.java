package Model;

public class Board {
    private Card[] playerOneSlots;
    private Card[] playerTwoSlots;

    public Board(){
        playerOneSlots = new Card[4];
        playerTwoSlots = new Card[4];
    }

    public Card[] getSlotsForPlayer(PlayerID player){
        return player == PlayerID.PLAYER_ONE ? playerTwoSlots : playerOneSlots;

    }

    public Card[] getOpponentSlots(PlayerID player) {
        return player == PlayerID.PLAYER_ONE ? playerTwoSlots : playerOneSlots;
    }

    public boolean placeCard(PlayerID player, int slotIndex, Card card) {
        Card[] slots = getSlotsForPlayer(player);

        if (slots[slotIndex] != null) {
            return false;
        }

        slots[slotIndex] = card;
        return true;
    }

    public Card removeCard(PlayerID player, int slotIndex) {
        Card[] slots = getSlotsForPlayer(player);
        Card removedCard = slots[slotIndex];
        slots[slotIndex] = null;
        return removedCard;
    }

    public Card getCard(PlayerID player, int slotIndex) {
        return getSlotsForPlayer(player)[slotIndex];
    }
}
