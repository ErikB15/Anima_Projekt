package Model;

import java.util.ArrayList;

/**
 * Detta är typ hur jag fattat att gameState ska se ut.
 * Det är som att ta en bild på hur planen ser ut "JUST NU"
 * Där finns en helvetes många variabler, jag har försökt at separera dem lite för att göra det enklare.
 * Dem är separerade utifrån saker som ofta hör ihop med varandra.
 */
public class GameState {
    private Player playerOne;
    private Player playerTwo;
    private Board board;

    private GamePhase phase;

    private int currentPlayerIndex; // 0 = playerOne, 1 = playerTwo
    private int turnNumber;

    private int maxCardsToPlayPerTurn;
    private int cardsPlayedThisTurn;

    private boolean gameOver;
    private Player winner;

    private ArrayList<Card> draftPool;
    private int currentDraftPlayerIndex;
    private int firstDraftPlayerIndex;

    /**
     * Poängen här är att det ska skapas ett GameState när spelet startar.
     * Därför är alla värden just nu "standard" värden som spelet har när det börjar.
     * @param playerOne - Spelare ett
     * @param playerTwo - Spelare två
     * @param board - Själva "board"
     */
    public GameState(Player playerOne, Player playerTwo, Board board) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = board;

        this.phase = GamePhase.DRAFT;

        this.currentPlayerIndex = 0;
        this.turnNumber = 1;

        this.maxCardsToPlayPerTurn = 1;
        this.cardsPlayedThisTurn = 0;

        this.gameOver = false;
        this.winner = null;

        this.draftPool = new ArrayList<>();
    }

    // Ska kunna ändra på vilken tur det är.
    public void switchTurn() {
        currentPlayerIndex = currentPlayerIndex == 0 ? 1 : 0;
        cardsPlayedThisTurn = 0;
        turnNumber++;

        updateMaxCardsToPlayPerTurn();
    }

    // Det ska vara denna metoden som har koll på hur många kort en spelare kan spela.
    private void updateMaxCardsToPlayPerTurn() {
        // Kommer inte ihåg exakt hur värdena skulle vara nu igen!
        if (turnNumber >= 9) {
            maxCardsToPlayPerTurn = 3;
        } else if (turnNumber >= 5) {
            maxCardsToPlayPerTurn = 2;
        } else {
            maxCardsToPlayPerTurn = 1;
        }
    }

    // Egentligen bara en enkel check för att se om spelet är över.
    public boolean isGameOver() {
        return gameOver;
    }

    // Kollar om spelet är över och sätter sedan variablerna till vad dem bör vara.
    public void checkGameOver() {
        if (playerOne.getHp() <= 0) {
            gameOver = true;
            winner = playerTwo;
            phase = GamePhase.GAME_OVER;
        } else if (playerTwo.getHp() <= 0) {
            gameOver = true;
            winner = playerOne;
            phase = GamePhase.GAME_OVER;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayerIndex == 0 ? playerOne : playerTwo;
    }

    public Player getOpponentPlayer() {
        return currentPlayerIndex == 0 ? playerTwo : playerOne;
    }

    // Kan behövas fler getters och setters, vet inte riktigt hur nöjd jag är med denna GameState än.
}