package de.hsos.swa.trades.boundary;

import de.hsos.swa.books.entity.BookDTO;
import de.hsos.swa.trades.entity.TradeDTO;
import de.hsos.swa.trades.service.TradeService;
import de.hsos.swa.trades.service.TradeVerwaltung;
import de.hsos.swa.users.entity.User;
import de.hsos.swa.users.service.UserService;
import de.hsos.swa.users.service.UserVerwaltung;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.List;

@Path("/trades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(
        info = @Info(
                title = "Trade-Management-API",
                version = "1.0",
                description = "API zur Verwaltung von Buch-Tausch-Transaktionen, einschließlich Anbieten von Büchern, Akzeptieren oder Ablehnen von Trades und Bewerten oder Löschen von Trades."
        )
)
/**
 * Die Klasse stellt eine REST-API für das Management von Buch-Tausch-Transaktionen bereit.
 * Sie ermöglicht es Benutzern, Trades anzuzeigen, Bücher zum Tausch anzubieten, Trades anzunehmen oder abzulehnen,
 * Trades zu bewerten und Trades zu löschen.
 * Die Authentifizierung erfolgt über JSON Web Tokens (JWT), die im Authorization-Cookie übergeben werden.
 * Die Klasse nutzt Quarkus Qute Templates für die HTML-Darstellung.
 *
 * @author Paula Matthies
 *
 */
public class TradeResource {
    @Inject
    private TradeVerwaltung tradeService;
    @Inject
    private UserVerwaltung userService;
    @Inject
    Template trades;
    @Inject
    JWTParser parser;

    @Inject
    Template books;

    private User authenticateUser(String token) throws ParseException {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new WebApplicationException("Nicht autorisiert! Bitte einloggen.", Response.Status.UNAUTHORIZED);
        }
        JsonWebToken jwt = parser.parse(token.substring(7));
        Long userId = Long.parseLong(jwt.getClaim("sub"));
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new WebApplicationException("Benutzer nicht gefunden.", Response.Status.NOT_FOUND);
        }
        return user;
    }
    @GET
    @Transactional
    @Produces(MediaType.TEXT_HTML)
    @Operation(
            summary = "Alle Trades anzeigen",
            description = "Zeigt alle verfügbaren Trades des Benutzers an."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Erfolgreich alle Trades abgerufen"),
            @APIResponse(responseCode = "401", description = "Nicht autorisiert, Token fehlt oder ungültig"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(15000)
    public Response getAllTrades(@CookieParam("Authorization") String token) throws ParseException {
        User user = authenticateUser(token);
        List<TradeDTO> tradeDtos = tradeService.getAllTrades();
        List<BookDTO> booksOfUser = userService.getAvailableBooksByUser(user.getId(), tradeDtos);
        TemplateInstance instance = trades
                .data("user", user)
                .data("trades", tradeDtos)
                .data("availableBooksOfUser", booksOfUser);
        return Response.ok(instance.render()).build();
    }

    @POST
    @Path("/offerBookToTrade")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Operation(
            summary = "Buch für Trade anbieten",
            description = "Bietet ein Buch zum Tausch an, das zu einem Trade hinzugefügt wird."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Buch erfolgreich zum Trade angeboten"),
            @APIResponse(responseCode = "401", description = "Nicht autorisiert, Token fehlt oder ungültig"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(15000)
    public Response offerBookToTrade(@FormParam("bookDBId") Long bookDatabaseId, @FormParam("tradeId") Long tradeId, @CookieParam("Authorization") String token) throws ParseException {
        User user = authenticateUser(token);

        tradeService.offerBookToTrade(tradeId, user.getId(), bookDatabaseId);
        List<TradeDTO> tradeDtos = tradeService.getAllTrades();
        List<BookDTO> booksOfUser = userService.getAvailableBooksByUser(user.getId(), tradeDtos);
        TemplateInstance instance = trades
                .data("user", user)
                .data("trades", tradeDtos)
                .data("availableBooksOfUser", booksOfUser);
        return Response.ok(instance.render()).build();
    }

    @POST
    @Path("/acceptOrReject")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Operation(
            summary = "Trade annehmen oder ablehnen",
            description = "Erlaubt es einem Benutzer, einen Trade anzunehmen oder abzulehnen."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Trade erfolgreich akzeptiert oder abgelehnt"),
            @APIResponse(responseCode = "401", description = "Nicht autorisiert, Token fehlt oder ungültig"),
            @APIResponse(responseCode = "404", description = "Trade nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(15000)
    public Response acceptOrReject(@FormParam("tradeId") Long tradeId,
                                   @FormParam("action") String action,
                                   @CookieParam("Authorization") String token) throws ParseException {
        User user = authenticateUser(token);
        boolean accept = false;
        if ("accept".equals(action)) {
            accept = true;
        } else if ("reject".equals(action)) {
        } else {
            System.out.println("Unbekannte Aktion: " + action);
        }

        tradeService.acceptOrReject(tradeId, accept);

        List<BookDTO> booklist = userService.getBooksByUser(user.getId());
        List<TradeDTO> tradesFromUser = tradeService.getTradesByUser(user.getId());
        TemplateInstance template =  books.data("user", user)
                .data("books", booklist)
                .data("trades", tradesFromUser);
        return Response.ok(template.render()).build();
    }

    @POST
    @Path("/rate")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Operation(
            summary = "Trade bewerten",
            description = "Ermöglicht einem Benutzer, einen Trade zu bewerten."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Trade erfolgreich bewertet"),
            @APIResponse(responseCode = "401", description = "Nicht autorisiert, Token fehlt oder ungültig"),
            @APIResponse(responseCode = "404", description = "Trade nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(15000)
    public Response rateTrade(@FormParam("tradeId") Long tradeId,
                                   @FormParam("bewertung") String rating,
                                   @CookieParam("Authorization") String token) throws ParseException {
        User user = authenticateUser(token);

        tradeService.rateTrade(tradeId, rating);

        List<BookDTO> booklist = userService.getBooksByUser(user.getId());
        List<TradeDTO> tradesFromUser = tradeService.getTradesByUser(user.getId());
        TemplateInstance template =  books.data("user", user)
                .data("books", booklist)
                .data("trades", tradesFromUser);
        return Response.ok(template.render()).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(
            summary = "Trade löschen",
            description = "Löscht einen Trade basierend auf der angegebenen Trade-ID."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Trade erfolgreich gelöscht"),
            @APIResponse(responseCode = "404", description = "Trade nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    public Response deleteTrade(@PathParam("id") Long id) {
        boolean deleted = tradeService.deleteTrade(id);
        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}
