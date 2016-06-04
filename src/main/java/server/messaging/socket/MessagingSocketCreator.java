package server.messaging.socket;

/**
 * created: 5/25/2016
 * package: server.messaging
 */
import db.exceptions.DatabaseException;
import db.services.AccountService;
import game.PlayingUser;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.jetbrains.annotations.Nullable;
import server.Context;
import server.messaging.Client;
import server.messaging.MessageService;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.net.HttpCookie;
//import java.rmi.AccessException;
import java.util.List;
import java.util.Map;

public class MessagingSocketCreator implements WebSocketCreator
{
    static final Logger LOGGER = LogManager.getLogger();

    final MessageService service;
    final AccountService accountService;
    final Boolean cookieAuthorization;

    public MessagingSocketCreator(Context context)
    {
        // Create the reusable sockets
        this.service = context.get(MessageService.class);
        this.accountService = context.get(AccountService.class);

        final Configuration props = context.get(Configuration.class);
        this.cookieAuthorization = props.getBoolean("websockets.cookieAuthorization", true);
    }

    @Nullable
    protected Client isAuthorized(ServletUpgradeRequest req) {
        LOGGER.info(String.format("Websocket neeeds to be authorized: %b", this.cookieAuthorization));
        if (!this.cookieAuthorization) {
            LOGGER.warn("[ W ] We don't know what to do with unauthorized connections!");
            return null;
        }

        final List<String> sessions = req.getServletParameters().get("session");
        LOGGER.info("[ I ] Getting authorization info");
        for (String sessionStr : sessions) {
            // TODO: "id" label should be in settings or context
            try {
                final Long id = Long.decode(sessionStr);
                final PlayingUser playingUser = new PlayingUser(accountService.getUser(id));
                final Client client = new Client(playingUser);

                if (id > 0) {
                    LOGGER.info("[ I ] WebSocket authorized. Getting user by id");
                    return client;
                }
            } catch (NumberFormatException e) {
                LOGGER.warn("[ I ] Error when trying to convert user id from cookie");
            } catch (DatabaseException e) {
                LOGGER.warn("[ W ] Couldn't get user form cookie!");
            }
        }
        LOGGER.info("[ I ] WebSocket not authorized.");
        return null;
    }

    @Override
    @Nullable
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp)
    {
        final Client authClient = isAuthorized(req);
        if (authClient != null) {
            for (String subprotocol : req.getSubProtocols())
            {
                if ("text".equals(subprotocol))
                {
                    resp.setAcceptedSubProtocol(subprotocol);
                    return new MessageSocket(service, authClient);
                }
            }

            // No valid subprotocol in request, ignore the request
            return null;
        }
        else {
            try {
                resp.sendForbidden("Not authorized");
            } catch (IOException e) {
                LOGGER.warn(String.format("[E] ERROR while sending Forbidden response%n%s", e.toString()));
            }
            return null;
        }
    }
}
