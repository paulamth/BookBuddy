package de.hsos.swa.books.acl;

import de.hsos.swa.books.entity.BookDTO;
import de.hsos.swa.books.service.BookVerwaltung;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Die Klasse BookInfoProvider implementiert das Interface BookInfoDelivery
 * und stellt Buchinformationen über die BookVerwaltung bereit.
 *
 * @author Rieke Lübbe
 *
 */
@ApplicationScoped
public class BookInfoProvider implements BookInfoDelivery {

    @Inject
    private BookVerwaltung bookService;

    /**
     * Ruft den Titel eines Buches anhand der Datenbank-ID ab.
     *
     * @param databaseBookId Die ID des Buches in der Datenbank.
     * @return Der Titel des Buches als String.
     */
    @Override
    public String getTitleByBookId(Long databaseBookId) {
        return bookService.getTitleByBook(databaseBookId);
    }

    /**
     * Holt eine Liste von BookDTOs basierend auf einer Liste von Datenbank-IDs.
     *
     * @param databaseIds Eine Liste von Buch-IDs aus der Datenbank.
     * @return Eine Liste von BookDTO-Objekten mit den Buchinformationen.
     */
    @Override
    public List<BookDTO> getBookDTOsByDBIds(List<Long> databaseIds) {
        return bookService.getAllBooksByIds(databaseIds);
    }
}
