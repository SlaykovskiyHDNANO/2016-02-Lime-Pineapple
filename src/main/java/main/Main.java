package main;

import db.models.game.cards.CardFactory;
import db.services.AccountService;
import db.services.CardService;
import db.services.impl.db.AccountDAO;
import db.services.impl.db.DBAccountServiceImpl;
import db.services.impl.db.DBSessionFactoryService;
import game.services.GameCardService;
import game.services.GameEngineService;
import game.services.GameFieldFactoryService;
import game.services.MatchmakingService;
import game.services.messages.GameMessageDeserializer;
import server.messaging.MessageService;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import server.Context;
import server.rest.RestAppV1;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import server.messaging.socket.MessagingServlet;

//import javax.ws.rs.core.Application;
//import java.io.FileInputStream;
//import java.io.IOException;
import java.math.MathContext;
import java.net.InetSocketAddress;
//import java.util.Properties;

import static java.lang.Integer.parseInt;

import static java.lang.Integer.parseInt;


public class Main {
    static final Logger LOGGER = LogManager.getLogger();

    public static final int DEFAULT_PORT = 9999;
    public static final String DEFAULT_PROP_PATH = "cfg/server.properties";

    @NotNull
    static Configuration loadProperties(Context serverContext) throws Throwable {
        LOGGER.info(String.format("[ I ] Loading properties from path %s", DEFAULT_PROP_PATH));
        Configuration props = null;
        Throwable savedThrowable = null;
        final Configurations configs = new Configurations();
        try {
            props = configs.properties(DEFAULT_PROP_PATH);
        } catch (ConfigurationException e) {
            LOGGER.error(String.format("[ E ] Something happened during configuration load:\n%s", e.toString()));
            savedThrowable = e;
        }

        if (props == null) {
            // rethrow exception
            throw savedThrowable;
        }
        LOGGER.info("[ I ] Loaded properties successfully!");
        serverContext.put(Configuration.class, props);
        return props;
    }



    @Nullable
    static Server initializeServer(Context serverContext) {
        //final Configuration srvConfig=new Configuration("fdvdf");
        final Configuration serverProps;
        try {
            serverProps = loadProperties(serverContext);
        } catch (Throwable e) {
            LOGGER.error(String.format("[ E ] Error when loading properties: %n%s", e.toString()));
            return null;
        }
        final String address = serverProps.getString("server.address");
        final String portString = serverProps.getString("server.port");
        int port = 0;
        try {
            port = parseInt(portString);
        } catch (NumberFormatException ignored) {

        }
        if (port<=0) {
            port = DEFAULT_PORT;
            LOGGER.warn(String.format("[ W ] Port is not specified or is not valid. Default port - %d is used.", DEFAULT_PORT));
        }
        LOGGER.info("[ I ] Building server full endpoint address from configuration...");
        final InetSocketAddress addr = new InetSocketAddress(port);//не указывай адрес, если хочешь видимость по сети
        LOGGER.info("[ I ] Built successfully completed!");
        LOGGER.info(String.format("Address after configuration: http://%s:%d", address,port));
        return new Server(addr);
    }

    @NotNull
    static ServletContextHandler initializeContextHandler(Context restContext) {
        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        contextHandler.setAttribute(Context.CONTEXT_KEY,restContext);
        return contextHandler;
    }

    @NotNull
    static ResourceHandler initializeResourceHandler() {
        // Static resource servlet
        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("static");
        return resourceHandler;
    }

    static void configureRestApi(@NotNull Context serverContext,
                                 @NotNull ServletContextHandler contextHandler) {
        final ResourceConfig application = new RestAppV1(serverContext)
                .register(JacksonFeature.class);
        final ServletHolder servletHolder = new ServletHolder(new ServletContainer(application));
        //add our Context to ServletContextHandler
        contextHandler.addServlet(servletHolder,"/api/v1/*");
    }



    static AccountService configureAccountService(@NotNull  Context serverContext) {
        final DBSessionFactoryService factory = new DBSessionFactoryService();
        factory.configure();
        final AccountService service = new DBAccountServiceImpl(factory, new AccountDAO());
        serverContext.put(AccountService.class , service);
        serverContext.put(DBSessionFactoryService.class, factory);
        return service;
    }
    // TODO: Rename it to "configureGame"
    static void configureGame(@NotNull Context serverContext,
                              @NotNull ServletContextHandler contextHandler,
                              @NotNull AccountService accountService) {
        final MessageService messageService = new MessageService();
        serverContext.put(MessageService.class, messageService);

        final GameMessageDeserializer gameMessageDeserializer = new GameMessageDeserializer();
        final GameCardService gameCardService = new GameCardService(new CardFactory());
        final GameFieldFactoryService gameFieldFactoryService = new GameFieldFactoryService(gameCardService);
        final GameEngineService gameServer = new GameEngineService(gameMessageDeserializer, messageService, accountService, gameCardService, gameFieldFactoryService);
        gameServer.configure();
        final MatchmakingService matchmakingService = new MatchmakingService(gameCardService, messageService);
        matchmakingService.configure();
        final ServletHolder holderSockets = new ServletHolder("ws-events", MessagingServlet.class);
        contextHandler.addServlet(holderSockets, "/sockets/*");
    }


    static void configureServer(Handler[] handlers, Server server) {
        final HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(handlers);
        server.setHandler(handlerList);
    }

    public static void main(String[] args) throws Exception {

        final Context serverContext = new Context();
        final Server server = initializeServer(serverContext);
        if (server == null)
            return;

        final ResourceHandler resourceHandler = initializeResourceHandler();
        final ServletContextHandler contextHandler = initializeContextHandler(serverContext);

        final AccountService accountService = configureAccountService(serverContext);
        configureGame(serverContext, contextHandler, accountService);
        configureRestApi(serverContext, contextHandler);
        configureServer(new Handler[]{resourceHandler, contextHandler}, server);

        try
        {
            LOGGER.info("[ I ] Starting server...");
            server.start();
            // uncomment this to see current Server dump (used modules, stats, etc)
            // srv.dump(System.err);
            server.join();
        }
        catch (InterruptedException t)
        {
            LOGGER.error(String.format("[ E ] Critical error in server work:%n%s", t.toString()));
        }
    }

}