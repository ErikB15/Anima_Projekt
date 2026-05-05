package Network;


/**
 * Representerar ett meddelande som skickas mellan server och klient.
 * Fungerar som ett "kuvert" där type beskriver vad meddelandet handlar om
 * och payload innehåller den faktiska datan som en JSON sträng.
 *
 * Alla kommunikation i nätverket sker via denna klass.
 *
 * @author Leo
 */
public class GameMessage {

    public enum Type {
        // Från Klient till Server
        JOIN,                       //Spelare anssluter med sitt steam id
        PLAY_CARD,                  //Spelare spelar ett kort
        END_TURN,                   //Spelare avslutar sin tur

        // Från server till klient
        WAITING,                    //Väntar på motståndare
        GAME_START,                 //Spelet börjar, skicka startläge
        GAME_STATE,                 //Uppdaterat spelläge efter ett drag
        YOUR_TURN,                  //Det är din tur
        GAME_OVER,                  //Spelet är slut

        // Båda håll
        ERROR,                      //något gick fel
        CHAT                        // Chatmedelande
    }
        private Type type;          //vilken typ
        private String payload;     //JSON sträng med relevcant data
        private String steamId;     // vem som skickar

        public GameMessage() {}

    /**
     * Skapar ett nytt meddelande med typ, data och avsändare.
     *
     * @param type    vilken typ av meddelande det är
     * @param payload data som medföljer, JSON sträng eller tom sträng
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
