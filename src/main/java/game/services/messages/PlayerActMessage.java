package game.services.messages;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import server.messaging.Message;
import server.messaging.MessageDeserializer;
import server.messaging.MessageType;

/**
 * created: 6/1/2016
 * package: game.services.messages
 */
public class PlayerActMessage extends Message {
    public static final String MESSAGE_NAME = "Game.Player.Act";

    final PlayerActData data;

    public PlayerActMessage(Message message, PlayerActData data) {
        super(message);
        this.data = data;
    }

    public PlayerActMessage(@NotNull String name, @NotNull PlayerActData data) {
        super(MessageType.GAME, name);
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }

    @SuppressWarnings("PublicField")
    public static class PlayerActData {
        // Эти три параметра нужны при placedCard = true
        public Long playerCardId;
        public Integer rowIndex;
        public Integer columnIndex;


        public Boolean activatedBossCard;
        public Boolean skippedTurn;
        public Boolean placedCard;

    }

    public static class Deserializer implements MessageDeserializer{
        @Override
        public void registerSubDeserializer(@NotNull String messageName, @NotNull MessageDeserializer deserializer) throws RuntimeException {
            throw new RuntimeException("Method not available");
        }

        @NotNull
        @Override
        public Message deserialize(Message message) {
            // try to deserialize
            final PlayerActData playerActData = new Gson().fromJson(message.getDataString(), PlayerActData.class);
            return new PlayerActMessage(message, playerActData);
        }
    }


    @Nullable
    @Override
    public JsonElement serializeData() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerCardId", this.data.placedCard);
        jsonObject.addProperty("rowIndex", this.data.rowIndex);
        jsonObject.addProperty("columnIndex", this.data.columnIndex);
        jsonObject.addProperty("activatedBossCard", this.data.activatedBossCard);
        jsonObject.addProperty("skippedTurn", this.data.skippedTurn);
        jsonObject.addProperty("placedCard",this.data.placedCard);
        return jsonObject;
    }
}
