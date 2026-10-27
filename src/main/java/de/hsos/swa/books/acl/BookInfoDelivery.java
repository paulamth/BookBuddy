package de.hsos.swa.books.acl;

import de.hsos.swa.books.entity.BookDTO;

import java.util.List;

/**
 * Dieses Interface definiert Methoden zur Informationsbereitstellung über Bücher.
 * Es ermöglicht den Zugriff auf Buchtitel und Buchdaten anhand von Datenbank-IDs.
 *
 * @author Rieke Lübbe
 *
 */
public interface BookInfoDelivery {

    /**
     * Gibt den Titel eines Buches anhand seiner Datenbank-ID zurück.
     *
     * @param databaseBookId Die ID des Buches in der Datenbank.
     * @return Der Titel des Buches als String.
     */
    String getTitleByBookId(Long databaseBookId);

    /**
     * Gibt eine Liste von BookDTOs basierend auf einer Liste von Datenbank-IDs zurück.
     *
     * @param databaseIds Eine Liste von Buch-IDs aus der Datenbank.
     * @return Eine Liste von BookDTO-Objekten mit den Buchinformationen.
     */
    List<BookDTO> getBookDTOsByDBIds(List<Long> databaseIds);
}
