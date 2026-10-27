package de.hsos.swa.books.gateway;

import de.hsos.swa.books.entity.Book;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Repository-Klasse für die Verwaltung der Buch-Entitäten in der Datenbank.
 * Diese Klasse implementiert das Interface BookSpeicher und stellt Methoden
 * für das Speichern, Finden und Löschen von Büchern bereit.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@RequestScoped
public class BookRepository implements BookSpeicher {

    @Inject
    private EntityManager entityManager;

    /**
     * Ruft alle in der Datenbank gespeicherten Bücher ab.
     *
     * @return Eine Liste aller Bücher in der Datenbank.
     */
    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("SELECT b FROM Book b", Book.class).getResultList();
    }

    /**
     * Sucht ein Buch anhand seiner internen Datenbank-ID.
     *
     * @param id Die interne ID des Buches.
     * @return Das gefundene Buch oder null, falls kein Buch mit dieser ID existiert.
     */
    @Override
    public Book findByDatabaseId(long id) {
        return entityManager.find(Book.class, id);
    }

    /**
     * Sucht ein Buch anhand seiner externen API-ID.
     * Falls das Buch nicht existiert, wird eine neue Instanz erstellt und gespeichert.
     *
     * @param id Die ID des Buches in der externen API.
     * @return Das gefundene oder neu erstellte Buch.
     */
    @Override
    public Book findByExternalApiId(String id) {
        List<Book> books = entityManager.createQuery(
                        "SELECT b FROM Book b WHERE b.idInExternApi = :id", Book.class)
                .setParameter("id", id)
                .getResultList();

        Book book = books.isEmpty() ? null : books.get(0);
        if (book == null) {
            book = new Book();
            book.setIdInExternApi(id);
            save(book);
        }

        return book;
    }

    /**
     * Speichert ein Buch in der Datenbank.
     *
     * @param book Das zu speichernde Buch.
     */
    @Override
    public void save(Book book) {
        entityManager.persist(book);
    }

    /**
     * Löscht ein Buch aus der Datenbank.
     *
     * @param book Das zu löschende Buch.
     */
    @Override
    public void delete(Book book) {
        entityManager.remove(book);
    }
}
