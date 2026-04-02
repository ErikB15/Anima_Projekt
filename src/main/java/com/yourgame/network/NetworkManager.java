package com.yourgame.network;

import java.util.function.Consumer;

public interface NetworkManager {

    void initialize();

    void hostLobby();

    void findAndJoinLobby();

    void send(NetworkMessage message);

    void setOnMessageReceived(Consumer<NetworkMessage> handler);

    void setOnConnectionStatusChanged(Consumer<String> handler);

    boolean isHost();

    void shutdown();
}