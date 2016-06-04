package game.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import server.messaging.Message;
import server.messaging.MessageResponse;
import server.messaging.MessageService;
import server.messaging.MessageType;

import java.io.IOException;

/**
 * created: 6/4/2016
 * package: game.services
 */
public class ResourceService {
    public static class MESSAGES {
        public static final String GET_RESOURCES_REQUEST = "Resources.Get.Request";
        public static final String GET_RESOURCES_RESPONSE = "Resources.Get.Response";
    }
    private static Logger LOGGER = LogManager.getLogger();

    private final MessageService messageService;
    private final GameCardService gameCardService;

    public ResourceService(@NotNull MessageService messageService, @NotNull GameCardService gameCardService) {
        this.messageService = messageService;
        this.gameCardService = gameCardService;
    }

    public void configure() {
        this.messageService.subscribe(MESSAGES.GET_RESOURCES_REQUEST, (sender, message)->{
            final Gson gson = new Gson();
            final JsonElement obj = gson.toJsonTree(gameCardService.getModels());
            LOGGER.info("Sent object: %s", obj.toString());
            try {
                this.messageService.sendMessage(sender, new MessageResponse(MessageType.RESOURCES, MESSAGES.GET_RESOURCES_RESPONSE, obj));
            } catch (IOException e) {
                LOGGER.warn(String.format("[ W ] Error in sending response to resource get: %n%s", e.toString()));
            };
        });

    }
}
