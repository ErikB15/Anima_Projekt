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
            // Draft klar, dela ut kort och starta spelfasen
            gameState.getPlayerOne().drawUntilHandIsFull();
            gameState.getPlayerTwo().drawUntilHandIsFull();

            // Den som INTE startade draften börjar spelfasen
            PlayerID firstPlayPlayer = (gameState.getFirstDraftPlayer() == PlayerID.PLAYER_ONE)
                    ? PlayerID.PLAYER_TWO : PlayerID.PLAYER_ONE;
            gameState.setCurrentPlayer(firstPlayPlayer);
            gameState.setPhase(GamePhase.PLAY);

            broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));
            broadcast(new GameMessage(GameMessage.Type.CHAT, "Draft phase done, game starting!", ""));
            sendToPlayer(getPlayerNameByRole(firstPlayPlayer),
                    new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));

        } else {
            // switchPlayer i GameState är buggad efter merge, ändrar currentPlayer istället för currentDraftPlayer
            // Vi sätter draft, spelaren direkt här istället.
            PlayerID nextDrafter = (gameState.getCurrentDraftPlayerId() == PlayerID.PLAYER_ONE)
                    ? PlayerID.PLAYER_TWO : PlayerID.PLAYER_ONE;
            gameState.setCurrentDraftPlayer(nextDrafter);
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
            cardId = Integer.parseInt(parts[0].trim());
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
        int handIndex = -1;
        for (int i = 0; i < player.getHand().size(); i++) {
            if (player.getHand().get(i).getCardID() == cardId) {
                cardToPlay = player.getHand().get(i);
                handIndex = i;
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
            String winnerName = gameState.getWinner() != null ? gameState.getWinner().getName() : "Unknown";
            broadcast(new GameMessage(GameMessage.Type.GAME_OVER, winnerName, ""));
            return;
        }

        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));
        broadcast(new GameMessage(GameMessage.Type.CHAT,
                playerName + " has placed down " + cardToPlay.getCardName(), ""));
        broadcast(new GameMessage(GameMessage.Type.CHAT, "___________________________", ""));
    }


    /**
     * Hanterar när en spelare attackerar ett av motståndarens kort.
     * Payload: "attackerIndex,defenderIndex"
     *
     * Kör samma logik som GameController.attackCard() men på serverns gameState:
     * båda korten tar skada, döda kort skickas till graveyard, GAME_STATE broadcastas.
     *
     * @param playerName spelarens namn
     * @param payload    "attackerIndex,defenderIndex"
     * @author Leo
     */
    public synchronized void handleAttackCard(String playerName, String payload) {
        if (gameState == null || gameState.getPhase() != GamePhase.PLAY) return;

        String[] parts = payload.split(",");
        if (parts.length < 2) return;

        int attackerIndex, defenderIndex;
        try {
            attackerIndex = Integer.parseInt(parts[0].trim());
            defenderIndex = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) { return; }

        PlayerID attackerRole = playerRoles.get(playerName);
        if (attackerRole == null) return;
        // Kolla att det faktiskt är denna spelares tur
        if (gameState.getCurrentPlayerId() != attackerRole) return;

        PlayerID defenderRole = (attackerRole == PlayerID.PLAYER_ONE)
                ? PlayerID.PLAYER_TWO : PlayerID.PLAYER_ONE;

        Board board = gameState.getBoard();
        Card[] attackerSlots = board.getSlotsForPlayer(attackerRole);
        Card[] defenderSlots = board.getSlotsForPlayer(defenderRole);

        if (attackerIndex < 0 || attackerIndex >= attackerSlots.length) return;
        if (defenderIndex < 0 || defenderIndex >= defenderSlots.length) return;

        Card attacker = attackerSlots[attackerIndex];
        Card defender = defenderSlots[defenderIndex];

        if (attacker == null || defender == null) return;
        if (attacker.getAsleep()) return;
        if (attacker.getHasAttackedThisTurn()) return;

        // Båda korten tar skada
        defender.takeDamage(attacker.getCardAD());
        attacker.takeDamage(defender.getCardAD());
        attacker.setHasAttackedThisTurn(true);

        // Döda kort hamnar i graveyard
        Player defenderPlayer = (defenderRole == PlayerID.PLAYER_ONE)
                ? gameState.getPlayerOne() : gameState.getPlayerTwo();
        Player attackerPlayer = (attackerRole == PlayerID.PLAYER_ONE)
                ? gameState.getPlayerOne() : gameState.getPlayerTwo();

        if (defender.isDead()) {
            Card dead = board.removeCard(defenderRole, defenderIndex);
            defenderPlayer.sendCardToGraveyard(dead);
        }
        if (attacker.isDead()) {
            Card dead = board.removeCard(attackerRole, attackerIndex);
            attackerPlayer.sendCardToGraveyard(dead);
        }

        gameState.checkGameOver();
        if (gameState.isGameOver()) {
            String winnerName = gameState.getWinner() != null ? gameState.getWinner().getName() : "Unknown";
            broadcast(new GameMessage(GameMessage.Type.GAME_OVER, winnerName, ""));
            return;
        }

        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));
        broadcast(new GameMessage(GameMessage.Type.CHAT,
                attacker.getCardName() + " has attacked " + defender.getCardName()
                        + " for " + attacker.getCardAD() + " damage", ""));
        broadcast(new GameMessage(GameMessage.Type.CHAT, "___________________________", ""));
    }

    /**
     * Hanterar när en spelare attackerar motståndaren direkt.
     * Payload: "attackerIndex"
     *
     * Motståndarens HP tar skada. Om HP når 0 slutar spelet.
     *
     * @param playerName spelarens namn
     * @param payload    "attackerIndex"
     * @author Leo
     */
    public synchronized void handleAttackPlayer(String playerName, String payload) {
        if (gameState == null || gameState.getPhase() != GamePhase.PLAY) return;

        int attackerIndex;
        try { attackerIndex = Integer.parseInt(payload.trim()); }
        catch (NumberFormatException e) { return; }

        PlayerID attackerRole = playerRoles.get(playerName);
        if (attackerRole == null) return;
        if (gameState.getCurrentPlayerId() != attackerRole) return;

        PlayerID defenderRole = (attackerRole == PlayerID.PLAYER_ONE)
                ? PlayerID.PLAYER_TWO : PlayerID.PLAYER_ONE;

        Card[] attackerSlots = gameState.getBoard().getSlotsForPlayer(attackerRole);
        if (attackerIndex < 0 || attackerIndex >= attackerSlots.length) return;

        Card attacker = attackerSlots[attackerIndex];
        if (attacker == null) return;
        if (attacker.getAsleep()) return;
        if (attacker.getHasAttackedThisTurn()) return;

        Player defenderPlayer = (defenderRole == PlayerID.PLAYER_ONE)
                ? gameState.getPlayerOne() : gameState.getPlayerTwo();

        defenderPlayer.takeDamage(attacker.getCardAD());
        attacker.setHasAttackedThisTurn(true);

        gameState.checkGameOver();
        if (gameState.isGameOver()) {
            String winnerName = gameState.getWinner() != null ? gameState.getWinner().getName() : "Unknown";
            broadcast(new GameMessage(GameMessage.Type.GAME_OVER, winnerName, ""));
            return;
        }

        broadcast(new GameMessage(GameMessage.Type.GAME_STATE, gson.toJson(gameState), ""));
        broadcast(new GameMessage(GameMessage.Type.CHAT,
                attacker.getCardName() + " has attacked " + defenderPlayer.getName()
                        + " straight to the face for " + attacker.getCardAD() + " damage!", ""));
        broadcast(new GameMessage(GameMessage.Type.CHAT, "___________________________", ""));
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
        broadcast(new GameMessage(GameMessage.Type.CHAT, playerName + " has ended their turn!", ""));
        broadcast(new GameMessage(GameMessage.Type.CHAT, "___________________________", ""));
        sendToPlayer(gameState.getCurrentPlayer().getName(),
                new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));
        sendToPlayer(gameState.getCurrentPlayer().getName(),
                new GameMessage(GameMessage.Type.YOUR_TURN, "", ""));
    }

    /**
     /**
     * Skapar listan med alla 12 kort som ingår i draft poolen.
     * MÅSTE matcha GameController.addAllCards() exakt, annars får klienterna
     * fel kortvärden från servern jämfört med vad GUI är förberedd att rendera.
     *
     * Effect fältet sätts till null på serversidan eftersom servern inte
     * kör effekter (de hanteras på klientsidan om alls).
     *
     * @return en ny ArrayList med 12 kort i id ordning 1-12
     * @author Leo
     */
    private ArrayList<Card> createAllCards() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("Kenneth",   10, 15, 1,"/CardPictures/Card1.png"));
        cards.add(new Card("KnifeGuy",  13, 12, 2,"/CardPictures/Card2.png"));
        cards.add(new Card("Harrold",   1,  30, 3,"/CardPictures/Card3.png"));
        cards.add(new Card("George",    5,  20, 4,"/CardPictures/Card4.png"));
        cards.add(new Card("Monkey",    30, 5,  5,"/CardPictures/Card5.png"));
        cards.add(new Card("Wizard",    30, 5,  6,"/CardPictures/Card6.png"));
        cards.add(new Card("blockHead", 1,  35, 7,"/CardPictures/Card7.png"));
        cards.add(new Card("Twins",     10, 17, 8,"/CardPictures/Card8.png"));
        cards.add(new Card("ChillGuy",  5,  22, 9,"/CardPictures/Card9.png"));
        cards.add(new Card("Bob",       15, 8,  10,"/CardPictures/Card10.png"));
        cards.add(new Card("Kick",      13, 13, 11,"/CardPictures/Card11.png"));
        cards.add(new Card("Pernilla",  2,  28, 12,"/CardPictures/Card12.png"));
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
