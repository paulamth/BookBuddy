package de.hsos.swa.books.service;

import org.json.JSONArray;
import org.json.JSONObject;
import de.hsos.swa.books.entity.BookDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Konvertiert eine API-Antwort (im JSON-Format) in BookDTO-Objekte.
 * Diese Klasse stellt Methoden zum Parsen der API-Antworten und zum Erstellen
 * von Buch-Datenübertragungsobjekten (DTOs) bereit.
 *
 * @author Rieke Lübbe
 *
 */
public class BookAPIResponseToDTOConverter {

    /**
     * Konvertiert die Antwort einer API-Anfrage in eine Liste von BookDTO-Objekten.
     *
     * @param apiResponseJson Die API-Antwort als JSON-String.
     * @return Eine Liste von BookDTO-Objekten, die aus der API-Antwort extrahiert wurden.
     *         Falls keine Bücher vorhanden sind, wird eine leere Liste zurückgegeben.
     */
    public static List<BookDTO> convertApiResponseToDTO(String apiResponseJson) {
        JSONObject apiResponse = new JSONObject(apiResponseJson);

        if (!apiResponse.has("items") || apiResponse.getJSONArray("items").isEmpty()) {
            return new ArrayList<>();
        }

        JSONArray itemsArray = apiResponse.getJSONArray("items");
        List<BookDTO> bookDTOList = new ArrayList<>();

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject bookObject = itemsArray.getJSONObject(i);
            String idInExternApi = "";

            if (bookObject.has("id")) {
                idInExternApi = bookObject.getString("id");
            }

            JSONObject volumeInfo = bookObject.getJSONObject("volumeInfo");

            BookDTO bookDTO = convertVolumeInfo(volumeInfo);
            bookDTO.setIdInExternApi(idInExternApi);

            System.out.println(bookDTO.getTitle() + " hat ID (extern) " + bookDTO.getIdInExternApi());
            bookDTOList.add(bookDTO);
        }
        return bookDTOList;
    }
    /**
     * Konvertiert die Volumeninformationen eines Buches in ein BookDTO-Objekt.
     *
     * @param volumeInfo JSON-Objekt mit den Volumeninformationen des Buches.
     * @return Das BookDTO-Objekt, das die Informationen des Buches enthält.
     */
    public static BookDTO convertVolumeInfo(JSONObject volumeInfo) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(volumeInfo.getString("title"));

        JSONArray industryIdentifiersArray = volumeInfo.getJSONArray("industryIdentifiers");
        for (int k = 0; k < industryIdentifiersArray.length(); k++) {
            JSONObject identifier = industryIdentifiersArray.getJSONObject(k);
            if ("ISBN_13".equals(identifier.getString("type"))) {
                bookDTO.setIsbn(identifier.getString("identifier"));
                break;
            }
        }

        if (volumeInfo.has("authors")) {
            JSONArray authorsArray = volumeInfo.getJSONArray("authors");
            StringBuilder authorsString = new StringBuilder();
            for (int j = 0; j < authorsArray.length(); j++) {
                if (j > 0) {
                    authorsString.append(", ");
                }
                authorsString.append(authorsArray.getString(j));
            }
            bookDTO.setAuthors(authorsString.toString());
        }

        bookDTO.setPublishedDate(volumeInfo.optString("publishedDate", "Unbekannt"));

        if (volumeInfo.has("imageLinks")) {
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            if (imageLinks.has("thumbnail")) {
                bookDTO.setImageLink(imageLinks.getString("thumbnail"));
            }
        }
        return bookDTO;
    }

    /**
     * Konvertiert die Antwort einer API-Anfrage für ein einzelnes Buch in ein BookDTO-Objekt.
     *
     * @param apiResponseJson Die API-Antwort als JSON-String.
     * @return Das BookDTO-Objekt, das das Buch aus der API-Antwort enthält.
     */
    public static BookDTO convertApiResponseToDTOForSingleBook(String apiResponseJson) {
        JSONObject apiResponse = new JSONObject(apiResponseJson);
        JSONObject bookObject = apiResponse.getJSONObject("volumeInfo");
        String idInExternApi = "";

        if (apiResponse.has("id")) {
            idInExternApi = apiResponse.getString("id");
        }

        BookDTO bookDTO = convertVolumeInfo(bookObject);
        bookDTO.setIdInExternApi(idInExternApi);
        System.out.println(bookDTO.getTitle() + " hat ID (extern) " + bookDTO.getIdInExternApi());

        return bookDTO;
    }
}
