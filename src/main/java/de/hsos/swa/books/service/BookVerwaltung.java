package de.hsos.swa.books.service;

import de.hsos.swa.books.entity.BookDTO;
import java.util.List;

/**
 * Interface für die Verwaltung von Buch-Daten.
 * Bietet Methoden zum Abrufen von Buchinformationen, Filtern von Büchern,
 * und Einfügen oder Finden von Büchern in der Datenbank.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public interface BookVerwaltung {

    /**
     * Holt die Datenbank-ID eines Buches anhand der externen API-ID.
     *
     * @param bookExternApiId Die externe API-ID des Buches.
     * @return Die Datenbank-ID des Buches.
     */
    Long getBookDBIdByExternId(String bookExternApiId);

    /**
     * Ruft eine Liste von BookDTO-Objekten ab, die mit den angegebenen
     * Filterparametern übereinstimmen.
     *
     * @param title Der Titel des Buches (optional).
     * @param author Der Autor des Buches (optional).
     * @param publishedDate Das Veröffentlichungsdatum des Buches (optional).
     * @return Eine Liste von BookDTO-Objekten, die den Filterkriterien entsprechen.
     */
    List<BookDTO> getFilteredBooksFromAPI(String title, String author, String publishedDate);


    /**
     * Holt alle Bücher anhand einer Liste von Datenbank-IDs.
     *
     * @param databaseIds Eine Liste von Datenbank-IDs.
     * @return Eine Liste von BookDTO-Objekten, die den angegebenen IDs entsprechen.
     */
    List<BookDTO> getAllBooksByIds(List<Long> databaseIds);

    /**
     * Holt ein Buch anhand seiner Datenbank-ID.
     *
     * @param idInDatabase Die Datenbank-ID des Buches.
     * @return Ein BookDTO-Objekt, das die Informationen des Buches enthält.
     */
    BookDTO getBookByDatabaseId(Long idInDatabase);

    /**
     * Holt den Titel eines Buches anhand seiner Datenbank-ID.
     *
     * @param bookInDatabaseId Die Datenbank-ID des Buches.
     * @return Der Titel des Buches.
     */
    String getTitleByBook(Long bookInDatabaseId);
}
