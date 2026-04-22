package Model;

public class Card {
    private String cardName;
    private int cardAD;
    private int cardHP;
    private int cardID;
    private Effect cardEffect;
    private int cardCost;
    private boolean asleep;
    private String imagePath;

    public Card(String cardName, int cardAD, int cardHP, int cardID, Effect cardEffect, String imagePath){
        this.cardAD = cardAD;
        this.cardHP = cardHP;
        this.cardID = cardID;
        this.cardName = cardName;
        this.cardEffect = cardEffect;
        this.imagePath = imagePath;
        // Här ska det läggas till en formel för att räkna ut kortets kostnad
    }

    public String getImagePath(){
        return imagePath;
    }

    public int getCardAD(){
        return cardAD;
    }
    public int getCardHP(){
        return cardHP;
    }

    public int getCardID() {
        return cardID;
    }
}
