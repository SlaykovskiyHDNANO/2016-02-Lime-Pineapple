package server.messaging;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * created: 5/26/2016
 * package: server.messaging
 */
public class Message implements Serializable {
    @NotNull
    final MessageType type;
    @NotNull
    final String name;

    @NotNull
    String dataString = "";

    @NotNull
    public String getDataString() {
        return dataString;
    }

    public void setDataString(@NotNull String dataString) {
        this.dataString = dataString;
    }

    @NotNull
    public MessageType getType() {
        return type;
    }

    @NotNull
    public String getName() {
        return name;
    }


    @Nullable
    public Object getData() {
        return null;
    }

    public Message(@NotNull MessageType type, @NotNull String name) {
        this.type = type;
        this.name = name;
    }

    public Message(Message message) {
        this.type = message.type;
        this.name = message.name;
    }

    @Nullable
    public JsonElement serializeData() {
        return null;
    }

    public final String serialize() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", this.type.ordinal());
        jsonObject.addProperty("name", this.name);
        jsonObject.add("data", this.serializeData());
        return jsonObject.toString();
    }

    public static Message deserializeToGenericMessage(String data) {
        final JsonParser parser = new JsonParser();
        final JsonObject obj = parser.parse(data).getAsJsonObject();
        final MessageType type = MessageType.values()[obj.get("type").getAsInt()];
        final String messageName = obj.get("name").getAsString();
        final Message message = new Message(type, messageName);
        message.dataString = obj.get("data").toString();
        return message;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        final Message message = (Message) o;

        if (getType() != message.getType()) return false;
        if (!getName().equals(message.getName())) return false;
        return getDataString().equals(message.getDataString());

    }

    @Override
    public int hashCode() {
        int result = getType().hashCode();
        //result = 31 * result + getName().hashCode();
        result = 31 * result + getDataString().hashCode();
        return result;
    }
}
