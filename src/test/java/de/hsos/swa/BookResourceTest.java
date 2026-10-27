package de.hsos.swa;

import de.hsos.swa.books.entity.BookDTO;
import de.hsos.swa.users.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

/**
 * Testklasse für die Endpunkte der BookResource.
 * Diese Tests stellen sicher, dass die API korrekt funktioniert und die
 * entsprechenden Statuscodes und Antworten liefert.
 *
 * @author Rieke Lübbe
 *
 */
@QuarkusTest
public class BookResourceTest {

    /**
     * Testet den GET-Endpunkt zum Suchen von Büchern mit einem Filter.
     * Hier wird nach dem Titel "Java" gesucht und erwartet, dass 5 Bücher zurückgegeben werden.
     */
    @Test
    public void testGetFilteredBooks() {
        String title = "Java";

        given()
                .queryParam("title", title)
                .when()
                .get("/books/api/search")
                .then()
                .statusCode(200)
                .body("size()", is(5));
    }

    /**
     * Testet den GET-Endpunkt, um die Bibliothek eines bestimmten Nutzers abzurufen.
     * Es wird erwartet, dass der Nutzer mit der ID 1 genau 3 Bücher in seiner Bibliothek hat.
     */
    @Test
    public void testGetLibraryByUser() {
        Long userId = 1L;

        given()
                .pathParam("userid", userId)
                .when()
                .get("/books/api/{userid}")
                .then()
                .statusCode(200)
                .body("size()", is(4));
    }

    /**
     * Testet den POST-Endpunkt, um ein Buch zur Bibliothek eines Nutzers hinzuzufügen.
     * Es wird erwartet, dass das Buch mit der ID "16F3DwAAQBAJ" erfolgreich zum Nutzer mit der ID 1 hinzugefügt wird.
     */
    @Test
    public void testAddToUserLibrary_Success() {
        Long userId = 1L;
        String bookId = "16F3DwAAQBAJ";

        given()
                .formParam("bookId", bookId)
                .when()
                .post("/books/api/" + userId)
                .then()
                .statusCode(204);
    }

    /**
     * Testet den POST-Endpunkt, um ein Buch zur Bibliothek eines Nutzers hinzuzufügen,
     * aber der Nutzer mit der ID 99 existiert nicht. Es wird erwartet, dass ein 404-Fehler zurückgegeben wird.
     */
    @Test
    public void testAddToUserLibrary_UserNotFound() {
        Long userId = 99L;
        String bookId = "456";

        given()
                .formParam("bookId", bookId)
                .when()
                .post("/books/api/"+ userId)
                .then()
                .statusCode(404);
    }

    /**
     * Testet den DELETE-Endpunkt, um ein Buch aus der Bibliothek eines Nutzers zu entfernen.
     * Es wird erwartet, dass das Buch mit der ID "0jzuEAAAQBAJ" erfolgreich entfernt wird.
     */
    @Test
    public void testRemoveBookFromLibrary_Success() {
        Long userId = 1L;
        Long bookId = 3L;

        given()
                .queryParam("bookId", bookId)
                .when()
                .delete("/books/api/"+ userId)
                .then()
                .statusCode(204);
    }

    /**
     * Testet den DELETE-Endpunkt, um ein Buch aus der Bibliothek eines Nutzers zu entfernen,
     * aber der Nutzer mit der ID 99 existiert nicht. Es wird erwartet, dass ein 404-Fehler zurückgegeben wird.
     */
    @Test
    public void testRemoveBookFromLibrary_UserNotFound() {
        Long userId = 99L;
        String bookId = "0jzuEAAAQBAJ";

        given()
                .queryParam("bookId", bookId)
                .when()
                .delete("/books/api/"+ userId)
                .then()
                .statusCode(404);
    }

}
