package server.messaging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * created: 6/4/2016
 * package: server.messaging
 */
public class MessageResponse extends Message {
    final JsonElement jsonElement;

    public MessageResponse(@NotNull MessageType type, @NotNull String name, @NotNull JsonElement jsonElement) {
        super(type, name);
        this.jsonElement = jsonElement;
    }

    @Nullable
    @Override
    public JsonElement serializeData() {
        return jsonElement;
    }
}
