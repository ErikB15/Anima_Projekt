package Network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import com.google.gson.Gson;
import java.net.Socket;

public class PlayerHandler implements Runnable {
    private final Socket socket;
    private final GameServer server;
    private final Gson gson = new Gson();
    private PrintWriter out;
    private String steamId;
    private String displayName;

    public PlayerHandler(Socket socket, GameServer server){
        this.socket = socket;
        this.server =  server;
    }

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

    private void handleMessage(GameMessage msg) {
        System.out.println("Server got " + msg);

        switch (msg.getType()) {
            case JOIN -> {
                this.steamId = msg.getSteamId();
                server.registerPlayer(steamId, out);
            }
            case PLAY_CARD -> {
                server.handlePlayCard(steamId, msg.getPayload());
                server.broadcast(new GameMessage(
                        GameMessage.Type.GAME_STATE, msg.getPayload(), steamId));
            }
            case END_TURN -> {
                server.handleEndTurn(steamId);
                server.broadcast(new GameMessage(
                        GameMessage.Type.YOUR_TURN, "", "server"));
            }
            case CHAT -> {
                server.broadcast(new GameMessage(GameMessage.Type.CHAT, msg.getPayload(), steamId));
            }
        }
    }

}
