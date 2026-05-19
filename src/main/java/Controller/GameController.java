package Controller;

import java.util.ArrayList;
import java.util.Collections;

import Model.*;
import Network.GameClient;
import Network.GameStateListener;
import Model.CardEffects.*;
import View.*;
import javafx.application.Platform;
import javafx.scene.image.ImageView;


/**
 * Game Controller klassen, syftet är att kontroller flödet av information från view och Model under matchens gång.
 * När matchens avslutas stängs även game controllern, då den endast är nödvändig när matchen är aktiv.
 */
public class GameController implements GameStateListener {
    private ArrayList<Card> allCards;
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
    private Board board;
    private PlayerID localPlayerRole;
    private GUIManager guiManager;
    private Card testCard; //ENDAST FÖR TESTNING
    private GameState gameState;
    private DubbelHit dubbelHit;
    private Taunt taunt;
    private Heal heal;
    private Shield shield;
    private Poison poison;
    private Buff buff;


    /**
     * Skapar en ny GameController och initierar spelets grunddata.
     * Initierar listor för alla kort och effekter samt skapar spelare och spelbräde.
     * Anropar addAllCards() för att fylla spelet med alla hårdkodade kort.
     *
     * @author Erik,Jim Ström, Elna
     */
    public GameController(){
        allCards = new ArrayList<>();
        allEffects = new ArrayList<>();
        playerOne = new Player("Player1");
        playerTwo = new Player("Player2"); //identifera spelare för servern såd e har ett namn
        board = new Board();
        gameState = new GameState(playerOne, playerTwo, board);
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
        // Ett exempel på hur ett kort kommer att hårdkodas, kommer bli en långgg parameter lista dock.
        allCards.add(new Card("Monkey", 40,50,1,taunt, "/CardPictures/Card5.png"));
        allCards.add(new Card("Bob", 40,25,2,buff, "/CardPictures/Card10.png"));
        allCards.add(new Card("Twins", 40,34,3,dubbelHit, "/CardPictures/Card8.png"));
        allCards.add(new Card("ChillGuy", 40,20,4,heal, "/CardPictures/Card9.png"));
        allCards.add(new Card("blockHead", 40,30,5,shield, "/CardPictures/Card7.png"));
        allCards.add(new Card("Wizard", 40,40,6,poison, "/CardPictures/Card6.png"));
        allCards.add(new Card("Pernilla", 40,40,6,poison, "/CardPictures/Card12.png"));
        allCards.add(new Card("Kick", 40,40,6,poison, "/CardPictures/Card11.png"));
        allCards.add(new Card("George", 40,40,6,poison, "/CardPictures/Card4.png"));
        allCards.add(new Card("Harrold", 40,40,6,poison, "/CardPictures/Card3.png"));
        allCards.add(new Card("KnifeGuy", 40,40,6,poison, "/CardPictures/Card2.png"));
        allCards.add(new Card("Kenneth", 40,40,6,poison, "/CardPictures/Card1.png"));
    }

    /**
     * Initierar spelbrädet.
     * Just nu tom metod som är avsedd för framtida uppsättning av spelbräde och UI-koppling.
     *
     * @author Jim Ström
     */
    public void setupBoard(){
        // Koppla spelare till de två olika "connections" vi gjort.
        // board = new Board();
        // gameState = new GameState(playerOne, playerTwo, board);
        // Kommer antagligen behöva göras sen när vi gör en connection istället.
        // Vi kommer behöva koppla player ett och player två till de två olika uppkopplingarna.
        // Och gameState ska bara skapas när vi gjort dessa grejer.
    }

    /**
     * Startas när vi går in i välja kort fasen.
     * Randomizer för att slumpmässigt välja vem som börjar välja.
     * Sen avslutas det med att sätta "GamePhase" till draft fasen.
     * @author Jim Ström
     */
    public void startDraftPhase(){
        int random = (int)(Math.random() * 2) + 1;
        if(random == 1){
            gameState.setFirstDraftPlayer(PlayerID.PLAYER_ONE);
            gameState.setCurrentDraftPlayer(PlayerID.PLAYER_ONE);
        }else {
            gameState.setFirstDraftPlayer(PlayerID.PLAYER_TWO);
            gameState.setCurrentDraftPlayer(PlayerID.PLAYER_TWO);
        }
        gameState.setPhase(GamePhase.DRAFT);
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
        if(gameState.getPhase() != GamePhase.DRAFT){return;}
        if(cardIndex < 0 || cardIndex >= allCards.size()){return;}

        Card chosenCard = allCards.get(cardIndex);
        gameState.getCurrentDraftPlayer().addCardToDeck(chosenCard);
        allCards.remove(chosenCard);

        if(allCards.isEmpty()){
            startPlayPhase();
            return;
        }

        gameState.switchDraftPlayer();
        // Ska finnas en metod eller callback för att uppdatera GUI:et
    }

