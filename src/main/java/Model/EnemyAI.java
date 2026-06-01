package Model;
import Controller.GameController;

import java.util.ArrayList;

public class EnemyAI {

    private GameController controller;
    private GameState gameState;

    private boolean aggressive;
    private boolean balanced;
    private boolean defensive;

    private int totalDeckAttack;
    private int totalDeckHealth;

    public EnemyAI(GameController controller, GameState gameState) {
        this.controller = controller;
        this.gameState = gameState;

        totalDeckAttack = 0;
        totalDeckHealth = 0;

        int random = (int)(Math.random() * 3);
        if(random == 0){
            aggressive = true;
            defensive = false;
            balanced = false;
        } else if (random == 1) {
            aggressive = false;
            defensive = true;
            balanced = false;
        }else {
            aggressive = false;
            defensive = false;
            balanced = true;
        }
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
     * AI:n kollar om den kan döda spelaren med alla sina kort som kan attackera.
     * Ifall AI:n kan döda spelaren, så dödar den spelaren.
     * Kan den inte döda spelaren? Då skiter den i det och går tillbaka.
     * @author Jim Ström
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
     * Här kollar vi om AI:n kommer dö ifall att motståndaren attackerar med alla korten som kan attackera.
     * Är det så att datorn kommer att dö, så kommer datorn helt prioritera att döda de starkaste korten spelaren har.
     * Sen fortsätter det som vanligt oavsett ifall datorn lyckades eller inte.
     * @author Jim Ström
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
        int indexOfHighest = -1;

        int secondHighestDamage = -1;
        int indexOfSecondHighest = -1;

        for (int i = 0; i < playerIndexCards.size();i++){
            int attack = gameState.getBoard().getCard(PlayerID.PLAYER_ONE,playerIndexCards.get(i)).getCardAD();
            if (attack > highestDamage){
                secondHighestDamage = highestDamage;
                indexOfSecondHighest = indexOfHighest;
                highestDamage = attack;
                indexOfHighest = playerIndexCards.get(i);
            } else if (attack > secondHighestDamage) {
                secondHighestDamage = attack;
                indexOfSecondHighest = playerIndexCards.get(i);
            }
        }



        for (int i = 0; i < computerIndexCards.size();i++){
            Card highDMGCard = gameState.getBoard().getCard(PlayerID.PLAYER_ONE, indexOfHighest);
            Card secondHighDMGCard = null;
            if(indexOfSecondHighest != -1){
                secondHighDMGCard = gameState.getBoard().getCard(PlayerID.PLAYER_ONE, indexOfSecondHighest);
            }

            if(highDMGCard != null){
            controller.attackCard(computerIndexCards.get(i), indexOfHighest);
            continue;
            }

            if(secondHighDMGCard != null){
                controller.attackCard(computerIndexCards.get(i), indexOfSecondHighest);
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
        Player computer = gameState.getPlayerTwo();
        Board board = gameState.getBoard();
        for (int i = 0; i < gameState.getMaxCardsToPlayPerTurn(); i++) {
            if (!board.hasEmptySlot(PlayerID.PLAYER_TWO) || gameState.getCardsPlayedThisTurn() >= gameState.getMaxCardsToPlayPerTurn()) {break;}
            if (computer.getHand().isEmpty()) {
                computer.drawUntilHandIsFull();}
            if (computer.getHand().isEmpty() || !board.hasEmptySlot(PlayerID.PLAYER_TWO)) {break;}

            ArrayList<Integer> affordableCards = new ArrayList<>();
            for(int k = 0; k < computer.getHand().size(); k++){
                if(computer.getHp() > computer.getHand().get(k).getCardCost()){
                    affordableCards.add(k);
                }
            }
            if (affordableCards.isEmpty()){break;}
            int bestScoreIndex = scoreCards(affordableCards);

            for(int k = 0; k < board.getSlotsForPlayer(PlayerID.PLAYER_TWO).length; k++){
                if (board.getSlotsForPlayer(PlayerID.PLAYER_TWO)[k] == null){
                    controller.placeCard(bestScoreIndex, k);
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

            int faceScore = (int)(Math.random() * 101);
            int cardScore = (int)(Math.random() * 101);

            if(aggressive){faceScore += 25;}
            if(defensive){cardScore += 25;}

            if(validTargets.isEmpty() || faceScore > cardScore){controller.attackPlayer(attackerIndex);
            } else {
                int defenderIndex = scoreAttackers(validTargets);
                controller.attackCard(attackerIndex, defenderIndex);
            }
        }
    }


    /**

     * @param allCards
     * @return
     * @author Jim Ström
     */
    public int scoreDraftCards(Card[] allCards){
        int bestScore = -1;
        int bestIndex = -1;



        for(int i = 0; i < allCards.length;i++){
            if(allCards[i] == null){
                continue;
            }

            Card cardBeingScored = allCards[i];

            int totalScore = calculateDraftScore(cardBeingScored) + calculateRatioScore(cardBeingScored);

            if(totalScore > bestScore){
                bestScore = totalScore;
                bestIndex = i;
            }
        }
        this.totalDeckHealth += allCards[bestIndex].getCardMaxHP();
        this.totalDeckAttack += allCards[bestIndex].getCardAD();


        double adPercentage = ((double) totalDeckAttack / (totalDeckAttack + totalDeckHealth)) * 100;

        double hpPercentage = ((double) totalDeckHealth / (totalDeckAttack + totalDeckHealth)) * 100;

        if (aggressive){
            System.out.println("Personality: Aggressive");

            System.out.println("Attack: " + totalDeckAttack);
            System.out.println("Health: " + totalDeckHealth);

            System.out.printf("AD: %.1f%%%n", adPercentage);
            System.out.printf("HP: %.1f%%%n", hpPercentage);
        } else if (defensive) {
            System.out.println("Personality: Defensive");

            System.out.println("Attack: " + totalDeckAttack);
            System.out.println("Health: " + totalDeckHealth);

            System.out.printf("AD: %.1f%%%n", adPercentage);
            System.out.printf("HP: %.1f%%%n", hpPercentage);
        }else {
            System.out.println("Personality: Balanced");

            System.out.println("Attack: " + totalDeckAttack);
            System.out.println("Health: " + totalDeckHealth);

            System.out.printf("AD: %.1f%%%n", adPercentage);
            System.out.printf("HP: %.1f%%%n", hpPercentage);
            
        }
        return bestIndex;
    }


    /**
     * Kolla vilket kort av motståndaren som är bäst och bör attackeras.
     * Detta beror på en del modifiers som finns med i calculateAttackScore();
     * @param validTargets - De olika motståndare korten som kan attackeras.
     * @return Indexet på kortet som vi vill attackera.
     * @author Jim Ström
     */
    private int scoreAttackers(ArrayList<Integer> validTargets){
        int bestScore = -1;
        int bestIndex = -1;

        for(int i = 0; i < validTargets.size();i++){
            Card cardBeingScored = gameState.getBoard().getCard(PlayerID.PLAYER_ONE,validTargets.get(i));
            int totalScore = calculateAttackScore(cardBeingScored);

            if(totalScore > bestScore){
                bestScore = totalScore;
                bestIndex = validTargets.get(i);
            }
        }

        return bestIndex;
    }

    /**
     * Här värdesätter vi vilket kort AI:n vill spela, utgår också från olika modifiers som finns i metoden-
     * calculateHandScore;
     * Sedan skickar vi tillbaka indexet på kortet som metoden anser borde spelas.
     * @param affordableCards - Kort som kan spelas utan att spelaren dör.
     * @return Index på kortet som ska spelas.
     * @author Jim Ström
     */
    private int scoreCards(ArrayList<Integer> affordableCards){
        Player computer = gameState.getPlayerTwo();

        int bestScore = -1;
        int bestIndex = -1;

        for(int i = 0; i < affordableCards.size();i++){
            Card cardBeingScored = computer.getHand().get(affordableCards.get(i));
            int totalScore = calculateHandScore(cardBeingScored);

            if(totalScore > bestScore){
             bestScore = totalScore;
             bestIndex = affordableCards.get(i);
            }
        }
        return bestIndex;
    }

    /**
     * Utgår från några parametrar, men tar även personligheterna i åtanke.
     * Om det är en aggressiv AI, vill den helst spela kort som har hög attack.
     * Är det en defensiv AI, vill den helst spela kort som har hög HP.
     * Är det en balanserad AI, utgår den från det vanliga tankesättet.
     * @param card Kortet som ska evalueras.
     * @return Det totala värdet på de olika uträkningarna.
     * @author Jim Ström
     */
    private int calculateHandScore(Card card){
        int attackScore = card.getCardAD() * 2;
        int healthScore = card.getCardMaxHP();
        int totalScore = attackScore + healthScore;

        if(aggressive){totalScore += attackScore/2;}
        if(defensive){totalScore += healthScore/2;}

        return totalScore;

    }

    /**
     * Utgår från parametrar och tar personligheterna extra i åtanke.
     * Här vill vi evaluera ett visst poäng för hur mycket vi vill attackera kortet i fråga.
     * @param card Kort objektet som ska evalueras.
     * @return Indexet på kortet som vi vill attackera.
     * @author Jim Ström
     */
    private int calculateAttackScore(Card card){
        int attackScore = card.getCardAD();
        int healthScore = card.getCardMaxHP();
        int totalScore = (attackScore * 3) + healthScore;

        if(aggressive){totalScore += attackScore;}
        if(defensive){totalScore += healthScore/2;}

        return totalScore;
    }


    private int calculateDraftScore(Card card){
        int attackScore = card.getCardAD();
        int healthScore = card.getCardMaxHP();
        int totalScore = 0;

        if(aggressive){totalScore = (4 * attackScore) + healthScore;}
        if(defensive){totalScore = attackScore + (healthScore * 3);}
        if(balanced){totalScore = (attackScore * 3) + (healthScore * 2);}

        return totalScore;
    }

    private int calculateRatioScore(Card card){
        double attackPoints = card.getCardAD() + totalDeckAttack;
        double healthPoints = card.getCardMaxHP() + totalDeckHealth;
        double totalPoints = (attackPoints + healthPoints);

        double attackRatio = attackPoints/totalPoints;

        if(aggressive){
            double targetRatio = 0.70;
            double distance = Math.abs(targetRatio - attackRatio);
            return Math.round((float)(70 - (distance * 70)));
        } else if (defensive){
            double targetRatio = 0.30;
            double distance = Math.abs(targetRatio - attackRatio);
            return Math.round((float)(70 - (distance * 70)));
        } else{
            double targetRatio = 0.50;
            double distance = Math.abs(targetRatio - attackRatio);
            return Math.round((float)(80 - (distance * 80)));
        }
    }


    /**
     * Hämtar de olika korten AI:n har som kan attackera, det vill säga de index där kort existerar och kan attackera.
     * @return Ger tillbaka en lista på alla index för kort som kan attackera.
     * @author Jim Ström
     */
    private ArrayList<Integer> getValidAttackers(){
        ArrayList<Integer> validAttackers = new ArrayList<>();
        Card[] computerCardsOnBoard = gameState.getBoard().getSlotsForPlayer(PlayerID.PLAYER_TWO);
        for(int i = 0; i < computerCardsOnBoard.length; i++){
            if (computerCardsOnBoard[i] != null && !computerCardsOnBoard[i].getAsleep() &&
                    !computerCardsOnBoard[i].getHasAttackedThisTurn()) {validAttackers.add(i);}
        }
        return validAttackers;
    }

    /**
     * Hämtar de olika motståndare korten som kan bli attackerade.
     * @return Ger tillbaka en lista på alla index för motståndarens kort som kan bli attackerade.
     * @author Jim Ström
     */
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
