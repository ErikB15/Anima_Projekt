package Network;

import Model.*;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.*;

/**
 * Spelservern som körs på hostens dator.
 * Ansvarar för att ta emot spelaranslutningar, köra draft-fasen,
 * och sedan hantera kommunikationen under spelets gång.
 *
 * Servern är den enda källan till sanning om spelläget.
 * Klienterna skickar handlingar (DRAFT_PICK, PLAY_CARD, END_TURN)
 * och servern broadcastar det uppdaterade spelläget till båda.
 *
 * @author Leo
 */
public class GameServer {
    private static final int PORT = 5555;
    private final Gson gson = new Gson();

    /** Det auktoritativa spelläget. Skapas när båda spelarna anslutit. */
    private GameState gameState;

    /** Kopplar spelarnamn till deras roll (PLAYER_ONE / PLAYER_TWO). */
    private final Map<String, PlayerID> playerRoles = new LinkedHashMap<>();

    /** Kopplar spelarnamn till deras nätverksström. */
    private final Map<String, PrintWriter> players = new LinkedHashMap<>();

    /**
     * Startar servern och väntar på att två spelare ska ansluta.
     * @author Leo
     */
    public void start() {
        System.out.println("Server startar på port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (players.size() < 2) {
                Socket socket = serverSocket.accept();
                System.out.println("Spelare anslöt!");
                new Thread(new PlayerHandler(socket, this)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registrerar en spelare när de skickat JOIN.
     * Första spelaren får WAITING och väntar.
     * Andra spelaren triggar draft-fasen: ett gemensamt kortpool skapas,
     * spelläget broadcastas och en spelare tilldelas DRAFT_TURN.
     *
     * @param name spelarens namn
     * @param out  nätverksströmmen till spelaren
     * @author Leo
     */
    public synchronized void registerPlayer(String name, PrintWriter out) {
        players.put(name, out);

        if (players.size() == 1) {
            playerRoles.put(name, PlayerID.PLAYER_ONE);
            sendToPlayer(name, new GameMessage(GameMessage.Type.WAITING, "", ""));

        } else {
            playerRoles.put(name, PlayerID.PLAYER_TWO);

            String p1Name = getPlayerNameByRole(PlayerID.PLAYER_ONE);
            Player p1 = new Player(p1Name);
            Player p2 = new Player(name);

            // Skapa spelläget med en gemensam kortpool (draft fas)
            gameState = new GameState(p1, p2, new Board());
            gameState.setDraftPool(createAllCards());
            gameState.setPhase(GamePhase.DRAFT);

            // Slumpa vem som väljer kort först i draften
            PlayerID firstDrafter = Math.random() < 0.5 ? PlayerID.PLAYER_ONE : PlayerID.PLAYER_TWO;
            gameState.setFirstDraftPlayer(firstDrafter);
            gameState.setCurrentDraftPlayer(firstDrafter);

            // Berätta för varje spelare vilken roll de har
            sendToPlayer(p1Name, new GameMessage(GameMessage.Type.GAME_START, "PLAYER_ONE", ""));
            sendToPlayer(name,   new GameMessage(GameMessage.Type.GAME_START, "PLAYER_TWO", ""));

            // Broadcast spelläget (med draft-pool) så båda ser korten
            broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));

            // Säg vem som väljer kort först
            String firstDrafterName = getPlayerNameByRole(firstDrafter);
            sendToPlayer(firstDrafterName, new GameMessage(GameMessage.Type.DRAFT_TURN, "", ""));
        }
    }

    /**
     * Hanterar när en spelare väljer ett kort i draft-fasen.
     * Kortet tas bort från poolen och läggs i spelarens kortlek.
     * Om poolen är tom startas spelfasen, annars skickas DRAFT_TURN till nästa spelare.
     *
     * @param playerName spelarens namn
     * @param cardIdStr  id på kortet som valts (som sträng)
     * @author Leo
     */
    public synchronized void handleDraftPick(String playerName, String cardIdStr) {
        if (gameState == null || gameState.getPhase() != GamePhase.DRAFT) return;

        int cardId;
        try { cardId = Integer.parseInt(cardIdStr); }
        catch (NumberFormatException e) { return; }

        PlayerID playerRole = playerRoles.get(playerName);
        if (playerRole == null) return;

        // Kolla att det är rätt spelares tur att välja
        if (gameState.getCurrentDraftPlayerId() != playerRole) return;

        // Hitta kortet i draft-poolen
        Card chosen = null;
        for (Card c : gameState.getDraftPool()) {
            if (c.getCardID() == cardId) { chosen = c; break; }
        }
        if (chosen == null) return;

        // Lägg kortet i spelarens kortlek och ta bort ur poolen
        Player picker = (playerRole == PlayerID.PLAYER_ONE)
                ? gameState.getPlayerOne()
                : gameState.getPlayerTwo();
        picker.addCardToDeck(chosen);
        gameState.getDraftPool().remove(chosen);

        if (gameState.getDraftPool().isEmpty()) {
            // Draft klar → dela ut kort och starta spelfasen
            gameState.getPlayerOne().drawUntilHandIsFull();
            gameState.getPlayerTwo().drawUntilHandIsFull();

            // Den som INTE startade draften börjar spelfasen
            PlayerID firstPlayPlayer = (gameState.getFirstDraftPlayer() == PlayerID.PLAYER_ONE)
                    ? PlayerID.PLAYER_TWO : PlayerID.PLAYER_ONE;
            gameState.setCurrentPlayer(firstPlayPlayer);
            gameState.setPhase(GamePhase.PLAY);

            broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));
            sendToPlayer(getPlayerNameByRole(firstPlayPlayer),
                    new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));

        } else {
            // Byt till nästa spelare i draften
            gameState.switchPlayer();
            broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));
            String nextDrafterName = getPlayerNameByRole(gameState.getCurrentDraftPlayerId());
            sendToPlayer(nextDrafterName, new GameMessage(GameMessage.Type.DRAFT_TURN, "", ""));
        }
    }

    /**
     * Hanterar när en spelare spelar ett kort under spelfasen.
     * Payload: "kortId,brädindex"
     *
     * @param playerName spelarens namn
     * @param payload    "kortId,brädindex"
     * @author Leo
     */
    public synchronized void handlePlayCard(String playerName, String payload) {
        if (gameState == null || gameState.getPhase() != GamePhase.PLAY) return;

        String[] parts = payload.split(",");
        if (parts.length < 2) return;

        int cardId, boardIndex;
        try {
            cardId     = Integer.parseInt(parts[0].trim());
            boardIndex = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) { return; }

        PlayerID playerRole = playerRoles.get(playerName);
        if (playerRole == null) return;

        if (gameState.getCardsPlayedThisTurn() >= gameState.getMaxCardsToPlayPerTurn()) return;

        Player player = (playerRole == PlayerID.PLAYER_ONE)
                ? gameState.getPlayerOne()
                : gameState.getPlayerTwo();

        // Hitta kortet i handen
        Card cardToPlay = null;
        int handIndex   = -1;
        for (int i = 0; i < player.getHand().size(); i++) {
            if (player.getHand().get(i).getCardID() == cardId) {
                cardToPlay = player.getHand().get(i);
                handIndex  = i;
                break;
            }
        }
        if (cardToPlay == null || handIndex == -1) return;

        if (!gameState.getBoard().placeCard(playerRole, boardIndex, cardToPlay)) return;

        player.getHand().remove(handIndex);
        player.takeDamage(cardToPlay.getCardCost());
        cardToPlay.setAsleep(true);
        gameState.setCardsPlayedThisTurn(gameState.getCardsPlayedThisTurn() + 1);
        gameState.checkGameOver();

        if (gameState.isGameOver()) {
            String winnerName = gameState.getWinner() != null ? gameState.getWinner().getName() : "Okänd";
            broadcast(new GameMessage(GameMessage.Type.GAME_OVER, winnerName, ""));
            return;
        }

        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));
    }

    /**
     * Hanterar när en spelare avslutar sin tur.
     *
     * @param playerName spelarens namn
     * @author Leo
     */
    public synchronized void handleEndTurn(String playerName) {
        if (gameState == null || gameState.getPhase() != GamePhase.PLAY) return;

        PlayerID playerRole = playerRoles.get(playerName);
        if (playerRole == null) return;

        gameState.getBoard().wakeUpCardsForPlayer(playerRole);
        gameState.getBoard().resetAttacksForPlayer(playerRole);
        gameState.switchTurn();
        gameState.getCurrentPlayer().drawUntilHandIsFull();

        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));
        sendToPlayer(gameState.getCurrentPlayer().getName(),
                new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));
    }

    /**
     * Skapar en lista med alla 12 spelkort.
     * Används som gemensam draft-pool när spelet startar.
     * Effect är null — effekter hanteras ej på serversidan.
     *
     * @author Leo
     */
    private ArrayList<Card> createAllCards() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("Monkey",    40, 50, 1,  null, "/CardPictures/Card5.png"));
        cards.add(new Card("Bob",       40, 25, 2,  null, "/CardPictures/Card10.png"));
        cards.add(new Card("Twins",     40, 34, 3,  null, "/CardPictures/Card8.png"));
        cards.add(new Card("ChillGuy",  40, 20, 4,  null, "/CardPictures/Card9.png"));
        cards.add(new Card("blockHead", 40, 30, 5,  null, "/CardPictures/Card7.png"));
        cards.add(new Card("Wizard",    40, 40, 6,  null, "/CardPictures/Card6.png"));
        cards.add(new Card("Pernilla",  40, 40, 7,  null, "/CardPictures/Card12.png"));
        cards.add(new Card("Kick",      40, 40, 8,  null, "/CardPictures/Card11.png"));
        cards.add(new Card("George",    40, 40, 9,  null, "/CardPictures/Card4.png"));
        cards.add(new Card("Harrold",   40, 40, 10, null, "/CardPictures/Card3.png"));
        cards.add(new Card("KnifeGuy",  40, 40, 11, null, "/CardPictures/Card2.png"));
        cards.add(new Card("Kenneth",   40, 40, 12, null, "/CardPictures/Card1.png"));
        return cards;
    }

    private String getPlayerNameByRole(PlayerID role) {
        return playerRoles.entrySet().stream()
                .filter(e -> e.getValue() == role)
                .map(Map.Entry::getKey)
                .findFirst().orElse("");
    }

    public void broadcast(GameMessage msg) {
        String json = gson.toJson(msg);
        players.values().forEach(out -> out.println(json));
    }

    public void sendToPlayer(String playerName, GameMessage msg) {
        PrintWriter out = players.get(playerName);
        if (out != null) out.println(gson.toJson(msg));
    }

    public Map<String, PrintWriter> getPlayers() {
        return players;
    }
}
