package Controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.*;
import Network.GameClient;
import Network.GameStateListener;
import Model.CardEffects.*;
import View.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * Game Controller klassen, syftet är att kontroller flödet av information från view och Model under matchens gång.
 * När matchens avslutas stängs även game controllern, då den endast är nödvändig när matchen är aktiv.
 */
public class GameController implements GameStateListener {
    private Card[] allCards;
    private ArrayList<Effect> allEffects;
    private GameClient gameClient; // nytt fält så vi kan snacka med gameclient

    //Mekanik för att byta plats på kort i playerOneActiveCards
    private int indexCardOnHandToMove;
    private int indexSpotToPlaceCard;
    private boolean cardPicked = false;
    private boolean spotPicked = false;
    private boolean attackTargetPicked = false;
    private int indexToCardToAttackWith = -1;
    private int indexToCardToAttack = -1;

    private boolean attackerPicked = false;
    private boolean defenderPicked = false;

    private Player playerOne;
    private Player playerTwo;
    private EnemyAI enemyAI;
    private Board board;
    private PlayerID localPlayerRole;
    private GUIManager guiManager;
    private Card testCard; //ENDAST FÖR TESTNING
    private GamePhase lastKnownPhase; // håller koll på serverns senaste fas så vi kan detektera DRAFT till PLAY övergång
    private GameState gameState;
    private DubbelHit dubbelHit;
    private Taunt taunt;
    private Heal heal;
    private Shield shield;
    private Poison poison;
    private Buff buff;
    private boolean SinglePlayer;

    /**
     * Skapar en ny GameController och initierar spelets grunddata.
     * Initierar listor för alla kort och effekter samt skapar spelare och spelbräde.
     * Anropar addAllCards() för att fylla spelet med alla hårdkodade kort.
     *
     * @author Erik,Jim Ström, Elna
     */
    public GameController(){
        allCards = new Card[12];
        allEffects = new ArrayList<>();
        playerOne = new Player("Player1");
        playerTwo = new Player("Player2"); //identifera spelare för servern såd e har ett namn
        board = new Board();
        gameState = new GameState(playerOne, playerTwo, board);
        enemyAI = new EnemyAI(this, gameState);
        addAllCards();

        dubbelHit = new DubbelHit();
        heal = new Heal();
        taunt = new Taunt();
        shield = new Shield();
        poison = new Poison();
        buff = new Buff();
    }


    /**
     * Lägger till alla kort som finns i spelet genom hårdkodad initiering.
     * Skapar Card-objekt och lägger dem i allCards-listan.
     *
     * @author Erik
     */
    public void addAllCards(){
        allCards[0] = new Card("Kenneth", 10,15,1,poison, "/CardPictures/Card1.png");
        allCards[1] = new Card("KnifeGuy", 13,12,2,poison, "/CardPictures/Card2.png");
        allCards[2] = new Card("Harrold", 1,30,3,poison, "/CardPictures/Card3.png");
        allCards[3] = new Card("George", 5,20,4,poison, "/CardPictures/Card4.png");
        allCards[4] = new Card("Monkey", 30,5,5,taunt, "/CardPictures/Card5.png");
        allCards[5] = new Card("Wizard", 30,5,6,poison, "/CardPictures/Card6.png");
        allCards[6] = new Card("blockHead", 1,35,7,shield, "/CardPictures/Card7.png");
        allCards[7] = new Card("Twins", 10,17,8,dubbelHit, "/CardPictures/Card8.png");
        allCards[8] = new Card("ChillGuy", 5,22,9,heal, "/CardPictures/Card9.png");
        allCards[9] = new Card("Bob", 15,8,10,buff, "/CardPictures/Card10.png");
        allCards[10] = new Card("Kick", 13,13,11,poison, "/CardPictures/Card11.png");
        allCards[11] = new Card("Pernilla", 2,28,12,poison, "/CardPictures/Card12.png");
    }


    public void startSingleplayer() {
        startDraftPhase();

        localPlayerRole = PlayerID.PLAYER_ONE;
        guiManager.setLocalRole(PlayerID.PLAYER_ONE);
        setSinglePLayer(true);
        guiManager.switchToPickCardScreen();

    }

    public void startMultiplayer(){
        // Här ska servern på något sätt definera vilken spelare som är PLAYER_ONE och vem som är PLAYER_TWO

        startDraftPhase();
        // Metoden under ska anropas här, men går inte för den behöver ett mouse event.
        // Metoden under kommer i framtiden antagligen bara anropas via controllern, så hade nog-
        // varit bäst om den inte behövde en mouse event.
        setSinglePLayer(false);
        //guiManager.switchToConnectScreen();
    }


    /**
     * Startas när vi går in i välja kort fasen.
     * Randomizer för att slumpmässigt välja vem som börjar välja.
     * Sen avslutas det med att sätta "GamePhase" till draft fasen.
     * @author Jim Ström
     */
    public void startDraftPhase(){
        int random = 1;  //endast för testning annars avänds raden under
        //int random = (int)(Math.random() * 2) + 1;
        if(random == 1){
            gameState.setFirstDraftPlayer(PlayerID.PLAYER_ONE);
            gameState.setCurrentPlayer(PlayerID.PLAYER_ONE);
        }else {
            gameState.setFirstDraftPlayer(PlayerID.PLAYER_TWO);
            gameState.setCurrentPlayer(PlayerID.PLAYER_TWO);
        }
        gameState.setPhase(GamePhase.DRAFT);
    }


