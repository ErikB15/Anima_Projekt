package Model;
import Controller.GameController;
public class EnemyAI {

    private GameController controller;
    private GameState gameState;

    public EnemyAI(GameController controller, GameState gameState) {
        this.controller = controller;
        this.gameState = gameState;
    }

    public void takeTurn() {
        playCards();
        attack();
        controller.endTurnSinglePLayer();
    }


    public void playCards(){
        Player playerOne = gameState.getPlayerOne();
        Player playerTwo = gameState.getPlayerTwo();
        Board board = gameState.getBoard();
        for (int i = 0; i < gameState.getMaxCardsToPlayPerTurn(); i++) {
            if (!board.hasEmptySlot(PlayerID.PLAYER_TWO)) {
                break;
            }
            if (gameState.getCardsPlayedThisTurn() >= gameState.getMaxCardsToPlayPerTurn()) {
                break;
            }

            if (playerTwo.getHand().isEmpty()) {playerTwo.drawUntilHandIsFull();}

            if (playerTwo.getHand().isEmpty()) {break;}

            if (!board.hasEmptySlot(PlayerID.PLAYER_TWO)) {break;}

            int handIndex = (int) (Math.random() * playerTwo.getHand().size());

            Card card = playerTwo.getHand().get(handIndex);

            int boardIndex = 0;
            for(int k = 0; k < board.getSlotsForPlayer(PlayerID.PLAYER_TWO).length; k++){
                if (board.getSlotsForPlayer(PlayerID.PLAYER_TWO)[k] == null){
                    controller.placeCard(handIndex, k);
                    break;
                }
            }
        }
    }

    public void attack(){

    }
}
