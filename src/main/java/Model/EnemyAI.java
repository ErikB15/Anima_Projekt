package Model;
import Controller.GameController;

import java.util.ArrayList;

public class EnemyAI {

    private GameController controller;
    private GameState gameState;

    public EnemyAI(GameController controller, GameState gameState) {
        this.controller = controller;
        this.gameState = gameState;
    }

    /**
     * Det som gör att AI:n faktiskt tar sin runda, spelar först korten den kan spela.
     * Sedan attackerar den det den kan attackera.
     * Sedan avslutar den sin rond igen.
     * @author Jim Ström
     */
    public void takeTurn() {
        playCards();
        attack();
        controller.endTurn();
    }


    /**
     * Simpelt nog spelar den alla korten som datorn kan spela.
     * Där finns många if checks, men dem flesta av dem förklarar sig själv.
     * Där finns två for-loopar, den ena är för att kolla hur många kort spelaren kan som max spela.
     * Den andra loopen är för att gå igenom alla utrymmen på planen, där spelaren lägger kortet som inte är fullt.
     * @author Jim Ström
     */
    public void playCards(){
        Player playerTwo = gameState.getPlayerTwo();
        Board board = gameState.getBoard();
        for (int i = 0; i < gameState.getMaxCardsToPlayPerTurn(); i++) {
            if (!board.hasEmptySlot(PlayerID.PLAYER_TWO) || gameState.getCardsPlayedThisTurn() >= gameState.getMaxCardsToPlayPerTurn()) {break;}
            if (playerTwo.getHand().isEmpty()) {playerTwo.drawUntilHandIsFull();}
            if (playerTwo.getHand().isEmpty() || !board.hasEmptySlot(PlayerID.PLAYER_TWO)) {break;}

            int handIndex = (int) (Math.random() * playerTwo.getHand().size());

            for(int k = 0; k < board.getSlotsForPlayer(PlayerID.PLAYER_TWO).length; k++){
                if (board.getSlotsForPlayer(PlayerID.PLAYER_TWO)[k] == null){
                    controller.placeCard(handIndex, k);
                    break;
                }
            }
        }
    }

    /**
     * Den här metoden gjordes om flertalet gånger, men landade i att hitta vilka kort som kan attackera samt
     * vilka kort som kan bli attackerade.
     * Sedan finns där som vanligt flertalet if satser som kollar diverse grejer.
     * Samt en stor loop som går ska kunna hantera alla korten på planen. Loopen är gjord för att loop 4 gånger.
     * Det är allt den gör, det finns nog finare sätt att hantera allt på men detta går för nu.
     * @author Jim Ström
     */
    public void attack(){
        for(int attackCount = 0; attackCount < 4; attackCount++){
            ArrayList<Integer> validAttackers = getValidAttackers();
            ArrayList<Integer> validTargets = getValidTargets();

            if(validAttackers.isEmpty()){
                return;
            }
            int attackerIndex =
                    validAttackers.get(
                            (int)(Math.random() * validAttackers.size())
                    );

            if(validTargets.isEmpty()){
                controller.attackPlayer(attackerIndex);

            } else {

                int defenderIndex =
                        validTargets.get(
                                (int)(Math.random() * validTargets.size())
                        );

                controller.attackCard(attackerIndex, defenderIndex);
            }
        }
    }


    private ArrayList<Integer> getValidAttackers(){
        ArrayList<Integer> validAttackers = new ArrayList<>();
        Card[] computerCardsOnBoard = gameState.getBoard().getSlotsForPlayer(PlayerID.PLAYER_TWO);
        for(int i = 0; i < computerCardsOnBoard.length; i++){
            if (computerCardsOnBoard[i] != null &&
                    !computerCardsOnBoard[i].getAsleep() &&
                    !computerCardsOnBoard[i].getHasAttackedThisTurn())
            {
                validAttackers.add(i);
            }
        }
        return validAttackers;
    }

    private ArrayList<Integer> getValidTargets(){
        ArrayList<Integer> validTargets = new ArrayList<>();
        Card[] playerCardsOnBoard = gameState.getBoard().getSlotsForPlayer(PlayerID.PLAYER_ONE);
        for(int i = 0; i < playerCardsOnBoard.length; i++){
            if (playerCardsOnBoard[i] != null){
                validTargets.add(i);
            }
        }
        return validTargets;
    }
}
