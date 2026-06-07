package Network;


/**
 * Representerar ett meddelande som skickas mellan server och klient.
 * Fungerar som ett kuvert där type beskriver vad meddelandet handlar om
 * och payload innehåller den faktiska datan som en JSON-sträng.
 *
 * All kommunikation i nätverket sker via denna klass.
 *
 * @author Leo
 */
public class GameMessage {

    public enum Type {
        JOIN,
        PLAY_CARD,
        END_TURN,
        DRAFT_PICK,
        ATTACK_CARD,
        ATTACK_PLAYER,
        WAITING,
        GAME_START,
        GAME_STATE,
        YOUR_TURN,
        DRAFT_TURN,
        GAME_OVER,
        ERROR,
        CHAT
    }

    private Type type;
    private String payload;
    private String steamId;

    /**
     * Tom konstruktor som krävs av Gson för att kunna deserialisera JSON.
     *
     * @author Leo
     */
    public GameMessage() {}

    /**
     * Skapar ett nytt meddelande med typ, data och avsändare.
     *
     * @param type    vilken typ av meddelande det är
     * @param payload data som medföljer, JSON-sträng eller tom sträng
     * @param steamId namnet på den som skickar
     * @author Leo
     */
    public GameMessage(Type type, String payload, String steamId){
        this.type = type;
        this.payload = payload;
        this.steamId = steamId;
    }

    /**
     * @return meddelandets typ
     */
    public Type getType() {
        return type;
    }

    /**
     * @return payload-strängen, kan vara JSON eller tom
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @return namnet på den som skickade meddelandet
     */
    public String getSteamId() {
        return steamId;
    }
}
