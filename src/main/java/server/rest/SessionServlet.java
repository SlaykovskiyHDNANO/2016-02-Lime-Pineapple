package server.rest;

import com.google.gson.JsonObject;
import db.exceptions.DatabaseException;
import db.services.AccountService;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import db.models.User;
import game.services.GameEngineService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.rest.common.Utils;

import java.rmi.AccessException;

import static server.rest.common.Utils.EMPTY_JSON;

@Path("/session")
public class SessionServlet extends HttpServlet {

    private AccountService accountService;
    private GameEngineService gameEngineService;
    private static final Logger LOGGER = LogManager.getLogger(SessionServlet.class);

    @Inject
    public SessionServlet(AccountService accountService) {
        this.accountService = accountService;
        //gameEngineService=engineService;
        LOGGER.info("[!] Initialized");
    }



    private Long getIdFromRequest(HttpServletRequest request) {
        final HttpSession currentSession = request.getSession();
        final Object obj = currentSession.getAttribute(Utils.USER_ID_KEY);
        return (Long)obj;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkSession(@Context HttpHeaders headers, @Context HttpServletRequest request) {

        final Long uid = getIdFromRequest(request);
        if (uid == null) {
            try {
                return addSession(accountService.getUser(Math.round(accountService.getCount()*Math.random())),request);
            } catch (DatabaseException e) {
                e.printStackTrace();

            }
            return Response.status(Response.Status.BAD_REQUEST).entity(EMPTY_JSON).build();
        }
        else {
            try {
                final User realUser = this.accountService.getUser(uid);
                if (realUser == null) {
                    return Response.status(Response.Status.OK).entity(EMPTY_JSON).build();
                } else {
                    final JsonObject idJs = new JsonObject();
                    idJs.addProperty("id", uid);

                    return Response.status(Response.Status.OK).entity(idJs.toString()).build();
                }
            } catch (DatabaseException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSession(User requestedUser, @Context HttpServletRequest request) {
        try {
            final User realUser = accountService.getUser(requestedUser.getUsername());
            if (realUser == null || !realUser.getPassword().equals(requestedUser.getPassword())) {
                LOGGER.info("[!] Invalid logging "+requestedUser.getUsername());
                return Response.status(Response.Status.BAD_REQUEST).entity(EMPTY_JSON).build();
            } else {
                final HttpSession currentSession = request.getSession();
                final JsonObject idJs = new JsonObject();
                currentSession.setAttribute(Utils.USER_ID_KEY, realUser.getId());
                idJs.addProperty("id", realUser.getId());
                //gameEngineService.userLogin(realUser);
                return Response.status(Response.Status.OK).entity(idJs.toString()).build();
            }
        } catch (DatabaseException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
        }

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSession(@Context HttpServletRequest request) {
        final HttpSession currentSession = request.getSession();
        currentSession.removeAttribute(Utils.USER_ID_KEY);
        /*final Long uid = getIdFromRequest(request);
        try{
            final User realUser = this.accountService.getUser(uid);
            if (realUser!=null) gameEngineService.userLogout(realUser);
        }
        catch (AccessException e) {
            LOGGER.error(e.getMessage());
        }*/
        return Response.status(Response.Status.OK).entity(EMPTY_JSON).build();

    }

}