package com.yourgame.network;

public class NetworkMessage {

    private final MessageType type;
    private final String payload;

    public NetworkMessage(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public String serialize() {
        return type.name() + "|" + payload;
    }

    public static NetworkMessage deserialize(String raw) {
        String[] parts = raw.split("\\|", 2);
        MessageType type = MessageType.valueOf(parts[0]);
        String payload = parts.length > 1 ? parts[1] : "";
        return new NetworkMessage(type, payload);
    }
}