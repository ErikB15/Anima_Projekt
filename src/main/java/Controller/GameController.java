package Controller;

import java.util.ArrayList;
import Model.*;
import View.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


/**
 * Game Controller klassen, syftet är att kontroller flödet av information från view och Model under matchens gång.
 * När matchens avslutas stängs även game controllern, då den endast är nödvändig när matchen är aktiv.
 */
public class GameController {
    private ArrayList<Card> allCards;
    private ArrayList<Effect> allEffects;

    //private ArrayList<Card> playerOneActiveCards;
    //private ArrayList<Card> playerOneCardPile;

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
     * @auther: Erik
     */
    public GameController(){
        allCards = new ArrayList<>();
        allEffects = new ArrayList<>();

        playerOne = new Player();
        playerTwo = new Player();
        board = new Board();

        addAllCards();
    }


    /**
     * Lägger till alla kort som finns i spelet genom hårdkodad initiering.
     * Skapar Card-objekt och lägger dem i allCards-listan.
     *
     * @auther: Erik
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
     * @auther:
     */
    public void setupBoard(){

    }
    /**
     * Hanterar kortvalsfasen där spelare turas om att välja kort från en gemensam kortpool.
     * Kort flyttas från allCards till respektive spelares deck.
     * Används inte i nuläget, kan bli relevant när vi ska göra varanan val.
     *
     * @auther:
     */
    public void chooseCardPhase(){
        int random = (int)(Math.random() * 2) + 1;
        while(!allCards.isEmpty()){
            if(random == 1){
                // Där bör finnas logik här för vilket kort spelaren väljer.
                // Svårt att implementera utan GUI:n dock.
                playerOne.addCardToDeck(testCard);
                allCards.remove(testCard);
                random = 2;
            }else{
                // Där bör finnas logik här för vilket kort spelaren väljer.
                // Svårt att implementera utan GUI:n dock.
                playerTwo.addCardToDeck(testCard);
                allCards.remove(testCard);
                random = 1;
            }
        }
    }

    /**
     * Binder kort-objektet till motsvarande ImageView i gui.
     * Kopplar varje kort i allCards till en visuell representation i guit.
     *
     * @Param: cardImageView - lista av ImageView som representerar kort i gui
     * @auther: Erik
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
     * @auther: Erik
     */
    private void bind(ImageView view, Card card) {
        view.setUserData(card);
    }

    /**
     * Sätter index för vilket kort i handen som ska flyttas.
     *
     * @Param: index - positionen i spelarens hand
     * @auther: Elna
     */
    public void setIndexCardOnHandToMove(int index){
        indexCardOnHandToMove = index;
        cardPicked = true;
    }

    /**
     * Sätter index för vilken plats på brädet kortet ska placeras på.
     * Triggar sedan flytt av kort från hand till bräde.
     *
     * @Param: index - position på spelbrädet
     * @auther: Erik
     */
    public void setIndexSpotToPlaceCard(int index){
        indexSpotToPlaceCard = index;
        spotPicked = true;
        moveCardFromHandtoBoard();
    }

    /**
     * Metoden hanterar flytten av ett kort från spelarens hand till spelbrädet. Metoden kontrollerar valda index,
     * uppdaterar spelmodellen genom att flytta kortet och ta bort det från handen, och anropar sedan GUIManager för att uppdatera det visuella resultatet.
     * Sen återställs input-status för nästa drag.
     *
     * @auther: Erik
      */
    public void moveCardFromHandtoBoard() {

        if (!cardPicked || !spotPicked) return;

        if (playerOne.getHand().size() <= indexCardOnHandToMove) return;

        Card cardMoved = playerOne.getHand().get(indexCardOnHandToMove);

        board.placeCard(PlayerID.PLAYER_ONE, indexSpotToPlaceCard, cardMoved);

        playerOne.getHand().remove(indexCardOnHandToMove);

        guiManager.renderHand(playerOne.getHand());
        guiManager.renderCard(Zone.PLAYER_BOARD, indexSpotToPlaceCard, cardMoved.getImagePath());

        cardPicked = false;
        spotPicked = false;
    }


    /**
     * Radera? tänker att vi inte ska ha event listeners i controller -Elna
     *
     * @param event
     * @auther:
     */
    public void handleCardClick(MouseEvent event) {
        ImageView clicked = (ImageView) event.getSource();
        Card card = (Card) clicked.getUserData();
        pickCard(card);
    }

    /**
     * Lägger valt kort i spelarens deck och tar bort det från den gemensamma kortlistan.
     * SideNote: check av pilesize är endast för att kontrollera att kortet faktiskt läggs till i deck. Inte nödvändingt men bra dubbelcheckning.
     *
     * @Param: card - kort som ska läggas till i spelarens deck
     * @auther: Erik
     */
    public void pickCard(Card card) {
        System.out.println("Pile size before: " + playerOne.getDeck().size());
        playerOne.addCardToDeck(card);
        System.out.println("Pile size after: " + playerOne.getDeck().size());
    }

    /**
     * Sätter guiManager instans.
     *
     * @param guiManager
     * @auther: Erik
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
     * @auther: Erik
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
        playerOne.drawInitialHand(3);
        playerTwo.drawInitialHand(3);

        guiManager.renderHand(playerOne.getHand());
    }
}