    /**
     * Lik startDraftPhase, nu kollar vi på vilken spelare som började få kort i draft fasen.
     * Efteråt ser vi till att båda de kortlekarna spelarna fått blir blandande.
     * Sen drar båda spelarna tills deras hand är fylld.
     * Till sist sätter vi gameState till "Play".
     * @author Jim Ström
     */
    public void startPlayPhase(){
        guiManager.switchToGameBoard();
        guiManager.changePlayerHP();

        if(gameState.getFirstDraftPlayer() == PlayerID.PLAYER_ONE){
            gameState.setCurrentPlayer(PlayerID.PLAYER_TWO);
        } else {
            gameState.setCurrentPlayer(PlayerID.PLAYER_ONE);
        }

        Collections.shuffle(playerOne.getDeck());
        Collections.shuffle(playerTwo.getDeck());
        playerOne.drawUntilHandIsFull();
        playerTwo.drawUntilHandIsFull();
        gameState.setPhase(GamePhase.PLAY);

        for(int i = 0; i < playerOne.getHand().size(); i++){
            guiManager.renderCard(Zone.HAND, i, playerOne.getHand().get(i).getImagePath());
        }

        if(getSinglePlayer() && getCurrentPlayerId() == PlayerID.PLAYER_TWO){
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));

            pause.setOnFinished(e -> {
                enemyAI.takeTurn();
            });

