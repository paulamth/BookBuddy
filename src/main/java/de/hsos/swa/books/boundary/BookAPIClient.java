package de.hsos.swa.books.boundary;

import de.hsos.swa.books.entity.BookDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Das Interface BookAPIClient stellt eine REST-Client-Schnittstelle zur externen Buch-API bereit.
 * Es ermöglicht das Abrufen von Büchern anhand ihrer ISBN oder ID sowie das Durchsuchen von Büchern.
 *
 * @author Rieke Lübbe
 *
 */
@ApplicationScoped
@RegisterRestClient(configKey = "BookAPI")
public interface BookAPIClient {

    /**
     * Ruft ein Buch anhand der ISBN ab.
     *
     * @param query  Die ISBN des gesuchten Buches (z. B. "isbn:9781234567890").
     * @param apiKey Der API-Schlüssel für die Authentifizierung.
     * @return Die Antwort der externen API als JSON-String.
     */
    @GET
    @Path("/volumes")
    String getBookByISBN(@QueryParam("q") String query, @QueryParam("key") String apiKey);

    /**
     * Ruft ein Buch anhand seiner eindeutigen ID aus der externen API ab.
     *
     * @param bookId Die ID des Buches in der externen API.
     * @param apiKey Der API-Schlüssel für die Authentifizierung.
     * @return Die Antwort der externen API als JSON-String.
     */
    @GET
    @Path("/volumes/{id}")
    String getBookById(@PathParam("id") String bookId, @QueryParam("key") String apiKey);

    /**
     * Sucht nach Büchern anhand eines Suchbegriffs und optionaler Parameter für die Paginierung.
     *
     * @param query      Der Suchbegriff (z. B. "harry potter").
     * @param apiKey     Der API-Schlüssel für die Authentifizierung.
     * @param startIndex Der Startindex für die Ergebnisse (z. B. für Paging).
     * @param maxResults Die maximale Anzahl von Ergebnissen pro Anfrage.
     * @return Die Antwort der externen API als JSON-String.
     */
    @GET
    @Path("/volumes")
    String searchBooks(@QueryParam("q") String query, @QueryParam("key") String apiKey,
                       @QueryParam("startIndex") Integer startIndex, @QueryParam("maxResults") Integer maxResults);
}
