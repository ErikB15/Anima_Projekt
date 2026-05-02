package Network;

import Model.GameState;
import Model.PlayerID;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import Model.Player;
import java.util.stream.*;

/**
 * Kör på hostens dator. Tar emot anslutningar,
 * startar spelet när 2 spelare är inne, vidarebefordrar meddelanden
 */
public class GameServer {

    private static final int PORT = 5555;
    private final Gson gson = new Gson();
    private GameState gameState;
    private final Map<String, PlayerID> playerRoles = new LinkedHashMap<>();
    // playerRoles kopplar nätverksnamnet ("Player1") till PlayerID.PLAYER_ONE

    // Håller koll på anslutna spelare, steamId och deras output-ström
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
    public synchronized void registerPlayer(String name, PrintWriter out) {
        players.put(name, out);

        if (players.size() == 1) {
            playerRoles.put(name, PlayerID.PLAYER_ONE);
            sendToPlayer(name, new GameMessage(GameMessage.Type.WAITING, "", ""));
        } else {
            playerRoles.put(name, PlayerID.PLAYER_TWO);

            // Skapa spelare och spelläge nu när båda är inne
            Player p1 = new Player(getPlayerNameByRole(PlayerID.PLAYER_ONE));
            Player p2 = new Player(name);
            gameState = new GameState(p1, p2, new Board());
            gameState.setCurrentPlayer(PlayerID.PLAYER_ONE);

            broadcast(new GameMessage(GameMessage.Type.GAME_START, "", ""));

            String firstPlayer = getPlayerNameByRole(PlayerID.PLAYER_ONE);
            sendToPlayer(firstPlayer, new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));
        }
    }

    public synchronized void handlePlayCard(String playerName, String cardId) {
        // Spellogik kopplas in här av logik-polaren senare
        // gameState.something(...)
        String json = gson.toJson(gameState);
        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, json, ""));
    }

    public synchronized void handleEndTurn(String playerName) {
        gameState.switchTurn();
        String json = gson.toJson(gameState);
        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, json, ""));

        String nextPlayerName = gameState.getCurrentPlayer().getName();
        sendToPlayer(nextPlayerName, new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));
    }

    private String getPlayerNameByRole(PlayerID role) {
        return playerRoles.entrySet().stream()
                .filter(e -> e.getValue() == role)
                .map(Map.Entry::getKey)
                .findFirst().orElse("");
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

    public Map<String, PrintWriter> getPlayers() {
        return players;
    }
}