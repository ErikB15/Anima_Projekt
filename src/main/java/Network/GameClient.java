package Network;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

/**
 * Representerar en spelares anslutning till spelservern.
 * Varje spelare skapar en instans av denna klass för att ansluta.
 * Klassen öppnar en socket, lyssnar på servern i en bakgrundstråd
 * och anropar GameStateListener när något händer i spelet.
 *
 * @author Leo
 */
public class GameClient {

    private final String serverHost;
    private final int serverPort;
    private final Gson gson = new Gson();

    private Socket socket;
    private PrintWriter out;
    private String steamId;
    private String displayName;
    private GameStateListener listener;

    /**
     * Skapar en ny GameClient som är redo att ansluta till en server.
     *
     * @param serverHost serverns IP adress, "localhost" för samma dator
     * @param serverPort porten servern lyssnar på
     * @author Leo
     */
    public GameClient(String serverHost, int serverPort){
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * Ansluter till spelservern och identifierar spelaren.
     * Öppnar en socket, startar en bakgrundstråd som lyssnar på servern
     * och skickar ett JOIN meddelande så servern vet vem som anslutit.
     *
     * @param steamId     spelarens unika namn som identifierar dem på servern
     * @param displayName spelarens visningsnamn
     * @throws Exception om anslutningen till servern misslyckas
     * @author Leo
     */
    public void connect(String steamId, String displayName) throws Exception{
        this.steamId = steamId;
        this.displayName = displayName;
        socket = new Socket(serverHost, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        new Thread(this::listenToServer).start();           //En bakgrundtråd som lyssnar på servern
        send(new GameMessage(GameMessage.Type.JOIN, displayName, steamId)); //presentera sig för server, vi skickar att vi sa join game med steamid
    }


    /**
     * Lyssnar kontinuerligt på meddelanden från servern.
     * Körs i en egen bakgrundstråd så att GUI tråden inte blockeras.
     * Varje rad som tas emot deserialiseras från JSON till ett GameMessage.
     *
     * @author Leo
     */
    private void listenToServer() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader((socket.getInputStream())))) {
            String line;
            while ((line = in.readLine()) != null) {
                GameMessage msg = gson.fromJson(line, GameMessage.class);
                handleMessage(msg);
            }
        } catch (Exception e) {
            System.out.println("Tappad anslutning till server.");
        }
    }


    /**
     * Hanterar inkommande meddelanden från servern.
     * Vidarebefordrar händelsen till GameStateListener som GameController
     * implementerar, vilket håller nätverkskoden separerad från GUI-koden.
     *
     * @param msg meddelandet som tagits emot från servern
     * @author Leo
     */
    private void handleMessage(GameMessage msg){
        if (listener == null)
            return;
        switch (msg.getType()) {
            case WAITING -> listener.onWaiting();
            case YOUR_TURN -> listener.onYourTurn();
            case GAME_STATE -> listener.onGameStateUpdate(msg.getPayload());
            case GAME_OVER -> listener.onGameOver(msg.getPayload());
            case ERROR -> listener.onError(msg.getPayload());
            case CHAT -> listener.onChat(msg.getPayload());
        }
    }

    //metoder nedan är handlingar en spelare kan

    /**
     * Skickar ett meddelande om att spelaren vill spela ett kort.
     *
     * @param cardId id på kortet som spelas
     * @author Leo
     */
    public void playCard(String cardId) {
        send(new GameMessage(GameMessage.Type.PLAY_CARD, cardId, steamId));
    }

    /**
     * Skickar ett meddelande om att spelaren avslutar sin tur.
     *
     * @author Leo
     */
    public void endTurn() {
        send(new GameMessage(GameMessage.Type.END_TURN, " ", steamId));
    }

    /**
     * Skickar ett chattmeddelande till alla spelare.
     *
     * @param message texten som ska skickas
     * @author Leo
     */
    public void sendChat(String message) {
        send(new GameMessage(GameMessage.Type.CHAT, message, steamId));
    }

    /**
     * Serialiserar ett GameMessage till JSON och skickar det till servern.
     *
     * @param msg meddelandet som ska skickas
     * @author Leo
     */
    private void send(GameMessage msg){
        out.println(gson.toJson(msg));
    }

    /**
     * Registrerar en lyssnare som får speluppdateringar från servern.
     * GameController implementerar detta interface och kopplas hit.
     *
     * @param listener instansen som ska ta emot nätverkshändelser
     * @author Leo
     */
    public void setListener(GameStateListener listener){
        this.listener = listener;
    }

    /**
     * @return spelarens namn som används för identifiering på servern
     */
    public String getSteamId() {
        return steamId;
    }

    /**
     * @return spelarens visningsnamn
     */
    public String getDisplayName() {
        return displayName;
    }
}
