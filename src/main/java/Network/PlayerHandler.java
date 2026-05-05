package Network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import com.google.gson.Gson;
import java.net.Socket;

/**
 * Hanterar kommunikationen med en enskild spelare på servern.
 * En instans skapas per spelare och körs i en egen bakgrundstråd.
 * Lyssnar kontinuerligt på spelarens meddelanden och delegerar
 * hanteringen vidare till GameServer.
 *
 * @author Leo
 */
public class PlayerHandler implements Runnable {
    private final Socket socket;
    private final GameServer server;
    private final Gson gson = new Gson();
    private PrintWriter out;
    private String steamId;
    private String displayName;

    /**
     * Skapar en ny PlayerHandler för en ansluten spelare.
     *
     * @param socket socketen för spelarens anslutning
     * @param server referens till spelservern som hanterar spellogiken
     * @author Leo
     */
    public PlayerHandler(Socket socket, GameServer server){
        this.socket = socket;
        this.server =  server;
    }

    /**
     * Startar lyssnarslingan för denna spelare.
     * Körs i en bakgrundstråd och läser meddelanden från spelaren
     * tills anslutningen stängs eller tappas.
     * Varje rad deserialiseras från JSON till ett GameMessage.
     *
     * @author Leo
     */
    @Override
    public void run(){
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {

            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) !=null) {
                GameMessage msg = gson.fromJson(line, GameMessage.class);
                handleMessage(msg);
            }
        } catch (Exception e) {
            System.out.println("Player disconnected: " + displayName);
        }
    }

    /**
     * Hanterar ett inkommet meddelande från spelaren.
     * Delegerar varje meddelandetyp till rätt metod i GameServer
     * så att PlayerHandler inte innehåller någon spellogik själv.
     *
     * @param msg meddelandet som tagits emot från spelaren
     * @author Leo
     */
    private void handleMessage(GameMessage msg) {
        System.out.println("Server got " + msg);

        switch (msg.getType()) {
            case JOIN -> {
                // Registrera spelaren med deras namn och nätverksström
                this.steamId = msg.getSteamId();
                server.registerPlayer(steamId, out);
            }
            case PLAY_CARD -> {
                // Delegera till servern som hanterar spellogik och broadcast
                server.handlePlayCard(steamId, msg.getPayload());
            }
            case END_TURN -> {
                // Delegera till servern som byter tur och notifierar spelarna
                server.handleEndTurn(steamId);
            }
            case CHAT -> {
                // Skicka chattmeddelandet till alla spelare
                server.broadcast(new GameMessage(GameMessage.Type.CHAT, msg.getPayload(), steamId));
            }
        }
    }

}
