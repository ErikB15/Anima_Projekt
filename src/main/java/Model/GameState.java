package Model;

import java.util.ArrayList;

/**
 * Detta är typ hur jag fattat att gameState ska se ut.
 * Det är som att ta en bild på hur planen ser ut "JUST NU"
 * Där finns en helvetes många variabler, jag har försökt at separera dem lite för att göra det enklare.
 * Dem är separerade utifrån saker som ofta hör ihop med varandra.
 *
 * NOTERA. GameState är INTE vad som kontrollerar regler osv, det är endast en "så här ser det ut just nu"
 * Där finns vissa check metoder, detta är mest för att dem kanske blir användbara sen.
 * Blir dem inte det tar vi bara bort dem.
 */
public class GameState {
    private Player playerOne;
    private Player playerTwo;
    private Board board;

    private GamePhase phase;

    private int turnNumber;

    private int maxCardsToPlayPerTurn;
    private int cardsPlayedThisTurn;

    private boolean gameOver;
    private Player winner;

    private ArrayList<Card> draftPool;
    private PlayerID currentPlayer;
    private PlayerID currentDraftPlayer;
    private PlayerID firstDraftPlayer;

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

        this.currentPlayer = null;
        this.turnNumber = 1;

        this.maxCardsToPlayPerTurn = 1;
        this.cardsPlayedThisTurn = 0;

        this.gameOver = false;
        this.winner = null;

        this.draftPool = new ArrayList<>();
    }

    // Ändrar vem som ska välja kort i "draft" fasen.
    public void switchDraftPlayer() {
        currentDraftPlayer = currentDraftPlayer == PlayerID.PLAYER_ONE
                ? PlayerID.PLAYER_TWO
                : PlayerID.PLAYER_ONE;
    }

    // Ska kunna ändra på vilken tur det är.
    public void switchTurn() {
        if (currentPlayer == PlayerID.PLAYER_TWO){
            turnNumber++;
        }
        currentPlayer = currentPlayer == PlayerID.PLAYER_ONE ? PlayerID.PLAYER_TWO : PlayerID.PLAYER_ONE;
        cardsPlayedThisTurn = 0;
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

    public void finishGame(Player winner) {
        this.gameOver = true;
        this.winner = winner;
        this.phase = GamePhase.GAME_OVER;
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

    /**
     * VARNING:
     * Röda havet av getters och setters under, finns nästan inget av intresse.
     * Osäker på hur många av dessa som faktiskt kommer användas men dem finns där tills vi städar upp koden.
     * Jag kommer inte skriva javadoc på alla dessa om det inte krävs. Hoppas inte det krävs.
     * Snälla krävs inte.
     * Om vi skulle behöva röra dem för någon anledning så har dem sorterats enligt variabel typ.
     *
     * @author Jim Ström
     */

    public int getCardsPlayedThisTurn() {return cardsPlayedThisTurn;}

    public int getMaxCardsToPlayPerTurn() {return maxCardsToPlayPerTurn;}

    public int getTurnNumber() {return turnNumber;}

    public Board getBoard() {return board;}

    public ArrayList<Card> getDraftPool() {return draftPool;}

    public GamePhase getPhase() {return phase;}

    public Player getCurrentPlayer() {
        return currentPlayer == PlayerID.PLAYER_ONE ? playerOne : playerTwo;
    }

    public PlayerID getCurrentPlayerId() {
        return currentPlayer;
    }

    public Player getOpponentPlayer() {return currentPlayer == PlayerID.PLAYER_ONE ? playerTwo : playerOne;}

    public Player getPlayerOne() {return playerOne;}

    public Player getPlayerTwo() {return playerTwo;}

    public Player getWinner() {return winner;}

    public Player getCurrentDraftPlayer() {return currentDraftPlayer == PlayerID.PLAYER_ONE ? playerTwo : playerOne;}

    public PlayerID getCurrentDraftPlayerId() {return currentDraftPlayer;}

    public PlayerID getFirstDraftPlayer() {return firstDraftPlayer;}

    public void setBoard(Board board) {this.board = board;}

    public void setCardsPlayedThisTurn(int cardsPlayedThisTurn) {this.cardsPlayedThisTurn = cardsPlayedThisTurn;}

    public void setTurnNumber(int turnNumber) {this.turnNumber = turnNumber;}

    public void setMaxCardsToPlayPerTurn(int maxCardsToPlayPerTurn) {this.maxCardsToPlayPerTurn = maxCardsToPlayPerTurn;}

    public void setCurrentDraftPlayer(PlayerID currentDraftPlayer) {this.currentDraftPlayer = currentDraftPlayer;}

    public void setCurrentPlayer(PlayerID currentPlayer) {this.currentPlayer = currentPlayer;}

    public void setFirstDraftPlayer(PlayerID firstDraftPlayer) {this.firstDraftPlayer = firstDraftPlayer;}

    public void setDraftPool(ArrayList<Card> draftPool) {this.draftPool = draftPool;}

    public void setGameOver(boolean gameOver) {this.gameOver = gameOver;}

    public void setPhase(GamePhase phase) {this.phase = phase;}

    public void setPlayerOne(Player playerOne) {this.playerOne = playerOne;}

    public void setPlayerTwo(Player playerTwo) {this.playerTwo = playerTwo;}

    public void setWinner(Player winner) {this.winner = winner;}
}