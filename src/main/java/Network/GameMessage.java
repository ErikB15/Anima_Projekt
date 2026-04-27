package Network;


/**
 * Sjölva "kuveret som skickas mellan server och klient med data
 * Alla meddelande mellan server och klient har detta format,
 * vilken typ av enum bestämmer payload innehåller
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


        public GameMessage(Type type, String payload, String steamId){
            this.type = type;
            this.payload = payload;
            this.steamId = steamId;
        }

    public Type getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public String getSteamId() {
        return steamId;
    }
}
