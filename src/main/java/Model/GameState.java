package Model;

import java.util.List;

public class GameState {
    private List<Player> players;
    private Board board;
    private int currentPlayerIndex;
    private boolean gameOver;
    private int maxCardsToPlayPerTurn;
    private int turnNumber;
}
