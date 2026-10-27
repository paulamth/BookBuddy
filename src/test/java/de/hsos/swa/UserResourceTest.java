package de.hsos.swa;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

/**
 * Testklasse für die Endpunkte der UserResource.
 * Diese Tests stellen sicher, dass die API korrekt funktioniert und die
 * entsprechenden Statuscodes und Antworten liefert.
 */
@QuarkusTest
public class UserResourceTest {

    /**
     * Testet den DELETE-Endpunkt, um einen Nutzer mit einer bestimmten ID zu löschen.
     * Es wird erwartet, dass der Nutzer mit der ID 1 erfolgreich gelöscht wird und ein
     * 204-Statuscode zurückgegeben wird.
     */
    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        given()
                .pathParam("id", userId)
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(204);
    }
}
