package server.messaging.messages;

import org.jetbrains.annotations.NotNull;
import server.messaging.Message;
import server.messaging.MessageType;

/**
 * created: 5/31/2016
 * package: game.services.messages
 */
public class SystemMessage extends Message {
    public static class MESSAGES {
        public static final String CLIENT_DISCONNECTED = "System.Client.Disconnected";
        public static final String CLIENT_CONNECTED = "System.Client.Connected";

    }
    public SystemMessage(@NotNull String name) {
        super(MessageType.SYSTEM, name);
    }
}
