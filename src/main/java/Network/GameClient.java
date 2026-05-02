package Network;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

/**
 * imports som måste göra så vi kan skicka json stränar plus ha sockets
 * Varje spelare har en instans av denna klass, den öppnar en socket till servern
 * lyssanr i bakgrunden och anropar GameStateListenr när något händer
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

    public GameClient(String serverHost, int serverPort){
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    //Metod som Ansluter till servern, steamId och displayname sätts här
    public void connect(String steamId, String displayName) throws Exception{
        this.steamId = steamId;
        this.displayName = displayName;
        socket = new Socket(serverHost, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        new Thread(this::listenToServer).start();           //En bakgrundtråd som lyssnar på servern
        send(new GameMessage(GameMessage.Type.JOIN, displayName, steamId)); //presentera sig för server, vi skickar att vi sa join game med steamid
    }

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

    //metoder nedan är handlingar en spelare kan utföra
    public void playCard(String cardId) {
        send(new GameMessage(GameMessage.Type.PLAY_CARD, cardId, steamId));
    }

    public void endTurn() {
        send(new GameMessage(GameMessage.Type.END_TURN, " ", steamId));
    }

    public void sendChat(String message) {
        send(new GameMessage(GameMessage.Type.CHAT, message, steamId));
    }

    private void send(GameMessage msg){
        out.println(gson.toJson(msg));
    }

    public void setListener(GameStateListener listener){
        this.listener = listener;
    }

    public String getSteamId() {
        return steamId;
    }
    public String getDisplayName() {
        return displayName;
    }
}
