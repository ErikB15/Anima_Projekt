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
 * Alla metoder anropas från en bakgrundstråd (nätverkstråden).
 * GameController använder därför Platform.runLater() för att säkert
 * uppdatera JavaFX-tråden när dessa metoder anropas.
 *
 * @author Leo
 */
public interface GameStateListener {
    /**
     * Anropas när spelaren anslutit men väntar på motståndaren.
     * @author Leo
     */
    void onWaiting();

    /**
     * Anropas när det är spelarens tur att agera.
     * @author Leo
     */
    void onYourTurn();

    /**
     * Anropas när servern skickar ett uppdaterat spelläge.
     * Spelläget levereras som en JSON-sträng som GUIManager
     * ansvarar för att tolka och rita om.
     *
     * @param json spelläget serialiserat som JSON
     * @author Leo
     */
    void onGameStateUpdate(String json);

    /**
     * Anropas när spelet är slut.
     *
     * @param winnerName namnet på spelaren som vann
     * @author Leo
     */
    void onGameOver(String winnerName);

    /**
     * Anropas när ett felmeddelande tas emot från servern.
     *
     * @param message beskrivning av felet
     * @author Leo
     */
    void onError(String message);

    /**
     * Anropas när ett chattmeddelande tas emot.
     *
     * @param message chattmeddelandets text
     * @author Leo
     */
    void onChat(String message);

    /**
     * Anropas när spelet startar och servern tilldelar spelaren en roll.
     * Rollen är antingen "PLAYER_ONE" eller "PLAYER_TWO".
     *
     * @param role spelarens tilldelade roll som sträng
     * @author Leo
     */
    void onGameStart(String role);

    /**
     * Anropas när det är spelarens tur att välja ett kort i draft-fasen.
     * @author Leo
     */
    void onDraftTurn();
}