    /**
     * Lik startDraftPhase, nu kollar vi på vilken spelare som började få kort i draft fasen.
     * Efteråt ser vi till att båda de kortlekarna spelarna fått blir blandande.
     * Sen drar båda spelarna tills deras hand är fylld.
     * Till sist sätter vi gameState till "Play".
     * @author Jim Ström
     */
    public void startPlayPhase(){
        if(gameState.getFirstDraftPlayer() == PlayerID.PLAYER_ONE){
            gameState.setCurrentPlayer(PlayerID.PLAYER_TWO);
        }else{
            gameState.setCurrentPlayer(PlayerID.PLAYER_ONE);
        }
        Collections.shuffle(playerOne.getDeck());
        Collections.shuffle(playerTwo.getDeck());
        playerOne.drawUntilHandIsFull();
        playerTwo.drawUntilHandIsFull();
        gameState.setPhase(GamePhase.PLAY);
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

        Card playedCard = currentPlayer.getHand().get(handIndex);

        if (handIndex < 0 || handIndex >= currentPlayer.getHand().size()) {return false;}
        if(gameState.getCardsPlayedThisTurn() == gameState.getMaxCardsToPlayPerTurn()) {return false;}
        if (!board.placeCard(currentPlayerID, boardIndex, currentPlayer.getHand().get(handIndex))){return false;}


        currentPlayer.getHand().remove(handIndex);
        currentPlayer.takeDamage(playedCard.getCardCost());

        playedCard.setAsleep(true);

        gameState.setCardsPlayedThisTurn(gameState.getCardsPlayedThisTurn() + 1);
        gameState.checkGameOver();

        addMassageInGui(1, currentPlayer, playedCard, null);

        if(gameState.isGameOver()){
            gameOver();
            return true;
        }

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

        System.out.println("Current player: " + gameState.getCurrentPlayerId());

        if (!cardPicked || !spotPicked) {
            return;
        }

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
            System.out.println();
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
        gameState.setPhase(GamePhase.END_TURN);
        System.out.println("player1 hp: " + playerOne.getHp() + ", player2 hp: " + playerTwo.getHp());
        Player currentPlayer = gameState.getCurrentPlayer();
        PlayerID currentPlayerID = gameState.getCurrentPlayerId();

        currentPlayer.drawUntilHandIsFull();
        board.wakeUpCardsForPlayer(currentPlayerID);
        board.resetAttacksForPlayer(currentPlayerID);

        //guiManager.renderHand(playerOne.getHand());

        for(int i = 0; i < playerOne.getHand().size(); i++){
            guiManager.renderCard(Zone.HAND,i,playerOne.getHand().get(i).getImagePath());
        }

        gameState.switchTurn();

        gameState.setPhase(GamePhase.PLAY);
        if (gameState.getCurrentPlayerId() == PlayerID.PLAYER_TWO) {
            enemyTurnInSinglePLayer();
        }
        addMassageInGui(3, currentPlayer, null, null);
    }


    /**
     *
     * @param attackerIndex
     * @param defenderIndex
     * @return
     */
    public boolean attackCard(int attackerIndex, int defenderIndex) {
        System.out.println("Innan gamestate Checken");
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

        gameState.checkGameOver();


        addMassageInGui(4, defenderPlayer, attacker, null);
        if (gameState.isGameOver()) {
            gameOver();
            return true;
        }
        return true;
    }

