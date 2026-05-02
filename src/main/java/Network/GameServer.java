package Network;

import Model.GameState;
import Model.PlayerID;
import Model.Board;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import Model.Player;
import java.util.stream.*;

/**
 * Spelservern som körs på hostens dator.
 * Ansvarar för att ta emot spelaranslutningar, skapa spelläget när
 * båda spelarna är inne, och hantera kommunikationen under spelets gång.
 *
 * Servern håller två uppslagstabeller:
 *   players som kopplar spelarnamn till deras nätverksström (för att skicka data)
 *   playerRoles som kopplar spelarnamn till deras roll (PLAYER_ONE eller PLAYER_TWO)
 *
 * @author Leo
 */
public class GameServer {

    private static final int PORT = 5555;
    private final Gson gson = new Gson();
    /**
     * Håller det aktuella spelläget. Skapas när båda spelarna anslutit.
     * Lever på servern och är den enda källan till sanning om spelets tillstånd.
     */
    private GameState gameState;
    /**
     * Kopplar spelarnamn till deras roll i spelet.
     * Den som ansluter först blir PLAYER_ONE, den andre PLAYER_TWO.
     * LinkedHashMap används för att ordningen spelarna anslöt bevaras.
     */
    private final Map<String, PlayerID> playerRoles = new LinkedHashMap<>();
    // playerRoles kopplar nätverksnamnet ("Player1") till PlayerID.PLAYER_ONE

    /**
     * Kopplar spelarnamn till deras nätverksström.
     * Används för att skicka meddelanden till en specifik spelare.
     * LinkedHashMap används för att ordningen spelarna anslöt bevaras.
     */
    private final Map<String, PrintWriter> players = new LinkedHashMap<>();

    /**
     * Startar servern och väntar på att två spelare ska ansluta.
     * Varje spelare som ansluter får en egen PlayerHandler i en bakgrundstråd
     * så att servern kan hantera båda spelarna samtidigt.
     *
     * @author Leo
     */
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


    /**
     * Registrerar en spelare när de skickat JOIN till servern.
     * Hanterar två fall här ---
     *  Första spelaren, tilldelas PLAYER_ONE och får vänta
     *  Andra spelaren, tilldelas PLAYER_TWO, spelläget skapas och spelet startar
     *
     * Metoden är synchronized för att förhindra att två spelare registreras
     * samtidigt från olika trådar, vilket skulle kunna förstöra spelläget.
     *
     * @param name spelarens namn som används som identifierare
     * @param out  nätverksströmmen som används för att skicka data till spelaren
     * @author Leo
     */
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

            // PLAYER_ONE börjar alltid
            String firstPlayer = getPlayerNameByRole(PlayerID.PLAYER_ONE);
            sendToPlayer(firstPlayer, new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));
        }
    }

    /**
     * Hanterar när en spelare spelar ett kort.
     * Tänker att spellogiken kopplas in här av logik ansvarig/JIM, när den är klar.
     * Broadcastar det uppdaterade spelläget till båda spelarna efteråt.
     *
     * Metoden är synchronized för att säkerställa att endast ett drag
     * behandlas åt gången.
     *
     * @param playerName namnet på spelaren som spelar kortet
     * @param cardId     id på kortet som spelas
     * @author Leo
     */
    public synchronized void handlePlayCard(String playerName, String cardId) {
        // Spellogik kopplas in här av Jim senare
        // gameState.something(...) vet inte riktigt men vad det nu gör
        String json = gson.toJson(gameState);
        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, json, ""));
    }

    /**
     * Hanterar när en spelare avslutar sin tur.
     * Byter tur i spelläget, broadcastar det nya spelläget och skickar
     * YOUR_TURN till nästa spelare.
     *
     * Metoden är synchronized för att säkerställa att turbyten
     * inte kan ske samtidigt från olika trådar.
     *
     * @param playerName namnet på spelaren som avslutar sin tur
     * @author Leo
     */
    public synchronized void handleEndTurn(String playerName) {
        gameState.switchTurn();
        String json = gson.toJson(gameState);
        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, json, ""));

        // Skicka YOUR_TURN enbart till den spelare vars tur det nu är
        String nextPlayerName = gameState.getCurrentPlayer().getName();
        sendToPlayer(nextPlayerName, new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));
    }

    /**
     * Hittar spelarnamnet för en given roll via playerRoles kartan.
     * Används för att ta reda på exempelvis vad PLAYER_ONE heter.
     *
     * @param role rollen att söka efter, PLAYER_ONE eller PLAYER_TWO
     * @return spelarens namn, eller tom sträng om rollen inte hittas
     * @author Leo
     */
    private String getPlayerNameByRole(PlayerID role) {
        return playerRoles.entrySet().stream()
                .filter(e -> e.getValue() == role)
                .map(Map.Entry::getKey)
                .findFirst().orElse("");
    }


    /**
     * Skickar ett meddelande till alla anslutna spelare.
     * Används när något händer som båda spelare ska veta om,
     * exempelvis ett nytt spelläge efter ett drag.
     *
     * @param msg meddelandet som ska skickas till alla
     * @author Leo
     */
    public void broadcast(GameMessage msg) {
        String json = gson.toJson(msg);
        players.values().forEach(out -> out.println(json));
    }

    /**
     * Skickar ett meddelande till en specifik spelare.
     * Används när bara en spelare ska notifieras, exempelvis YOUR_TURN.
     *
     * @param steamId namnet på spelaren som ska ta emot meddelandet
     * @param msg     meddelandet som ska skickas
     * @author Leo
     */
    public void sendToPlayer(String steamId, GameMessage msg) {
        PrintWriter out = players.get(steamId);
        if (out != null) out.println(gson.toJson(msg));
    }

    /**
     * @return uppslagstabellen med alla anslutna spelare och deras strömmar
     */
    public Map<String, PrintWriter> getPlayers() {
        return players;
    }
}