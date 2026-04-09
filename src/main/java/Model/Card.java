package Model;

public class Card {
    private String cardName;
    private int cardAD;
    private int cardHP;
    private int cardID;
    private Effect cardEffect;
    private int cardCost;
    private boolean asleep;

    public Card(String cardName, int cardAD, int cardHP, int cardID, Effect cardEffect){
        this.cardAD = cardAD;
        this.cardHP = cardHP;
        this.cardID = cardID;
        this.cardName = cardName;
        this.cardEffect = cardEffect;
        // Här ska det läggas till en formel för att räkna ut kortets kostnad
    }
}
