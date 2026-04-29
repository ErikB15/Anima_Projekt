package Model;

import java.util.ArrayList;
import java.util.Collections;

public class Player {
    private int hp;
    private ArrayList<Card> deck;
    private ArrayList<Card> hand;
    private ArrayList<Card> graveyard;


    /**
     * Spelaren har koll på 4 viktiga grejer. Sitt egna HP samt tre olika typer av "kortlekar"
     * Deck - De korten som spelaren drar från varje runda.
     * Hand - De korten en spelare kan spela och har i sin hand varje runda.
     * Graveyard - De korten en spelare har förlorat.
     * @author Jim Ström
     */
    public Player(){
        this.hp = 30; // PS. 30 Är bara test "value" just nu.
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.graveyard = new ArrayList<>();
    }

    /**
     * Lägger bara till ett kort, nödvändigt för första fasen där de två spelarna väljer kort.
     * @param addedCard Kortet en spelare valt att de vill ha.
     * @author Jim Ström
     */
    public void addCardToDeck(Card addedCard){
        deck.add(addedCard);
    }

    /**
     * Metoden som tillåter spelaren att dra ett nytt kort från sin "deck".
     * Den har några safety checks, är spelarens kortlek tom så reshuffle vi den genom en metod.
     * Sen kollar vi igen, detta är en safety check för att se till att allt funkade som det skulle.
     * Sedan drar vi kortet från kortleken.
     * @author Jim Ström
     */
    public void fillHandToThree() {
        while (hand.size() < 3) {
            if (deck.isEmpty()) {
                reshuffleDeck();
            }

            if (deck.isEmpty()) {
                return;
            }

            hand.add(deck.removeFirst());
        }
    }

    /**
     * Det här är för när ett kort dör, då ska det läggas in i "graveyard" kortleken.
     * Notera att vi nollställer kortets stats efter vi slängt in den i graveyard kortleken.
     * @param deadCard - Det kortet som blev dödat.
     * @author Jim Ström
     */
    public void sendCardToGraveyard(Card deadCard){
        if (deadCard == null) {
            return;
        }
        graveyard.add(deadCard);
        deadCard.setCardCurrentHP(deadCard.getCardMaxHP());
        deadCard.setAsleep(false);
        deadCard.setHasAttackedThisTurn(false);
    }

    /**
     * Simpelt, lägger till alla korten från graveyard in i din riktiga deck.
     * Sedan clearar vi graveyard och gör den tom igen.
     * Därefter shufflar vi den genom en "Collection" metod.
     * @author Jim Ström
     */
    public void reshuffleDeck(){
        deck.addAll(graveyard);
        graveyard.clear();
        Collections.shuffle(deck);
    }

    /**
     * Checkar först så att index är i handen, borde göras innan men alltid bra att vara säker.
     * Stämmer indexen på något sätt inte så returneras null, lär behöva hanteras i controllern.
     * @param index - Indexet på kortet i handen som ska tas bort.
     * @return - Ger kortet som ska tas bort (för att kunna lägga det på planen)
     * @author Jim Ström
     */
    public Card removeCardFromHand(int index){
        if(index < 0 || index >= hand.size()){
            return null;
        }
        return hand.remove(index);
    }

    /**
     * En if check för att sätta HP:et till 0 om det skulle egentligen bli mindre än 0.
     * För att förenkla och inte göra det problematiskt med andra int checks.
     * @param damage - Skadan gjord på spelaren.
     * @author Jim Ström
     */
    public void takeDamage(int damage) {
        if((hp - damage) <= 0){
            this.hp = 0;
        }else {
            this.hp = (hp - damage);
        }
    }

    /**
     * Kollar om spelaren är död, är HP:et mindre eller lika med noll (borde inte kunna bli mindre),
     * så returneras true, då spelaren är död.
     * @return En boolean som säger ja eller nej beroende på om spelaren är död eller inte.
     * @author Jim Ström.
     */
    public boolean isDead(){
        return hp <= 0;
    }

    /**
     * Enkel getter.
     * @return Ger tillbaka HP:et av spelaren
     * @author Jim Ström
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