            pause.play();
        }
    }


    /**
     * Denna metoden är den som ska kallas när en spelare försöker välja ett kort.
     * Bör finnas någon form av callback eller "updateGUI" metod i botten av denna koden.
     * Vi har två viktiga checks i början, vi kollar så att kortet som valts inte är utanför array listen.
     * Vi kollar dessutom att gameStaten är DRAFT.
     * Sen får personen som valt ett kort sitt kort och det tas bort från listan.
     * Därefter kollar vi ifall listan är tom, ifall den är tom så startar vi "playPhase".
     * Är den inte tom så bytas det vem som väljer kort nästa gång.
     *
     * PS. Det kan bli så att det behövs lägga till en check här som ser till att,
     * det är rätt spelare som försöker välja kort. Så att både spelarna inte kan välja kort samtidigt.
     * Är inte helt säker på hur det ska göras än
     * @param cardIndex - Index på kortet som väljs
     * @author Jim Ström
     */
    public void chooseCardPhase(int cardIndex){
        if (gameState.getPhase() != GamePhase.DRAFT) return;
        if (cardIndex < 0 || cardIndex >= allCards.length) return;
        if (allCards[cardIndex] == null) return;

        if (gameState.getCurrentPlayerId() != localPlayerRole) {
            return;
        }

        Card chosenCard = allCards[cardIndex];
        gameState.getCurrentPlayer().addCardToDeck(chosenCard);

        allCards[cardIndex] = null;

        if (isAllCardsEmpty()) {
            guiManager.updateGuiAfterCardIsPicked(cardIndex);

            PauseTransition delay = new PauseTransition(Duration.millis(200));
            delay.setOnFinished(e -> startPlayPhase());
            delay.play();
            return;
        }

        guiManager.switchTurnLabelInPickCard(getCurrentPlayerId());
        gameState.switchPlayer();
        guiManager.updateGuiAfterCardIsPicked(cardIndex);

        if(getSinglePlayer() == true){
            computerChooseCardInSinglePayer();
        }
    }

    /**
     * Hjälp metod som bara kollar om allCards är tom på kortobjekt
     *
     * @return true || false
     * @author Erik
     */
    private boolean isAllCardsEmpty(){
        for (Card c : allCards){
            if (c != null) return false;
        }
        return true;
    }


    /**
     * Okej detta blir ett långt javadoc men det behövs nog för att förklara denna metod.
     *
     * Metoden hanterar motståndarens kortval i singleplayer under draft-fasen.
     * Metoden körs efter att spelaren själv har valt ett kort och ansvarar för att simulera att en dator väljer själv.
     *
     * Processen styrs av två tidsfördröjningar (PauseTransition) som separerar logiken i två steg.
     *
     * Sidenote om PauseTransition: Jag vill fördröja valet av kort och "animationerna" för att det ska vara lite mer äkta
     * och lättare för användaren att förstå vad som händer så inte allting händer på ett ögonblick.
     * Och man kan inte använde thread.sleep för då hänger hela programmet sig. Skillanden med thread.sleep och pausetransition är att
     * thread.sleep får hela trådan att pause och det vill vi inte. PauseTransition är mer som en fördröjning av en aktivtet på javaFX tråden.
     * Med det kan vi schemalägga upgifter.
     *
     * 1. Pick (fördröjning innan motståndaren väljer kort)
     *    - Skapar en artificiell paus för att simulera "tänkandet".
     *    - Efter fördröjningen skannas alla kort i pickCardViews.
     *    - En lista av tillgängliga kort byggs genom att filtrera bort redan valda kort
     *      (selectedCardsInPickCardphase).
     *    - Ett slumpmässigt kort väljs från den kvarvarande listan.
     *    - Om inga kort återstår avslutas metoden och turen återgår till spelaren.
     *
     * 2. Kortval och uppdatering av spelstatus
     *    - Det valda ImageView-objektet markeras som valt och läggs till i selectedCardsInPickCardphase.
     *    - Kortets visuella representation byts till baksidan (CardBACKSIDE.png) för att indikera att det är taget.
     *    - Kortets HP-etikett uppdateras via changeHP för att reflektera att kortet inte längre är tillgängligt.
     *    - Kortobjektet hämtas från ImageView via getUserData och skickas till GameController via addCardToOpponent,
     *      vilket lägger till kortet i motståndarens deck och tar bort det från gemensamma kortpoolen.
     *
     * Viktigt:
     * - All faktisk spel-logik (korttilldelning och borttagning från kortpool) hanteras i GameController.
     * - gui-uppdateringar sker stegvis för att undvika att spelaren och motståndaren väljer samtidigt.
     * - pick.play() är det som faktiaskt "startar" det som står inom .setOnFinished.
     * Tänk det lite som en run metod, vi skapar tasken sen senare så startar vi den.
     *
     * @author Erik
     */
    private void computerChooseCardInSinglePayer() {

        PauseTransition pick = new PauseTransition(Duration.seconds(1));

        pick.setOnFinished(e -> {

            List<Integer> availableIndexes = new ArrayList<>();

            for (int i = 0; i < allCards.length; i++) {
                if (allCards[i] != null) {
                    availableIndexes.add(i);
                }
            }

            if (availableIndexes.isEmpty()) {
                return;
            }

            int randomIndex = (int) (Math.random() * availableIndexes.size());
            int chosenIndex = availableIndexes.get(randomIndex);

            Card card = allCards[chosenIndex];

            if (card == null) {
                return;
            }

            allCards[chosenIndex] = null;

            playerTwo.addCardToDeck(card);

            guiManager.updateGuiAfterCardIsPicked(chosenIndex);

            if (isAllCardsEmpty()) {
                guiManager.updateGuiAfterCardIsPicked(chosenIndex);

                PauseTransition delay = new PauseTransition(Duration.millis(200));
                delay.setOnFinished(action -> startPlayPhase());
                delay.play();
                return;
            }

            guiManager.switchTurnLabelInPickCard(getCurrentPlayerId());
            gameState.switchPlayer();
        });

        pick.play();
    }


    /**
     * Tanken är att denna metoden ska checka alla spelreglerna. Detta gör uppdelningen dels enklare men-
     * också gör att vi slipper ha väldigt stora metoder.
     * Så denna metoden hanterar spellogiken och alla checks som måste göras där.
     * Sen moveCardFromHandToBoard genomför själva GUI rörelsen.
     * Dessutom, kanske, ni märker att där finns många gameState.get/.set
     * Detta är för att som jag fattat är det bäst att röra informationen i gameState och spara den där.
     * Även ifall det ser väldigt fult ut rent kod -mässigt.
     * @param handIndex - Indexet på kortet i handen vi vill röra.
     * @param boardIndex - Index på brädan där vi vill placera kortet.
     * @return - Returnerar en boolean för ifall att det lyckades eller inte.
     */
    public boolean placeCard(int handIndex, int boardIndex){

        if(gameState.getPhase() != GamePhase.PLAY){return false;}

        Player currentPlayer = gameState.getCurrentPlayer();
        PlayerID currentPlayerID = gameState.getCurrentPlayerId();

        if (handIndex < 0 || handIndex >= currentPlayer.getHand().size()) {return false;}
        Card playedCard = currentPlayer.getHand().get(handIndex);

        if(gameState.getCardsPlayedThisTurn() == gameState.getMaxCardsToPlayPerTurn()) {return false;}
        if (!board.placeCard(currentPlayerID, boardIndex, currentPlayer.getHand().get(handIndex))){return false;}


        currentPlayer.getHand().remove(handIndex);
        currentPlayer.takeDamage(playedCard.getCardCost());

        playedCard.setAsleep(true);

        gameState.setCardsPlayedThisTurn(gameState.getCardsPlayedThisTurn() + 1);
        gameState.checkGameOver();

        if(currentPlayerID == PlayerID.PLAYER_TWO){
            guiManager.renderCard(Zone.OPPONENT_BOARD, boardIndex, playedCard.getImagePath());
        }else {
            guiManager.renderCard(Zone.PLAYER_BOARD, boardIndex, playedCard.getImagePath());
        }
        addMassageInGui(1, currentPlayer, playedCard, null);

        if(gameState.isGameOver()){
            gameOver();
            return true;
        }
        guiManager.changePlayerHP();
        return true;
    }

    /**
     * Metoden hanterar flytten av ett kort från spelarens hand till spelbrädet. Metoden kontrollerar valda index,
     * uppdaterar spelmodellen genom att kalla metoden placeCard som ändrar modellernas information.
     * Efteråt anropar sedan GUIManager för att uppdatera det visuella resultatet.
     * Sen återställs input-status för nästa drag.
     *
     * @author Erik, Jim Ström, Elna
     */
    public void moveCardFromHandtoBoard() {

        Player currentPlayer = gameState.getCurrentPlayer();

        if (!cardPicked || !spotPicked) {
            return;
        }

        // NYTT för MULTIPLAYER
        // I multiplayer skickar vi bara handlingen till servern.
        // Servern validerar, kör logiken, och broadcastar nya spelläget tillbaka.
        if (gameClient != null) {
            if (indexCardOnHandToMove >= 0 && indexCardOnHandToMove < playerOne.getHand().size()) {
                int cardId = playerOne.getHand().get(indexCardOnHandToMove).getCardID();
                gameClient.playCard(cardId, indexSpotToPlaceCard);
            }
            resetPlacementState();
            return; // Viktgit här är kör INTE singleplayer logiken nedanför
        }
        // SLUT NYTT för multiplayer

        if (currentPlayer.getHand().size() <= indexCardOnHandToMove) {
            resetPlacementState();
            return;
        }

        Card cardMoved = currentPlayer.getHand().get(indexCardOnHandToMove);

        if(!placeCard(indexCardOnHandToMove, indexSpotToPlaceCard)){
            resetPlacementState();
            return;
        }
        guiManager.renderCard(Zone.HAND,2,null);

        //guiManager.renderHand(currentPlayer.getHand());

        for(int i = 0; i < playerOne.getHand().size(); i++){
            guiManager.renderCard(Zone.HAND,i,playerOne.getHand().get(i).getImagePath());
        }

        guiManager.renderCard(Zone.PLAYER_BOARD, indexSpotToPlaceCard, cardMoved.getImagePath());

        resetPlacementState();
    }

    /**
     * Vad som ska hända när knappen EndTurn klickas, jag har lagt till en extra GamePhase.
     * Detta är så att om en spelare börjar spam klicka eller försöka attackera precis efter de klickat EndTurn.
     * Så kommer de andra checks (som kollar vilken "Phase" det är) stoppa dem från att göra det tills endTurn är klar.
     * Har skapat en ytterligare metod, "wakeUpCardsForPlayer" som väcker korten av den spelare som klickat endTurn.
     *
     * @author Jim, Erik
     */
    public void endTurnSinglePLayer(){
        // Nytt för MULTIPLAYER gren
        // I multiplayer skickar vi END_TURN till servern.
        // Servern växlar tur, drar kort åt nästa spelare, och broadcastar nya spelläget.
        if (gameClient != null) {
            gameClient.endTurn();
            gameState.setPhase(GamePhase.END_TURN); // blockera lokala handlingar tills servern svarar
            return; // Viktigt här, kör INTE singleplayer logiken (AI osv)
        }
        // SLUT NYTT för multiplayer

        gameState.setPhase(GamePhase.END_TURN);
        Player currentPlayer = gameState.getCurrentPlayer();
        PlayerID currentPlayerID = gameState.getCurrentPlayerId();

        currentPlayer.drawUntilHandIsFull();
        board.wakeUpCardsForPlayer(currentPlayerID);
        board.resetAttacksForPlayer(currentPlayerID);

        for(int i = 0; i < playerOne.getHand().size(); i++){
            guiManager.renderCard(Zone.HAND,i,playerOne.getHand().get(i).getImagePath());
        }

        gameState.switchTurn();

        gameState.setPhase(GamePhase.PLAY);

        addMassageInGui(3, currentPlayer, null, null);
    }


    /**
     *
     * @param attackerIndex
     * @param defenderIndex
     * @return
     */
    public boolean attackCard(int attackerIndex, int defenderIndex) {
        // Nytt för MULTIPLAYER gren
        // I multiplayer skickar vi attacken till servern, som kör logiken och broadcastar resultatet.
        if (gameClient != null) {
            gameClient.attackCard(attackerIndex, defenderIndex);
            return true;
        }
        // Slut på nytt

        if (gameState.getPhase() != GamePhase.PLAY) return false;

        PlayerID attackerPlayerID = gameState.getCurrentPlayerId();
        Player attackerPlayer = gameState.getCurrentPlayer();


        PlayerID defenderPlayerID = attackerPlayerID == PlayerID.PLAYER_ONE ? PlayerID.PLAYER_TWO : PlayerID.PLAYER_ONE;

        Player defenderPlayer = gameState.getOpponentPlayer();

        System.out.println("Innan index checkarna");
        if (attackerIndex < 0 || attackerIndex >= board.getSlotsForPlayer(attackerPlayerID).length) return false;
        if (defenderIndex < 0 || defenderIndex >= board.getSlotsForPlayer(defenderPlayerID).length) return false;

        Card attacker = board.getCard(attackerPlayerID, attackerIndex);
        Card defender = board.getCard(defenderPlayerID, defenderIndex);

        System.out.println("Innan null checkarna");
        if (attacker == null) return false;
        if (defender == null) return false;

        System.out.println("Innan Asleep checken");
        if (attacker.getAsleep()) return false;
        System.out.println("Innan HasAttacked This turn checken");
        if (attacker.getHasAttackedThisTurn()) return false;

        System.out.println("Defender HP before: " + defender.getCardCurrentHP());
        System.out.println("Attacker HP before: " + attacker.getCardCurrentHP());

        defender.takeDamage(attacker.getCardAD());
        attacker.takeDamage(defender.getCardAD());

        attacker.setHasAttackedThisTurn(true);

        addMassageInGui(2, attackerPlayer, attacker, defender);
        guiManager.renderCard(getBoardZone(defenderPlayerID),defenderIndex,defender.getImagePath());
        guiManager.renderCard(getBoardZone(attackerPlayerID),attackerIndex,attacker.getImagePath());

        if (defender.isDead()) {
            Card deadCard = board.removeCard(defenderPlayerID, defenderIndex);
            defenderPlayer.sendCardToGraveyard(deadCard);
            guiManager.renderCard(getBoardZone(defenderPlayerID),defenderIndex,null);

        }

        if (attacker.isDead()) {
            Card deadCard = board.removeCard(attackerPlayerID, attackerIndex);
            attackerPlayer.sendCardToGraveyard(deadCard);
            guiManager.renderCard(getBoardZone(attackerPlayerID),attackerIndex,null);
        }

        gameState.checkGameOver();

        if (gameState.isGameOver()) {
            gameOver();
            return true;

        }

        System.out.println("Defender HP after: " + defender.getCardCurrentHP());
        System.out.println("Attacker HP after: " + attacker.getCardCurrentHP());
        System.out.println("attack färdig");
        return true;
    }

    public boolean attackPlayer(int attackCard){
        // Nytt för MULTIPLAYER gren
        // I multiplayer skickar vi den direkta attacken till servern.
        if (gameClient != null) {
            gameClient.attackPlayer(attackCard);
            return true;
        }
        // SLUT på Nytt här

        if (gameState.getPhase() != GamePhase.PLAY) return false;

        PlayerID attackerPlayerID = gameState.getCurrentPlayerId();
        Player defenderPlayer = gameState.getOpponentPlayer();

        if (defenderPlayer == null) return false;
        if (attackCard < 0 || attackCard >= board.getSlotsForPlayer(attackerPlayerID).length) return false;

        Card attacker = board.getCard(attackerPlayerID, attackCard);

        if (attacker == null) return false;
        if (attacker.getAsleep()) return false;
        if (attacker.getHasAttackedThisTurn()) return false;

        defenderPlayer.takeDamage(attacker.getCardAD());
        attacker.setHasAttackedThisTurn(true);
        guiManager.changePlayerHP();

        gameState.checkGameOver();


        addMassageInGui(4, defenderPlayer, attacker, null);
        if (gameState.isGameOver()) {
            gameOver();
            return true;
        }
        return true;
    }


    /**
     * Vad som ska hända när knappen EndTurn klickas, jag har lagt till en extra GamePhase.
     * Detta är så att om en spelare börjar spam klicka eller försöka attackera precis efter de klickat EndTurn.
     * Så kommer de andra checks (som kollar vilken "Phase" det är) stoppa dem från att göra det tills endTurn är klar.
     * Har skapat en ytterligare metod, "wakeUpCardsForPlayer" som väcker korten av den spelare som klickat endTurn.
     *
     * @author Jim, Erik
     */
    public void endTurn(){
        // MULTIPLAYER GREN
        // I multiplayer skickar vi END_TURN till servern istället för att köra logiken lokalt.
        // Servern växlar tur, drar kort, och broadcastar nya spelläget tillbaka.
        if (gameClient != null) {
            gameClient.endTurn();
            gameState.setPhase(GamePhase.END_TURN); // blockera lokala handlingar tills servern svarar
            return; // VIKTIGT, kör INTE singleplayer logiken (AI etc) nedanför för fryser spelet
        }
        // SLUT MULTIPLAYER GREN

        gameState.setPhase(GamePhase.END_TURN);
        System.out.println("player1 hp: " + playerOne.getHp() + ", player2 hp: " + playerTwo.getHp());
        Player currentPlayer = gameState.getCurrentPlayer();
        PlayerID currentPlayerID = gameState.getCurrentPlayerId();

        currentPlayer.drawUntilHandIsFull();
        board.wakeUpCardsForPlayer(currentPlayerID);
        board.resetAttacksForPlayer(currentPlayerID);
        gameState.setCardsPlayedThisTurn(0);

        for(int i = 0; i < playerOne.getHand().size(); i++){
            guiManager.renderCard(Zone.HAND,i,playerOne.getHand().get(i).getImagePath());
        }
        addMassageInGui(3, currentPlayer, null, null);
        gameState.switchTurn();

        gameState.setPhase(GamePhase.PLAY);
        if (gameState.getCurrentPlayerId() == PlayerID.PLAYER_TWO) {
            enemyAI.takeTurn();
        }
        guiManager.displayTurnRound();
    }

    /**
     * Skapar nya objekt för nästa spel som användaren kan försöka gå in i.
     * Skiftar även menyn från spel menyn till main menyn.
     * Det är även här kopplingen mellan spelarna bryts.
     *
     * @author Jim
     */
    public void gameOver(){
        // BORDE FINNAS NÅGOT SOM TAR OSS TILL MAIN MENYN HÄR.
        playerOne = new Player("Player 1");
        playerTwo = new Player("Player 2");
        board = new Board();
        gameState = new GameState(playerOne, playerTwo, board);

        guiManager.switchToGameOverMenu();

        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");
        System.out.println("THE GAME HAS ENDED!");

        // TODO.. Här ska det fixas game over, mest troligen blir det bara att gameState resettas samt GUI:n
        // Om jag inte hunnit och ni redan kollar på detta, så kan ni göra en GUI metod som bara resettar allt.
        // Och sedan kalla den här inne, så ska jag fixa att uppdaterra klasserna och all den delen strax.

        //guiManager.switchToGameOverScreen(); //visar just nu endast ett tumt fönster som inte säger ngt mer än gamover.
        // gameover metoden borde beräkna resultat av matchen och sedan visa det i giut via guimanager.
    }

    /**
     * Binder kort-objektet till motsvarande ImageView i gui.
     * Kopplar varje kort i allCards till en visuell representation i guit.
     *
     * @param cardImageView - lista av ImageView som representerar kort i gui
     * @author Erik
     */
    public void bindCardsToView(ArrayList<ImageView> cardImageView) {
        for (int i = 0; i < allCards.length; i++) {
            bind(cardImageView.get(i), allCards[i]);
        }
    }

    /**
     * Kopplar ett enskilt kort till en ImageView genom att lagra kortet i view:ns userData.
     *
     * @param view - bild-"ramen".
     * @param card - objektet som ska kopplas.
     * @author Erik
     */
    private void bind(ImageView view, Card card) {
        view.setUserData(card);
    }


    /**
     * Metod för att lägga till valt kort i motståndarens hand.
     * Delen med "NULL CARD" är för att kolla om det finns ett kort eller inte i bildramen.
     * Detta syns när man spelat en runda, trycker exitGame, sen försöker spela en runda till.
     * Vi måste lösa så att spelet återställs vid exit-game.
     *
     //* @param card - kort-objektet
     * @author Erik
     */

    public PlayerID getCurrentPlayerId(){
        return gameState.getCurrentPlayerId();
    }


    /**
     * Metod för att lägga in ett meddelande i eventloggen i gameboard.
     * Anropas efter varje endTurn änsålänge men borde anropas såfort något har hänt, t.ex attack, kortplacering etc.
     *
     * @author Erik, Jim
     */
    public void addMassageInGui(int eventNumber, Player player, Card firstCard, Card secondCard){
        // guiManager.addMessageToEventLog(message);
        // Finns ingen metod som lägger till meddelandet till Event log. Metoden ovan funkar ej.
        switch (eventNumber){
            case 1:
                // Om någon placerar ett kort
                guiManager.sendMessageToEventLog(player.getName() + " has placed down " + firstCard.getCardName());
                guiManager.sendMessageToEventLog("___________________________");
                break;
            case 2:
                // Om någon attackerat ett kort.
                guiManager.sendMessageToEventLog(firstCard.getCardName() + " has attacked " + secondCard.getCardName() + " for " + firstCard.getCardAD());
                guiManager.sendMessageToEventLog("___________________________");
                if (firstCard.isDead()){
                    guiManager.sendMessageToEventLog(firstCard.getCardName() + " has died in battle fighting " + secondCard.getCardName());
                    guiManager.sendMessageToEventLog("___________________________");
                }
                if (secondCard.isDead()){
                    guiManager.sendMessageToEventLog(secondCard.getCardName() + " has died in battle fighting " + secondCard.getCardName());
                    guiManager.sendMessageToEventLog("___________________________");
                }
                break;
            case 3:
                // Om någon har avslutat sin tur.
                guiManager.sendMessageToEventLog(player.getName() + " has ended their turn!");
                guiManager.sendMessageToEventLog("___________________________");
                break;

            case 4:
                // Om någon direkt attackerat en spelare.
                guiManager.sendMessageToEventLog(firstCard.getCardName() + " has attacked " + player.getName() + " straight to the face for " + firstCard.getCardAD() + " damage!");
                guiManager.sendMessageToEventLog("___________________________");
                break;
        }
    }

    /**
     * getter för att hämta alla korten på sin sida av spelbrädan.
     * @param player
     * @return ArrayList av kort objekt
     * @author Erik
     */
    public ArrayList<Card> getCardsOnSide(PlayerID player) {
        Card[] slots = board.getSlotsForPlayer(player);
        ArrayList<Card> result = new ArrayList<>();

        for (Card c : slots) {
            if (c != null) {
                result.add(c);
            }
        }

        return result;
    }


    /**
     * Ansluter spelaren till spelservern via nätverket.
     * Skapar en GameClient, registrerar GameController som lyssnare
     * för nätverkshändelser och skickar ett JOIN meddelande till servern.
     *
     * @param playerName spelarens namn som används för identifiering på servern
     * @throws Exception om anslutningen till servern misslyckas
     * @author Leo
     */
    public void connectToServer(String playerName) throws Exception {
        gameClient = new GameClient("localhost", 5555);
        gameClient.setListener(this);
        gameClient.connect(playerName, playerName);
    }

    /**
     * Anropas av nätverket när spelaren väntar på motståndaren.
     * Körs via Platform.runLater() för att säkert uppdatera JavaFX tråden.
     * @author Leo
     */
    @Override
    public void onWaiting() {
        Platform.runLater(() -> guiManager.showWaiting());
    }

    /**
     * Anropas av nätverket när det är spelarens tur.
     * Aktiverar spelarens knappar i GUI via GUIManager.
     * @author Leo
     */
    @Override
    public void onYourTurn() {
        Platform.runLater(() -> guiManager.enableCardButtons());
    }

    /**
     * Anropas av nätverket när spelläget uppdaterats.
     * Parsar Json till gamestate, sunkar lokal model, och anropar
     * specifika gui metoder med färdig data.
     *
     * @param j det uppdaterade spelläget som JSON sträng
     * @author Leo
     */
    @Override
    public void onGameStateUpdate(String j) {
        Platform.runLater(() -> {
            // 1. Parsa JSON till GameState objekt
            try {
            GameState serverState = new com.google.gson.Gson().fromJson(j, GameState.class);
            // 2. Synca lokal hand och bräde mot servern (kritiskt här)
            syncLocalHandFromServer(serverState);
            updateGuiFromServerState(serverState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    }

    /**
     * Bryter ner serverns spelläge i specifika delar och anropar rätt GUI metod för varje.
     * Detekterar också fas övergång från DRAFT till PLAY och byter scen automatiskt.
     *
     * @param state det auktoritativa spelläget från servern
     * @author Leo
     */
    private void updateGuiFromServerState(GameState state) {
        if (state == null || localPlayerRole == null) return;

        // AUTOMATISKT SCENBYTE
        // Om vi precis växlade från DRAFT till PLAY då byt till GameBoard
        if (lastKnownPhase == GamePhase.DRAFT && state.getPhase() == GamePhase.PLAY) {
            guiManager.switchToGameBoard();
            // Efter scenbytet är guiManager en NY instans, sätt rollen igen
            // (switchToGameBoard skapar ny GUIManager som startar med default roll)
            guiManager.setLocalRole(localPlayerRole);
        }
        lastKnownPhase = state.getPhase();
        // SLUT SCENBYTE

        // DRAFT fas: skicka över id för kvarvarande kort i poolen
        if (state.getPhase() == GamePhase.DRAFT) {
            java.util.HashSet<Integer> remainingIds = new java.util.HashSet<>();
            if (state.getDraftPool() != null) {
                for (Card c : state.getDraftPool()) {
                    remainingIds.add(c.getCardID());
                }
            }
            guiManager.updateDraftPool(remainingIds);
            return;
        }

        // PLAY fas: rita bräde och hand
        if (state.getPhase() != GamePhase.PLAY || state.getBoard() == null) return;

        Card[] mySlots = state.getBoard().getSlotsForPlayer(localPlayerRole);
        PlayerID opponentRole;
        if (localPlayerRole == PlayerID.PLAYER_ONE) {
            opponentRole = PlayerID.PLAYER_TWO;
        } else {
            opponentRole = PlayerID.PLAYER_ONE;
        }
        Card[] opponentSlots = state.getBoard().getSlotsForPlayer(opponentRole);

        ArrayList<String> mySlotsPaths = new ArrayList<>();
        for (Card c : mySlots) {
            if (c != null) {
                mySlotsPaths.add(c.getImagePath());
            } else {
                mySlotsPaths.add(null);
            }
        }

        ArrayList<String> opponentSlotsPaths = new ArrayList<>();
        for (Card c : opponentSlots) {
            if (c != null) {
                opponentSlotsPaths.add(c.getImagePath());
            } else {
                opponentSlotsPaths.add(null);
            }
        }

        Player myPlayer;
        if (localPlayerRole == PlayerID.PLAYER_ONE) {
            myPlayer = state.getPlayerOne();
        } else {
            myPlayer = state.getPlayerTwo();
        }

        ArrayList<String> handPaths = new ArrayList<>();
        for (Card c : myPlayer.getHand()) {
            handPaths.add(c.getImagePath());
        }

        guiManager.updateMyHand(handPaths);
        guiManager.updateMyBoard(mySlotsPaths);
        guiManager.updateOpponentBoard(opponentSlotsPaths);
    }

    /**
     * Anropas av nätverket när spelet är slut.
     * Vidarebefordrar vinnarens namn till GUIManager.
     *
     * @param winner namnet på spelaren som vann
     * @author Leo
     */
    @Override
    public void onGameOver(String winner) {
        Platform.runLater(() -> guiManager.showGameOver(winner));
    }

    /**
     * Anropas av nätverket när ett felmeddelande tas emot.
     *
     * @param msg felmeddelandet från servern
     * @author Leo
     */
    @Override
    public void onError(String msg)         {
        Platform.runLater(() -> guiManager.showError(msg));
    }

    /**
     * Anropas av nätverket när ett chattmeddelande tas emot.
     *
     * @param msg chattmeddelandets text
     * @author Leo
     */
    @Override
    public void onChat(String msg) {
        Platform.runLater(() -> guiManager.showChat(msg));
    }

    /**
     * Kollar om spelet körs i multiplayer läge.
     * Används av GUIManager för att välja rätt beteende vid kortval och endTurn.
     *
     * @return true om en GameClient är ansluten cilket betyder = multiplayer
     * @author Leo
     */
    public boolean isMultiplayer() {
        return gameClient != null;
    }

    /**
     * Skickar ett DRAFT_PICK meddelande till servern med valt kortId.
     * Anropas av GUIManager när spelaren klickar på ett kort i PickCardScreen.
     *
     * @param cardId id på det valda kortet
     * @author Leo
     */
    public void sendDraftPick(int cardId) {
        if (gameClient != null) {
            gameClient.draftPick(cardId);
        }
    }

    /**
     * Synkar den lokala handen mot serverns auktoritativa spelläge.
     * KRITISK metod efterom att servern drar kort och tar bort spelade kort,
     * så den lokala handen måste spegla det. Utan denna är den lokala
     * handen alltid tom och placeCard/moveCardFromHandtoBoard misslyckas,
     * vilket förstör spelet.
     *
     * @param serverState det deserialiserade spelläget från servern
     * @author Leo
     */
    private void syncLocalHandFromServer(GameState serverState) {
        if (localPlayerRole == null || serverState == null) return;

        // Hämta vår hand från serverns spelläge baserat på vår roll
        ArrayList<Card> serverHand = (localPlayerRole == PlayerID.PLAYER_ONE)
                ? serverState.getPlayerOne().getHand()
                : serverState.getPlayerTwo().getHand();

        // Ersätt lokala handen helt med serverns version
        playerOne.getHand().clear();
        if (serverHand != null) {
            playerOne.getHand().addAll(serverHand);
        }

        // Synca även brädet och spelinfo
        if (serverState.getBoard() != null) {
            board = serverState.getBoard();
            gameState.setBoard(board);
        }
        gameState.setCardsPlayedThisTurn(serverState.getCardsPlayedThisTurn());

        // KRITISKT för fryser annars, synca currentPlayer så isLocalPlayersTurn() ger rätt svar
        if (serverState.getCurrentPlayerId() != null) {
            gameState.setCurrentPlayer(serverState.getCurrentPlayerId());
        }

        // Synca fasen, viktig för placeCard checken som kräver PLAY
        if (serverState.getPhase() == GamePhase.PLAY) {
            gameState.setPhase(GamePhase.PLAY);
        }
    }
    @Override
    public void onGameStart(String role) {
        if(role.equals("PLAYER_ONE")){
            localPlayerRole = PlayerID.PLAYER_ONE;
        } else {
            localPlayerRole = PlayerID.PLAYER_TWO;
        }
        // Servern berättar vilken roll vi har, "PLAYER_ONE" eller "PLAYER_TWO"
        // Spara det och skicka vidare till GUI så den vet vilken sida av brädet som är "min"
        localPlayerRole = PlayerID.valueOf(role);
        Platform.runLater(() -> guiManager.setLocalRole(localPlayerRole));
        System.out.println("(CTRL) onGameStart, role=" + role + ", localPlayerRole=" + localPlayerRole);
    }

    @Override
    public void onDraftTurn() {
        // Servern säger att det är vår tur att välja kort i draft fasen
        // Säg till GUIManager att aktivera kortval (sätter isDraftTurn = true där)
        Platform.runLater(() -> guiManager.enableDraftPicking());
    }

    //Borde tas bort!
    public void set(){
        gameState.setPhase(GamePhase.PLAY);
    }

    public GameState getGameState(){
        return gameState;
    }
    public void resetAttackState() {
        System.out.println("RESET ATTACK STATE CALLED");

        indexToCardToAttackWith = -1;
        indexToCardToAttack = -1;

        attackTargetPicked = false;

        attackerPicked = false;
        defenderPicked = false;
    }

    public void resetPlacementState() {

        System.out.println("RESET PLACEMENT STATE CALLED");

        indexCardOnHandToMove = -1;
        indexSpotToPlaceCard = -1;

        cardPicked = false;
        spotPicked = false;
    }

    public void resetAllSelectionStates() {
        resetPlacementState();
        resetAttackState();
    }

    public boolean isCardPicked() {
        return cardPicked;
    }

    public boolean isAttackerPicked() {
        return attackerPicked;
    }

    public boolean isDefenderPicked() {
        return defenderPicked;
    }




    /**
     * Sätter index för vilket kort i handen som ska flyttas.
     *
     * @param index - positionen i spelarens hand
     * @author Elna
     */
    public void setIndexCardOnHandToMove(int index){
        indexCardOnHandToMove = index;
        cardPicked = true;
    }

    /**
     * Sätter index för vilken plats på brädet kortet ska placeras på.
     * Triggar sedan flytt av kort från hand till bräde.
     *
     * @param index - position på spelbrädet
     * @author Erik
     */
    public void setIndexSpotToPlaceCard(int index){
        indexSpotToPlaceCard = index;
        spotPicked = true;
        moveCardFromHandtoBoard();
    }

    /**
     * Metod för att sätta index för det kortet man vill attacckera med
     * @param index
     * @author Erik
     */
    public void setIndexOfCardOnMyBoardToAttackWith(int index){
        indexToCardToAttackWith = index;
        attackerPicked = true;
    }

    public void setIndexToCardToAttack(int index){
        indexToCardToAttack = index;
        defenderPicked = true;
    }

    public int getIndexToCardToAttackWith(){
        return indexToCardToAttackWith;
    }

    /**
     * Sätter guiManager instans.
     *
     * @param guiManager -
     * @author Erik
     */
    public void setGuiManager(GUIManager guiManager){
        this.guiManager=guiManager;
    }

    /**
     * Ger en zone beroende på vilket spelarID det är.
     * @param playerID - SpelarID:et
     * @return - Zonen som är kopplad till spelarID:et.
     * @author Jim Ström
     */
    private Zone getBoardZone(PlayerID playerID) {
        if (playerID == PlayerID.PLAYER_ONE) {
            return Zone.PLAYER_BOARD;
        } else {
            return Zone.OPPONENT_BOARD;
        }
    }

    public int getHPforCardHand(int index){
        return playerOne.getHand().get(index).getCardCurrentHP();
    }

    public int getHPforCardBoard(int index, int whatPlayer){
        // whatPlayer 1 = "min" sida, 2 = "motståndarens" sida
        // Mappa via localPlayerRole istället för hårdkodat PLAYER_ONE/TWO så att HP speglas och inte kodat för position
        PlayerID myRole = (localPlayerRole != null) ? localPlayerRole : PlayerID.PLAYER_ONE;
        PlayerID opponentRole = (myRole == PlayerID.PLAYER_ONE) ? PlayerID.PLAYER_TWO : PlayerID.PLAYER_ONE;

        PlayerID target = (whatPlayer == 1) ? myRole : opponentRole;
        Card card = board.getCard(target, index);
        if (card == null) return 0;
        return card.getCardCurrentHP();
    }

    private void setSinglePLayer(boolean singlePLayer) {
        this.SinglePlayer=singlePLayer;
    }
    private boolean getSinglePlayer(){
        return SinglePlayer;
    }

    /**
     * Getter för playerHP. Används av GUI för att displaya player ID på spelplan
     * @param player Om spelare ett eller två ska hämtas
     * @return en integer med spelarens HP
     * @author Elna N.
     */
    public int getPlayerHP(int player){
        if(player == 1){
           return playerOne.getHp();
        } else{
            return playerTwo.getHp();
        }
    }

}
