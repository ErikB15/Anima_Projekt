package Model;

import java.util.ArrayList;
import java.util.Collections;

public class Player {
    private int hp;
    private ArrayList<Card> deck;
    private ArrayList<Card> hand;
    private ArrayList<Card> graveyard;
    private String name; //la till detta för konstruktorn för att servenr behöver veta vilket nätverks id som tillhär vilken spelare
    //annrs leder det till att när vi skcikar json stängar till klienten vet den inte vem det tillhör


    /**
     * Spelaren har koll på 4 viktiga grejer. Sitt egna HP samt tre olika typer av "kortlekar"
     * Deck - De korten som spelaren drar från varje runda.
     * Hand - De korten en spelare kan spela och har i sin hand varje runda.
     * Graveyard - De korten en spelare har förlorat.
     */
    public Player(String name){
        this.hp = 30; // PS. 30 Är bara test "value" just nu.
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.graveyard = new ArrayList<>();
        this.name = name;
    }

    /**
     * Lägger bara till ett kort, nödvändigt för första fasen där de två spelarna väljer kort.
     * @param addedCard Kortet en spelare valt att de vill ha.
     */
    public void addCardToDeck(Card addedCard){
        deck.add(addedCard);
    }

    /**
     * Metoden som tillåter spelaren att dra ett nytt kort från sin "deck".
     * Den har några safety checks, är spelarens kortlek tom så reshuffle vi den genom en metod.
     * Sen kollar vi igen, detta är en safety check för att se till att allt funkade som det skulle.
     * Sedan drar vi kortet från kortleken.
     */
    public void drawUntilHandIsFull() {
        for (int i = hand.size(); i < 3; i++) {

            if (deck.isEmpty()) {
                reshuffleDeck();
            }

            if (deck.isEmpty()) {
                return;
            }

            int randomIndex = (int)(Math.random() * deck.size());
            hand.add(deck.remove(randomIndex));
        }
    }


    /**
     * Det här är för när ett kort dör, då ska det läggas in i "graveyard" kortleken.
     * Notera att vi nollställer kortets stats efter vi slängt in den i graveyard kortleken.
     * @param deadCard - Det kortet som blev dödat.
     */
    public void sendCardToGraveyard(Card deadCard){
        graveyard.add(deadCard);
        deadCard.setCardCurrentHP(deadCard.getCardMaxHP());
        deadCard.setAsleep(false);
        deadCard.setHasAttackedThisTurn(false);
    }

    /**
     * Simpelt, lägger till alla korten från graveyard in i din riktiga deck.
     * Sedan clearar vi graveyard och gör den tom igen.
     * Därefter shufflar vi den genom en "Collection" metod.
     */
    public void reshuffleDeck(){
        deck.addAll(graveyard);
        graveyard.clear();
        Collections.shuffle(deck);
    }

    public String getName() {
        return name;
    }


    public void takeDamage(int damage) {
        if ((hp - damage) <= 0) {
            this.hp = 0;
        } else {
            this.hp = (hp - damage);
        }
    }

    /**
     * Kan du gissa vad detta göra?
     * @param hp - HP:et du vill sätta det till.
     * @return - En boolean, syftet med denna är att göra en enkel check som till och med kollar ifall matchen
     * är över.
     */
    public boolean setHp(int hp) {
        this.hp = hp;
        if(hp <= 0){
            return true;
        }
        return false;
    }

    /**
     * Enkel getter.
     * @return Ger tillbaka HP:et av spelaren
     */
    public int getHp() {
        return hp;
    }

    /**
     * Enkel getter för att hämta handen spelaren har.
     * @return En arraylist av cards som är spelarens hand.
     * @author Jim Ström
     */

    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Enkel getter för att hämta kortlek spelaren har.
     * @return En arraylist av cards som är spelarens kortlek.
     * @author Jim Ström
     */
    public ArrayList<Card> getDeck() {
        return deck;
    }

    /**
     * Enkel getter för att hämta graveyard spelaren har.
     * @return En arraylist av cards som är spelarens graveyard.
     * @author Jim Ström
     */
    public ArrayList<Card> getGraveyard() {
        return graveyard;
    }




}
