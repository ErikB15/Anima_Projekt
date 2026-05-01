package Controller;

import java.util.ArrayList;
import java.util.Collections;

import Model.*;
import View.*;
import javafx.scene.image.ImageView;


/**
 * Game Controller klassen, syftet är att kontroller flödet av information från view och Model under matchens gång.
 * När matchens avslutas stängs även game controllern, då den endast är nödvändig när matchen är aktiv.
 */
public class GameController {
    private ArrayList<Card> allCards;
    private ArrayList<Effect> allEffects;

    //Mekanik för att byta plats på kort i playerOneActiveCards
    private int indexCardOnHandToMove;
    private int indexSpotToPlaceCard;
    private boolean cardPicked = false;
    private boolean spotPicked = false;

    private Player playerOne;
    private Player playerTwo;
    private Board board;
    private GUIManager guiManager;
    private Card testCard; //ENDAST FÖR TESTNING
    private GameState gameState;

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
        playerOne = new Player();
        playerTwo = new Player();
        board = new Board();
        gameState = new GameState(playerOne, playerTwo, board);
        addAllCards();
    }


    /**
     * Lägger till alla kort som finns i spelet genom hårdkodad initiering.
     * Skapar Card-objekt och lägger dem i allCards-listan.
     *
     * @author Erik
     */
    public void addAllCards(){
        // Ett exempel på hur ett kort kommer att hårdkodas, kommer bli en långgg parameter lista dock.
        allCards.add(new Card("Test1", 1,50,1,null, "/CardFRONT.png"));
        allCards.add(new Card("Test2", 5,25,2,null, "/CardFRONT.png"));
        allCards.add(new Card("Test3", 13,34,3,null, "/CardFRONT.png"));
        allCards.add(new Card("Test4", 30,20,4,null, "/CardFRONT.png"));
        allCards.add(new Card("Test5", 10,30,5,null, "/CardFRONT.png"));
        allCards.add(new Card("Test6", 2,40,6,null, "/CardFRONT.png"));
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
        //if(gameState.getPhase() != GamePhase.PLAY){return false;}
        // Avkommenoterad för att kunna testa olika grejer, kommer finnas när allting puzzlat samman.

        Player currentPlayer = gameState.getCurrentPlayer();
        PlayerID currentPlayerID = gameState.getCurrentPlayerId();

        Card playedCard = currentPlayer.getHand().get(handIndex);

        if (!board.placeCard(currentPlayerID, boardIndex, currentPlayer.getHand().get(handIndex))){return false;}
        if(gameState.getCardsPlayedThisTurn() == gameState.getMaxCardsToPlayPerTurn()) {return false;}

        currentPlayer.getHand().remove(handIndex);
        currentPlayer.takeDamage(playedCard.getCardCost());

        playedCard.setAsleep(true);

        gameState.setCardsPlayedThisTurn(gameState.getCardsPlayedThisTurn() + 1);
        gameState.checkGameOver();

        if(gameState.isGameOver()){
            gameOver();
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
        System.out.println("Current player: " + gameState.getCurrentPlayerId()); // Debug, var nödvändig.

        if (!cardPicked || !spotPicked) return;

        if (currentPlayer.getHand().size() <= indexCardOnHandToMove) return;

        Card cardMoved = currentPlayer.getHand().get(indexCardOnHandToMove);

        // Det är här jag slängt in denna metoden. Istället för att sätta den på board här direkt.
        if(!placeCard(indexCardOnHandToMove, indexSpotToPlaceCard)){return;}
        // Själva metoden kommer skicka tillbaka om det inte gått, och då går det ännu längre bak.

        guiManager.renderHand(currentPlayer.getHand());
        guiManager.renderCard(Zone.PLAYER_BOARD, indexSpotToPlaceCard, cardMoved.getImagePath());

        cardPicked = false;
        spotPicked = false;
    }

    /**
     * Vad som ska hända när knappen EndTurn klickas, jag har lagt till en extra GamePhase.
     * Detta är så att om en spelare börjar spam klicka eller försöka attackera precis efter de klickat EndTurn.
     * Så kommer de andra checks (som kollar vilken "Phase" det är) stoppa dem från att göra det tills endTurn är klar.
     * Har skapat en ytterligare metod, "wakeUpCardsForPlayer" som väcker korten av den spelare som klickat endTurn.
     *
     * OBS, behövs callback eller GUI uppdateringen här igen mot slutet!
     * @author Jim Ström
     */
    public void endTurn(){
        gameState.setPhase(GamePhase.END_TURN);

        Player currentPlayer = gameState.getCurrentPlayer();
        PlayerID currentPlayerID = gameState.getCurrentPlayerId();

        currentPlayer.drawUntilHandIsFull();
        board.wakeUpCardsForPlayer(currentPlayerID);

        gameState.switchTurn();
        gameState.setPhase(GamePhase.PLAY);
    }


    public void gameOver(){

        // TODO.. Här ska det fixas game over, mest troligen blir det bara att gameState resettas samt GUI:n
        // Om jag inte hunnit och ni redan kollar på detta, så kan ni göra en GUI metod som bara resettar allt.
        // Och sedan kalla den här inne, så ska jag fixa att uppdaterra klasserna och all den delen strax.
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
     * Radera? Tänker att vi inte ska ha event listeners i controller -Elna
     *
     * @param event
     * @author
     */
    //public void handleCardClick(MouseEvent event) {
    //    ImageView clicked = (ImageView) event.getSource();
    //    Card card = (Card) clicked.getUserData();
    //    pickCard(card);
    //}

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
     * IGNORERA DETTA, JAG SKREV LOGIK FÖR ATTACK I BOARD KLASSEN.
     * INSÅG ATT DET ÄR BÄTTRE ATT HA DEN I CONTROLLERN DÅ DEN RÖR FLERA OBJEKT SAMTIDIGT.
     * DET HÄR ÄR BARA KVAR SÅ JAG KAN ANVÄNDA DET SOM PROTOTYP FÖR NÄR JAG EVENTUELLT GÖR ATTACK
     * FUNKTIONEN HÄR INNE.
     *
     * public void attack(int attackingCard, int defendingCard){
     *         int defCardHP = playerTwoSlots[defendingCard].getCardCurrentHP();
     *         int atkCardHP = playerOneSlots[attackingCard].getCardCurrentHP();
     *         int defCardNewHP = (defCardHP - playerOneSlots[attackingCard].getCardAD());
     *         int atkCardNewHP = (atkCardHP - playerTwoSlots[defendingCard].getCardAD());
     *         playerTwoSlots[defendingCard].setCardCurrentHP(defCardNewHP);
     *         playerOneSlots[attackingCard].setCardCurrentHP(atkCardNewHP);
     *         playerOneSlots[attackingCard].setHasAttackedThisTurn(true);
     *
     *         if(defCardNewHP <= 0){
     *             playerTwo.sendCardToGraveyard(playerTwoSlots[defendingCard]);
     *             playerTwoSlots[defendingCard] = null;
     *         }
     *         if(atkCardNewHP <= 0){
     *             playerOne.sendCardToGraveyard(playerOneSlots[attackingCard]);
     *             playerOneSlots[attackingCard] = null;
     *         }
     *     }
     */

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
        System.out.println(card);
        System.out.println(card != null ? card.getImagePath() : "NULL CARD");

        playerOne.addCardToDeck(card);
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

        gameState.setCurrentPlayer(PlayerID.PLAYER_ONE); // Behövs för annars vet inte gameState vem det är.
        // Ska settas på ett annat ställe sen.
        guiManager.renderHand(playerOne.getHand());
    }
}
