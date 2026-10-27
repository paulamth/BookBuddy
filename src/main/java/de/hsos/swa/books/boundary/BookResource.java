package de.hsos.swa.books.boundary;

import de.hsos.swa.books.entity.BookDTO;
import de.hsos.swa.books.service.BookService;
import de.hsos.swa.books.service.BookVerwaltung;
import de.hsos.swa.trades.entity.TradeDTO;
import de.hsos.swa.trades.service.TradeService;
import de.hsos.swa.trades.service.TradeVerwaltung;
import de.hsos.swa.users.entity.User;
import de.hsos.swa.users.service.UserService;
import de.hsos.swa.users.service.UserVerwaltung;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/books")
@OpenAPIDefinition(
        info = @Info(
                title = "Bücherverwaltung-API",
                version = "1.0",
                description = "API zur Verwaltung von Büchern und Benutzerbibliotheken"
        )
)
/**
 * Die Klasse BookResource stellt REST-Endpunkte für die Verwaltung von Büchern und Benutzerbibliotheken zur Verfügung.
 * Sie enthält Methoden zur Buchsuche, Bücherauflistung, gefilterten Suche sowie zur Initialisierung von Buchtauschvorgängen.
 * Diese Klasse nutzt Quarkus, Jakarta RESTful Web Services (JAX-RS) und Qute zur Template-Generierung.
 * Zudem werden OpenAPI-Annotationen verwendet, um die API-Dokumentation zu verbessern.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public class BookResource {

    @Inject
    private BookVerwaltung bookService;
    @Inject
    private UserVerwaltung userService;
    @Inject
    private TradeVerwaltung tradeService;

    @Inject
    Template search;

    @Inject
    Template books;

    @Inject
    Template failure;
    @GET
    @Path("/search")
    @Produces(MediaType.TEXT_HTML)
    @Operation(
            summary = "Zeigt die Suchseite für Bücher an",
            description = "Rendert eine HTML-Seite, auf der der Benutzer nach Büchern suchen kann."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Suchseite erfolgreich geladen"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    public Response showSearchPage() {
        User user = new User();

        TemplateInstance templateInstance = search
                .data("error", "")
                .data("user", user)
                .data("books", null);
        return Response.ok(templateInstance.render()).build();
    }

    @GET
    @Path("/list")
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    @Operation(
            summary = "Zeigt die Liste der Bücher des Benutzers an",
            description = "Rendert eine HTML-Seite, die alle Bücher des Benutzers anzeigt."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Bücherliste erfolgreich geladen"),
            @APIResponse(responseCode = "403", description = "Zugriff verweigert: Gastbenutzer haben keinen Zugriff"),
            @APIResponse(responseCode = "404", description = "Benutzer nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    public Response listBooks(@Context SecurityContext securityContext) {
        try {

            User user = getUserBySecurityContext(securityContext);
            if (user == null) {
                return Response.seeOther(URI.create("/books/failure")).build();
            }

            List<BookDTO> booklist = userService.getBooksByUser(user.getId());
            List<TradeDTO> tradesFromUser = tradeService.getTradesByUser(user.getId());

            TemplateInstance template = books
                    .data("user", user)
                    .data("books", booklist)
                    .data("trades", tradesFromUser);

            return Response.ok(template.render()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ein Fehler ist aufgetreten: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/search/filter")
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    @Operation(
            summary = "Führt eine gefilterte Buchsuche durch mit Ergebnis auf einer HTML-Seite",
            description = "Ermöglicht es, nach Titeln, Autoren oder Veröffentlichungsdatum zu filtern."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Suchergebnisse erfolgreich geladen"),
            @APIResponse(responseCode = "400", description = "Ungültige Eingabedaten: Mindestens ein Suchkriterium muss angegeben werden"),
            @APIResponse(responseCode = "404", description = "Keine Suchergebnisse gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 500)
    @Timeout(5000)
    public Response getFilteredBookSearchPage(@Context SecurityContext securityContext,
                                              @QueryParam("title") String title,
                                              @QueryParam("author") String author,
                                              @QueryParam("publishedDate") String publishedDate) {
        User user = getUserBySecurityContext(securityContext);
        if ((title == null || title.isEmpty()) &&
                (author == null || author.isEmpty()) &&
                (publishedDate == null || publishedDate.isEmpty())) {
            TemplateInstance instance = search
                    .data("user", user)
                    .data("books", null)
                    .data("error", "Gebe mindestens ein Suchkriterium an");
            return Response.ok(instance.render()).build();        }

        List<BookDTO> books = bookService.getFilteredBooksFromAPI(title, author, publishedDate);
        TemplateInstance instance = search
                .data("user", user)
                .data("books", books)
                .data("error", books.isEmpty() ? "Keine Suchergebnisse gefunden." : null);

        return Response.ok(instance.render()).build();
    }

    @GET
    @Path("/api/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
            summary = "Führt eine gefilterte Buchsuche durch",
            description = "Ermöglicht das Filtern von Büchern nach Titel, Autor und Veröffentlichungsdatum über eine API."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Suchergebnisse erfolgreich zurückgegeben"),
            @APIResponse(responseCode = "400", description = "Ungültige Eingabedaten"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 3000)
    @Timeout(6000)
    public Response getFilteredBooks(@QueryParam("title") String title,
                                     @QueryParam("author") String author,
                                     @QueryParam("publishedDate") String publishedDate) {
        List<BookDTO> books = bookService.getFilteredBooksFromAPI(title, author, publishedDate);
        return Response.ok(books).build();
    }

    @GET
    @Path("/api/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
            summary = "Gibt die Bibliothek des Benutzers zurück",
            description = "Gibt eine Liste von Büchern zurück, die im Besitz des angegebenen Benutzers sind."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Benutzerbibliothek erfolgreich geladen"),
            @APIResponse(responseCode = "404", description = "Benutzer nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10000)
    public Response getLibraryByUser(@PathParam("userid") Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<BookDTO> books = bookService.getAllBooksByIds(user.getBesitzendeBuecher());
        return Response.ok(books).build();
    }

    @POST
    @Path("/initTrade")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Operation(
            summary = "Startet einen Tausch von Büchern",
            description = "Erstellt einen neuen Tauschvorgang für das angegebene Buch."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Tausch erfolgreich initiiert"),
            @APIResponse(responseCode = "404", description = "Benutzer oder Buch nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(5000)
    public Response initTrade(@Context SecurityContext securityContext,
                              @FormParam("bookId") String bookIdInExternalApi) {
        User user = getUserBySecurityContext(securityContext);

        if (user == null) {
            return Response.seeOther(URI.create("/books/failure")).build();
        }
        Long databaseBookId = bookService.getBookDBIdByExternId(bookIdInExternalApi);
        TradeDTO tradeDTO = new TradeDTO(databaseBookId, user.getId());
        tradeService.createTrade(tradeDTO);
        List<BookDTO> booklist = userService.getBooksByUser(user.getId());
        List<TradeDTO> tradesFromUser = tradeService.getTradesByUser(user.getId());
        TemplateInstance template =  books
                .data("user", user)
                .data("books", booklist)
                .data("trades", tradesFromUser);
        return Response.ok(template.render()).build();
    }
    @DELETE
    @Path("/api/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
            summary = "Löscht ein Buch aus der Bibliothek",
            description = "Entfernt ein Buch aus der Datenbank und der Bibliothek des Benutzers."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Buch erfolgreich gelöscht"),
            @APIResponse(responseCode = "404", description = "User nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    public Response removeBook(@QueryParam("bookId") Integer bookId,
                               @PathParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        System.out.println("UsID " + userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Long bookDBId = bookService.getBookDBIdByExternId(bookId.toString());
        userService.removeBookFromLibrary(bookDBId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/deleteFromLibrary")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Operation(
            summary = "Löscht ein Buch aus der Bibliothek",
            description = "Entfernt ein Buch aus der Datenbank und der Bibliothek des Benutzers."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Buch erfolgreich gelöscht"),
            @APIResponse(responseCode = "404", description = "Buch nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    public Response removeBook(@Context SecurityContext securityContext,
                               @FormParam("bookId") String bookIdInExternalApi) {
        User user = getUserBySecurityContext(securityContext);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Benutzer nicht gefunden.").build();
        }
        Long bookDBId = bookService.getBookDBIdByExternId(bookIdInExternalApi);
        userService.removeBookFromLibrary(bookDBId, user.getId());

        List<BookDTO> booklist = userService.getBooksByUser(user.getId());
        List<TradeDTO> tradesFromUser = tradeService.getTradesByUser(user.getId());
        TemplateInstance template =  books
                .data("user", user)
                .data("books", booklist)
                .data("trades", tradesFromUser);
        return Response.ok(template.render()).build();
    }

    @POST
    @Path("/addToUserLibrary")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Operation(
            summary = "Fügt ein Buch zur Benutzersammlung hinzu",
            description = "Fügt ein Buch zur Sammlung des Benutzers hinzu, basierend auf der übergebenen bookId."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Buch erfolgreich zur Bibliothek hinzugefügt"),
            @APIResponse(responseCode = "404", description = "Benutzer nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(5000)
    public Response addToUserLibrary(@Context SecurityContext securityContext,
                                    @FormParam("bookId") String bookId) {
        User user = getUserBySecurityContext(securityContext);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Benutzer nicht gefunden.").build();
        }
        long bookIdInDB = bookService.getBookDBIdByExternId(bookId);
        userService.addBookToLibrary(bookIdInDB, user.getId());
        List<BookDTO> booklist = userService.getBooksByUser(user.getId());
        List<TradeDTO> tradesFromUser = tradeService.getTradesByUser(user.getId());
        TemplateInstance template =  books
                .data("user", user)
                .data("books", booklist)
                .data("trades", tradesFromUser);
        return Response.ok(template.render()).build();
    }

    @POST
    @Path("/api/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    @Operation(
            summary = "Fügt ein Buch zur Benutzersammlung hinzu",
            description = "Fügt ein Buch zur Sammlung des Benutzers hinzu, basierend auf der übergebenen bookId und userId."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Buch erfolgreich zur Bibliothek hinzugefügt"),
            @APIResponse(responseCode = "404", description = "Benutzer oder Buch nicht gefunden"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(5000)
    public Response addToUserLibrary(
            @PathParam("userId") Long userId,
            @FormParam("bookId") String bookId) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Benutzer nicht gefunden.").build();
        }

        long bookIdInDB = bookService.getBookDBIdByExternId(bookId);
        if (bookIdInDB == -1) {
            return Response.status(Response.Status.NOT_FOUND).entity("Buch nicht gefunden.").build();
        }

        userService.addBookToLibrary(bookIdInDB, user.getId());

        return Response.status(Response.Status.NO_CONTENT).build();
    }


    private User getUserBySecurityContext(SecurityContext securityContext) {
        if (securityContext.isUserInRole("GAST")) {
            return null;
        }
        String userIdString = securityContext.getUserPrincipal().getName();
        Long userId = Long.parseLong(userIdString);
        return userService.getUserById(userId);
    }

    @GET
    @Path("/failure")
    @Produces(MediaType.TEXT_HTML)
    @Operation(
            summary = "Zeigt eine Fehlerseite an",
            description = "Zeigt eine Fehlerseite an, wenn der Benutzer nicht auf die Ressource zugreifen kann."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Fehlerseite erfolgreich geladen"),
            @APIResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    public Response noAccess() {
        return Response.ok(failure.render()).build();
    }
}
