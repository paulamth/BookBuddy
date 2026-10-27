package de.hsos.swa;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

/**
 * Testklasse für die Endpunkte der TradeResource.
 * Diese Tests stellen sicher, dass die API korrekt funktioniert und die
 * entsprechenden Statuscodes und Antworten liefert.
 *
 */
@QuarkusTest
public class TradeResourceTest {

    /**
     * Testet den DELETE-Endpunkt, um einen Handel mit einer bestimmten ID zu löschen.
     * Es wird erwartet, dass der Handel mit der ID 1 erfolgreich gelöscht wird und ein
     * 204-Statuscode zurückgegeben wird.
     */
    @Test
    public void testDeleteTrade() {
        Long tradeId = 1L;

        given()
                .pathParam("id", tradeId)
                .when()
                .delete("/trades/{id}")
                .then()
                .statusCode(204);
    }

}
