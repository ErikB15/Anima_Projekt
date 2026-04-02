package com.yourgame.network;

import java.util.function.Consumer;

public class LocalMockNetworkManager implements NetworkManager {

    private Consumer<NetworkMessage> onMessageReceived;
    private Consumer<String> onStatusChanged;
    private boolean host;

    @Override
    public void initialize() {
        if (onStatusChanged != null) {
            onStatusChanged.accept("Mock network initialized");
        }
    }

    @Override
    public void hostLobby() {
        host = true;
        if (onStatusChanged != null) {
            onStatusChanged.accept("Mock host lobby created");
        }
    }

    @Override
    public void findAndJoinLobby() {
        host = false;
        if (onStatusChanged != null) {
            onStatusChanged.accept("Mock joined lobby");
        }
    }

    @Override
    public void send(NetworkMessage message) {
        System.out.println("Mock send: " + message.serialize());

        if (onMessageReceived != null) {
            onMessageReceived.accept(message);
        }
    }

    @Override
    public void setOnMessageReceived(Consumer<NetworkMessage> handler) {
        this.onMessageReceived = handler;
    }

    @Override
    public void setOnConnectionStatusChanged(Consumer<String> handler) {
        this.onStatusChanged = handler;
    }

    @Override
    public boolean isHost() {
        return host;
    }

    @Override
    public void shutdown() {
        if (onStatusChanged != null) {
            onStatusChanged.accept("Mock network shutdown");
        }
    }
}