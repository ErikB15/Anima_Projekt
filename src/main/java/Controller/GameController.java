package Controller;

import java.util.ArrayList;
import Model.*;
import View.*;

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
    private GUIManager guiManager; //ELNA: La till denna för att koppla GUIManager till controller
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
        this.testCard = new Card("Test", 1,2,25,null);
        // Sedan lägger vi till det i vår arraylist av alla kort.
        allCards.add(testCard);
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
}
