package server.rest;

import db.services.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1/")
public class RestAppV1 extends Application {
    private static final Logger LOGGER = LogManager.getLogger(RestAppV1.class);
    AccountService accountService;
    final Set<Object> objects = new HashSet<>();
    server.Context context;
    @Context
    ServletContext ctx;

    @Override
    public Set<Object> getSingletons() {
        context = (server.Context)ctx.getAttribute("context");
        accountService = context.get(AccountService.class);
        LOGGER.info("[+] Started application...");
        objects.add(new UserServlet(accountService));
        objects.add(new SessionServlet(accountService));
        //objects.add(new SignInServlet(accountService));
        return objects;
    }
}
