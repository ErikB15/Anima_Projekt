package Controller;

import java.util.ArrayList;
import Model.*;
import View.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Game Controller klassen, syftet är att kontroller flödet av information från view och Model under matchens gång.
 * När matchens avslutas stängs även game controllern, då den endast är nödvändig när matchen är aktiv.
 */
public class GameController {
    private ArrayList<Card> allCards;
    private ArrayList<Effect> allEffects;
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
        allCards.add(new Card("Test1", 1,2,1,null, "CardFRONT.png"));
        allCards.add(new Card("Test2", 1,2,2,null, "CardFRONT.png"));
        allCards.add(new Card("Test3", 1,2,3,null, "CardFRONT.png"));
        allCards.add(new Card("Test4", 1,2,4,null, "CardFRONT.png"));
        allCards.add(new Card("Test5", 1,2,5,null, "CardFRONT.png"));
        allCards.add(new Card("Test6", 1,2,6,null, "CardFRONT.png"));
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
                playerOne.addCard(testCard);
                allCards.remove(testCard);
                random = 2;
            }else{
                // Där bör finnas logik här för vilket kort spelaren väljer.
                // Svårt att implementera utan GUI:n dock.
                playerTwo.addCard(testCard);
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

        view.setImage(new Image(url.toExternalForm()));
        view.setUserData(card);
    }



    public void handleCardClick(MouseEvent event) {

        ImageView clicked = (ImageView) event.getSource();
        Card card = (Card) clicked.getUserData();
        pickCard(card);
    }

    public void pickCard(Card card) {
        System.out.println("Picked card: " + card);
    }


    public void setCards(ArrayList<Card> cards) {
        this.allCards = cards;
    }

    public void setGuiManager(GUIManager guiManager){
        this.guiManager=guiManager;
    }
}
