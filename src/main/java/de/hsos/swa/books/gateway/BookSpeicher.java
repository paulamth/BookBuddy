package de.hsos.swa.books.gateway;

import de.hsos.swa.books.entity.Book;
import de.hsos.swa.trades.entity.Trade;
import java.util.List;

/**
 * Interface für das Speichern und Abrufen von Büchern aus einer Datenquelle.
 * Diese Schnittstelle definiert die grundlegenden CRUD-Operationen für Buch-Entitäten.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public interface BookSpeicher {

    /**
     * Ruft alle Bücher aus der Datenquelle ab.
     *
     * @return Eine Liste aller Bücher.
     */
    List<Book> findAll();

    /**
     * Sucht ein Buch anhand seiner internen Datenbank-ID.
     *
     * @param ID Die interne ID des Buches.
     * @return Das gefundene Buch oder null, falls kein Buch mit dieser ID existiert.
     */
    Book findByDatabaseId(long ID);

    /**
     * Sucht ein Buch anhand seiner externen API-ID.
     * Falls das Buch nicht existiert, wird es möglicherweise in einer späteren Implementierung erstellt und gespeichert.
     *
     * @param ID Die ID des Buches in der externen API.
     * @return Das gefundene Buch.
     */
    Book findByExternalApiId(String ID);

    /**
     * Speichert ein Buch in der Datenquelle.
     *
     * @param book Das zu speichernde Buch.
     */
    void save(Book book);

    /**
     * Löscht ein Buch aus der Datenquelle.
     *
     * @param book Das zu löschende Buch.
     */
    void delete(Book book);
}
