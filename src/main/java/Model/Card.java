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

    public Card(String cardName, int cardAD, int cardMaxHP, int cardID, Effect cardEffect, String imagePath){
        this.cardAD = cardAD;
        this.cardMaxHP = cardMaxHP;
        this.cardID = cardID;
        this.cardName = cardName;
        this.cardEffect = cardEffect;
        this.imagePath = imagePath;
        // Här ska det läggas till en formel för att räkna ut kortets kostnad
    }


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
}
