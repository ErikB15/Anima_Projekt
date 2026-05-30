package Model;
import Controller.GameController;

import java.util.ArrayList;
import java.util.Comparator;

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
        if(canIKillPlayer()){return;}
        amIGonnaDie();
        playCards();
        attack();
        controller.endTurn();
    }


    /**
     *
     */
    public boolean canIKillPlayer(){
        ArrayList<Integer> computerIndexCards = getValidAttackers();
        if(computerIndexCards.isEmpty()){return false;}
        int damage = 0;
        int playerHP = gameState.getPlayerOne().getHp();
        for(int i = 0; i < computerIndexCards.size(); i++){
            damage += gameState.getBoard().getCard(PlayerID.PLAYER_TWO, computerIndexCards.get(i)).getCardAD();
        }
        if(damage >= playerHP){
            for(int i = 0; i < computerIndexCards.size(); i++){
                controller.attackPlayer(computerIndexCards.get(i));
            }
            return true;
        }
        return false;
    }

    /**
     *
     */
    public void amIGonnaDie(){
        ArrayList<Integer> playerIndexCards = getValidTargets();
        ArrayList<Integer> computerIndexCards = getValidAttackers();

        if(playerIndexCards.isEmpty()){return;}

        int damage = 0;
        int computerHP = gameState.getPlayerTwo().getHp();

        for(int i = 0; i < playerIndexCards.size(); i++){
            damage += gameState.getBoard().getCard(PlayerID.PLAYER_ONE, playerIndexCards.get(i)).getCardAD();
        }

        if(!(damage >= computerHP)){return;}
        if(computerIndexCards.isEmpty()){return;}

        int highestDamage = -1;
        int indexOfHighestDamage = -1;

        int secondHighestDamage = -1;
        int indexOfSecondHighestDamage = -1;

        for (int i = 0; i < playerIndexCards.size();i++){
            int attack = gameState.getBoard().getCard(PlayerID.PLAYER_ONE,playerIndexCards.get(i)).getCardAD();
            if (attack > highestDamage){
                secondHighestDamage = highestDamage;
                indexOfSecondHighestDamage = indexOfHighestDamage;
                highestDamage = attack;
                indexOfHighestDamage = playerIndexCards.get(i);
            } else if (attack > secondHighestDamage) {
                secondHighestDamage = attack;
                indexOfSecondHighestDamage = playerIndexCards.get(i);
            }
        }



        for (int i = 0; i < computerIndexCards.size();i++){
            Card highDMGCard = gameState.getBoard().getCard(PlayerID.PLAYER_ONE,indexOfHighestDamage);
            Card secondHighDMGCard = null;
            if(indexOfSecondHighestDamage != -1){
                secondHighDMGCard = gameState.getBoard().getCard(PlayerID.PLAYER_ONE, indexOfSecondHighestDamage);
            }

            if(highDMGCard != null){
            controller.attackCard(computerIndexCards.get(i),indexOfHighestDamage);
            continue;
            }

            if(secondHighDMGCard != null){
                controller.attackCard(computerIndexCards.get(i),indexOfSecondHighestDamage);
            }
        }

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

            ArrayList<Integer> affordableCards = new ArrayList<>();
            for(int k = 0; k < playerTwo.getHand().size();k++){
                if(playerTwo.getHp() > playerTwo.getHand().get(k).getCardCost()){
                    affordableCards.add(k);
                }
            }
            if (affordableCards.isEmpty()){break;}
            int handIndex = affordableCards.get((int)(Math.random() * affordableCards.size()));
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

            if(validAttackers.isEmpty()){return;}
            int attackerIndex = validAttackers.get((int)(Math.random() * validAttackers.size()));
            if(validTargets.isEmpty()){controller.attackPlayer(attackerIndex);
            } else {
                int defenderIndex = validTargets.get((int)(Math.random() * validTargets.size()));
                controller.attackCard(attackerIndex, defenderIndex);
            }
        }
    }


    private ArrayList<Integer> getValidAttackers(){
        ArrayList<Integer> validAttackers = new ArrayList<>();
        Card[] computerCardsOnBoard = gameState.getBoard().getSlotsForPlayer(PlayerID.PLAYER_TWO);
        for(int i = 0; i < computerCardsOnBoard.length; i++){
            if (computerCardsOnBoard[i] != null && !computerCardsOnBoard[i].getAsleep() &&
                    !computerCardsOnBoard[i].getHasAttackedThisTurn()) {validAttackers.add(i);}
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
