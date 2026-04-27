package Network;

import com.google.gson.Gson;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Kör på hostens dator. Tar emot anslutningar,
 * startar spelet när 2 spelare är inne, vidarebefordrar meddelanden
 */
public class GameServer {

    private static final int PORT = 5555;
    private final Gson gson = new Gson();

    // Håller koll på anslutna spelare, steamId - deras output-ström
    private final Map<String, PrintWriter> players = new LinkedHashMap<>();

    public void start() {
        System.out.println("Server startar på port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (players.size() < 2) {
                Socket socket = serverSocket.accept();
                System.out.println("Spelare anslöt!");
                // Varje spelare får sin egen tråd på servern
                new Thread(new PlayerHandler(socket, this)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Registreras när en spelare skickat JOIN
    public synchronized void registerPlayer(String steamId, PrintWriter out) {
        players.put(steamId, out);
        System.out.println("Registrerade: " + steamId + " (" + players.size() + "/2)");

        if (players.size() == 1) {
            // Första spelaren väntar
            sendToPlayer(steamId, new GameMessage(GameMessage.Type.WAITING, "", ""));
        } else {
            // Båda inne, starta spel
            broadcast(new GameMessage(GameMessage.Type.GAME_START, "", ""));
            // Ge tur till första spelaren
            String firstPlayer = players.keySet().iterator().next();
            sendToPlayer(firstPlayer, new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));
        }
    }

    // Skicka till alla anslutna spelare
    public void broadcast(GameMessage msg) {
        String json = gson.toJson(msg);
        players.values().forEach(out -> out.println(json));
    }

    // Skicka till en specifik spelare
    public void sendToPlayer(String steamId, GameMessage msg) {
        PrintWriter out = players.get(steamId);
        if (out != null) out.println(gson.toJson(msg));
    }

    public Map<String, PrintWriter> getPlayers() { return players; }
}