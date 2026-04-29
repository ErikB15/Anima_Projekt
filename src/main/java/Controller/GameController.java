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

    /**
     * Constructor för Game Controllern.
     */
    public GameController(){
        allCards = new ArrayList<Card>();
        allEffects = new ArrayList<Effect>();
        addAllCards();

    }

    /**
     * Tanken med denna är det kommer vara här vi hård kodar in alla dem korten vi vill lägga till i vårt spel.
     * Det är även här vi ska skapa korten och lägga in bilderna osv för korten. (Osäker med bilderna)
     */
    public void addAllCards(){
        // Ett exempel på hur ett kort kommer att hårdkodas, kommer bli en långgg parameter lista dock.
        allCards.add(new Card("Test1", 1,50,1,null, "CardFRONT.png"));
        allCards.add(new Card("Test2", 5,25,2,null, "CardFRONT.png"));
        allCards.add(new Card("Test3", 13,34,3,null, "CardFRONT.png"));
        allCards.add(new Card("Test4", 30,20,4,null, "CardFRONT.png"));
        allCards.add(new Card("Test5", 10,30,5,null, "CardFRONT.png"));
        allCards.add(new Card("Test6", 2,40,6,null, "CardFRONT.png"));
    }

    /**
     * Detta ska initiera själva brädets skapande, dvs skapar GUI:et och gör så att båda spelarna får upp det.
     */
    public void setupBoard(){

    }

    /**
     * Metoden designerad för att låta en spelare välja kort. Kan även omdirigeras till MainMenuController.
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

    public void bindCardsToView(ArrayList<ImageView> cardImageView) {
        if (allCards == null || allCards.size() < cardImageView.size()) {
            throw new IllegalStateException("Not enough cards");
        }

        for (int i = 0; i < cardImageView.size(); i++) {
            bind(cardImageView.get(i), allCards.get(i));
        }
    }


    private void bind(ImageView view, Card card) {
        var url = getClass().getClassLoader().getResource(card.getImagePath());

        if (url == null) {
            throw new IllegalStateException("Resource not found: " + card.getImagePath());
        }

        //view.setImage(new Image(url.toExternalForm())); Onödig, tänkte jag behövde den. Kan tas bort men dubbelkolla första att allt funkar
        view.setUserData(card);
    }

    //Denna metod anropas och sätts när en spelare väljer ett kort att flytta
    public void setIndexCardOnHandToMove(int index){
        indexCardOnHandToMove = index;
        cardPicked = true;

    }

    //Denna metod anropas och sätts när en spelare väljer ett kort att flytta.
    // Denna metod anropar metoden som faktiskt flyttar korten.
    public void setIndexSpotToPlaceCard(int index){
        indexSpotToPlaceCard = index;
        spotPicked = true;
        moveCardFromHandtoBoard();
    }

    //Denna metod flyttar plats på ett kort i arrayen, från en spelares hand( index 0-2) ut på spelbrädet ( index 3-6)
    public void moveCardFromHandtoBoard(){

        if((cardPicked == true)  && (spotPicked == true)){

           if((playerOne.getHand().get(indexCardOnHandToMove)  != null) && (playerOne.getHand().get(indexSpotToPlaceCard) == null)){

               /* Card cardMoved = playerOne.getHand().get(indexCardOnHandToMove);
               board.getPlayerOneSlots()
               playerOneActiveCards.add(indexSpotToPlaceCard, cardMoved);
               playerOneActiveCards.remove(indexCardOnHandToMove);*/
           } else{
               //guiManager.sendMessageThroughGUI("Unable to move card");
           }

            cardPicked = false;
            spotPicked = false;

        }


    }

    /**
     * Radera? tänker att vi inte ska ha event listeners i controller -Elna
     * @param event
     */
    public void handleCardClick(MouseEvent event) {

        ImageView clicked = (ImageView) event.getSource();
        Card card = (Card) clicked.getUserData();
        pickCard(card);
    }

    public void pickCard(Card card) {
        playerOne.addCardToDeck(card);
    }


    public void setCards(ArrayList<Card> cards) {
        this.allCards = cards;
    }

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
}
