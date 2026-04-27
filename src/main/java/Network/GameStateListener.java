package Network;


/**
 * Ett interface som GUI manager ska implemnetera. Nätverkskoden ska ej veta något om gui koden/javafx
 * ska endast anropa dessa metoder i denna och GUI manager uppdaterar skärmen
 */
public interface GameStateListener {
    void onWaiting();                       //visa "väntar på motståndare"
    void onYourTurn();                      //aktiverA spelarens knappar, så de kan trycka
    void onGameStateUpdate(String json);    //ta emot spelläge som JSON
    void onGameOver(String winnername);     //visa vinnare
    void onError(String message);           //visa felmeddelande
    void onChat(String message);           //detta är om vi implemnetrar en chat, då visa chatmeddelande
}


//spellägget skicka som en rå JSON fil så vi får se hur vi löser det med GUI tolka när vi vet vad den ska rita och hur
