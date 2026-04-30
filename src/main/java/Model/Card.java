package Model;

public class Card {
    private String cardName;
    private int cardAD;
    private int cardMaxHP;
    private int cardCurrentHP;
    private int cardID;
    private Effect cardEffect;
    private int cardCost;
    private boolean asleep;
    private boolean hasAttackedThisTurn;
    private String imagePath;


    /**
     * Denna klassen är egentligen typ bara sparande av information.
     * Har lite hantering också men den är inte mycket.
     * Har gjort en TEMPORÄR  formel som kan komma att ändras, gjort för testning.
     * @param cardName - Namnet på kortet.
     * @param cardAD - Attack förmågan av kortet.
     * @param cardMaxHP - Kortets max HP.
     * @param cardID - Kortets ID.
     * @param cardEffect - En effekt som kortet eventuellt kommer at ha.
     * @param imagePath - Vägen till bilden som är kopplad till kortet.
     * @author Jim Ström
     */
    public Card(String cardName, int cardAD, int cardMaxHP, int cardID, Effect cardEffect, String imagePath){
        this.cardAD = cardAD;
        this.cardMaxHP = cardMaxHP;
        this.cardID = cardID;
        this.cardName = cardName;
        this.cardEffect = cardEffect;
        this.imagePath = imagePath;
        this.cardCurrentHP = cardMaxHP;

        cardCost = (Math.round((float)(cardAD + cardMaxHP) / 4));
        // Temporär formel som lagts till endast för testning.
    }

    /**
     * Gjord för att kortet ska kunna ta skada.
     * Om skadan sänker kortets hp under 0 så blir hp:et istället bara 0.
     * Annars tar den skadan som vanligt.
     * @param damage - Skadan som sker på kortet.
     * @author Jim Ström.
     */
    public void takeDamage(int damage) {
        if(cardCurrentHP - damage <= 0){
            cardCurrentHP = 0;
        }else{
            cardCurrentHP -= damage;
        }
    }

    /**
     * Denna kollar bara om kortet är dött eller inte, gjort nu, kanske inte används, för se.
     * @return Returnerar en boolean som säger sant om kortet är dött eller falskt om kortet inte är dött.
     * @author Jim Ström
     */
    public boolean isDead() {return cardCurrentHP <= 0;}


    /**
     * Normaltvis försöker jag göra en kommentar på allting.
     * Men om ni skrollar ner lite, tror jag ni förstår varför jag inte gör en kommentar på allting under.
     * Kommer det in att vi måste göra kommentarer på allt? Då får jag göra det men i nuläget skippar vi detta.
     * Massa getters och setters för alla de olika variablerna som Card klassen innehåller.
     * @author Jim Ström
     */


    public void setCardCurrentHP(int cardCurrentHP) {this.cardCurrentHP = cardCurrentHP;}

    public void setAsleep(boolean asleep) {this.asleep = asleep;}

    public void setHasAttackedThisTurn(boolean hasAttackedThisTurn) {this.hasAttackedThisTurn = hasAttackedThisTurn;}

    public String getImagePath(){
        return imagePath;
    }

    public int getCardAD(){
        return cardAD;
    }

    public int getCardMaxHP(){
        return cardMaxHP;
    }

    public int getCardID() {
        return cardID;
    }

    public int getCardCurrentHP() {return cardCurrentHP;}

    public boolean getAsleep(){return asleep;}

    public boolean getHasAttackedThisTurn(){return hasAttackedThisTurn;}

    public int getCardCost() {return cardCost;}

    public String getCardName(){
        return cardName;
    }
}
