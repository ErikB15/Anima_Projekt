package Network;


/**
 * Interface som GameController implementerar för att ta emot
 * nätverkshändelser från GameClient.
 *
 * Syftet med detta interface är att hålla nätverkskoden separerad
 * från GUI koden. GameClient vet ingenting om JavaFX eller hur
 * skärmen ser ut, den anropar bara dessa metoder när något händer.
 * GameController tar emot anropet och vidarebefordrar till GUIManager.
 *
 * Viktigt här är att alla metoder anropas från en bakgrundstråd (nätverkstråden).
 * GameController använder Platform.runLater() för att säkert uppdatera
 * JavaFX tråden från dessa metoder från vad jag fattade.
 *
 * @author Leo
 */
public interface GameStateListener {
    /**
     * Anropas när spelaren anslutit men väntar på motståndaren.
     * @author Leo
     */
    void onWaiting();                       //visa "väntar på motståndare"

    /**
     * Anropas när det är spelarens tur att agera.
     * @author Leo
     */
    void onYourTurn();                      //aktiverA spelarens knappar, så de kan trycka

    /**
     * Anropas när servern skickar ett uppdaterat spelläge.
     * Spelläget levereras som en JSON-sträng som GUIManager
     * ansvarar för att tolka och rita om.
     *
     * @param json spelläget serialiserat som JSON
     * @author Leo
     */
    void onGameStateUpdate(String json);    //ta emot spelläge som JSON

    /**
     * Anropas när spelet är slut.
     *
     * @param winnerName namnet på spelaren som vann
     * @author Leo
     */
    void onGameOver(String winnerName);     //visa vinnare

    /**
     * Anropas när ett felmeddelande tas emot från servern.
     *
     * @param message beskrivning av felet
     * @author Leo
     */
    void onError(String message);           //visa felmeddelande

    /**
     * Anropas när ett chattmeddelande tas emot.
     *
     * @param message chattmeddelandets text
     * @author Leo
     */
    void onChat(String message);           //detta är om vi implemnetrar en chat, då visa chatmeddelande
}


//spellägget skicka som en rå JSON fil så vi får se hur vi löser det med GUI tolka när vi vet vad den ska rita och hur