    /**
     * Metoden för att simulera single-player motståndarens omgång.
     * Samma metoder som när vi vill lägga kort men med en while-loop som kontrollerar att där motståndaren vill lägga kort är en gilltig plats.
     *
     * @author Erik, Jim
     */
    private void enemyTurnInSinglePLayer() {

        if (playerTwo.getHand().isEmpty()) {
            playerTwo.drawUntilHandIsFull();
        }

        if (playerTwo.getHand().isEmpty()) {
            return;
        }

        if (!board.hasEmptySlot(PlayerID.PLAYER_TWO)) {
            return;
        }

        int handIndex =
                (int) (Math.random() * playerTwo.getHand().size());

        Card card = playerTwo.getHand().get(handIndex);


        int boardIndex = 0;
        for(int i = 0; i < board.getSlotsForPlayer(PlayerID.PLAYER_TWO).length; i++){
            if (board.getSlotsForPlayer(PlayerID.PLAYER_TWO)[i] == null){
                board.placeCard(PlayerID.PLAYER_TWO, i, card);
                boardIndex = i;
                guiManager.renderCard(
                        Zone.OPPONENT_BOARD,
                        boardIndex,
                        card.getImagePath()
                );
                break;
            }
        }

        playerTwo.getHand().remove(handIndex);

        playerTwo.takeDamage(card.getCardCost());

        card.setAsleep(true);


        gameState.checkGameOver();

        if(gameState.isGameOver()) {
            gameOver();
            return;
        }
    }

    /**
     * Metod för att hantera end-turn och turbyte mellan spelare i mulitplayer.
     * Ska fungera likannde som för singleplayer med små modifikationer.
     *
     * @author Erik
     */
    public void endTurnMultiPLayer(){

        enemyTurnInMultiPlayer();
    }

    /**
     * Metoden för att starta multi-player motståndarens omgång.
     *
     * @author Erik
     */
    public void enemyTurnInMultiPlayer(){

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
        for (int i = 0; i < cardImageView.size(); i++) {
            bind(cardImageView.get(i), allCards.get(i));
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
     * Metod för att lägga till valt kort i spelarens hand.
     * Delen med "NULL CARD" är för att kolla om det finns ett kort eller inte i bildramen.
     * Detta syns när man spelat en runda, trycker exitGame, sen försöker spela en runda till.
     * Vi måste lösa så att spelet återställs vid exit-game.
     *
     * @param card - kort-objektet
     * @author Erik
     */
    public void addCardToPlayerOne(Card card){
        playerOne.addCardToDeck(card);
        allCards.remove(card);
    }

    /**
     * Metod för att lägga till valt kort i motståndarens hand.
     * Delen med "NULL CARD" är för att kolla om det finns ett kort eller inte i bildramen.
     * Detta syns när man spelat en runda, trycker exitGame, sen försöker spela en runda till.
     * Vi måste lösa så att spelet återställs vid exit-game.
     *
     * @param card - kort-objektet
     * @author Erik
     */
    public void addCardToOpponent(Card card){
        playerTwo.addCardToDeck(card);
        allCards.remove(card);
    }


    /**
     * Startar spelet genom att låta spelarna dra sina initiala händer och renderar spelarens hand i gui.
     *
     * @auther: Erik
     */
    public void startGame() {
        playerOne.drawUntilHandIsFull();
        playerTwo.drawUntilHandIsFull();

        guiManager.setYourTurn(true);
        gameState.setCurrentPlayer(PlayerID.PLAYER_ONE); // Behövs för annars vet inte gameState vem det är.
        // Ska settas på ett annat ställe sen.

        for(int i = 0; i < playerOne.getHand().size(); i++){
            guiManager.renderCard(Zone.HAND,i,playerOne.getHand().get(i).getImagePath());
        }
        //guiManager.renderHand(playerOne.getHand());
    }
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
                // För att få ett tomt utrymme under.
            case 2:
                // Om någon attackerat ett kort.

                guiManager.sendMessageToEventLog(firstCard.getCardName() + " has attacked " + secondCard.getCardName() + " for " + firstCard.getCardAD());
                guiManager.sendMessageToEventLog("___________________________");
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
     * Vidarebefordrar JSON strängen till GUIManager som ritar om brädet.
     *
     * @param j det uppdaterade spelläget som JSON sträng
     * @author Leo
     */
    @Override
    public void onGameStateUpdate(String j) {
        Platform.runLater(() -> guiManager.updateBoard(j));
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

    @Override
    public void onGameStart(String role) {

    }

    @Override
    public void onDraftTurn() {

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

        if(whatPlayer == 1){
            return board.getCard(PlayerID.PLAYER_ONE, index).getCardCurrentHP();
        } else{
            return board.getCard(PlayerID.PLAYER_TWO, index).getCardCurrentHP();
        }

    }
}
