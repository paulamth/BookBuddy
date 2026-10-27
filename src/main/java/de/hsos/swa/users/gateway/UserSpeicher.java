package de.hsos.swa.users.gateway;

import de.hsos.swa.users.entity.User;
import java.util.List;

/**
 * Schnittstelle für die Speicherung und Verwaltung von Benutzerdaten.
 * Diese Schnittstelle definiert Methoden zum Abrufen, Speichern und Löschen von Benutzern sowie
 * zum Verwalten der Bücher, die einem Benutzer zugeordnet sind.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public interface UserSpeicher {

    /**
     * Findet einen Benutzer anhand seiner ID.
     *
     * @param ID Die ID des Benutzers.
     * @return Der Benutzer mit der angegebenen ID oder null, wenn kein Benutzer gefunden wurde.
     */
    User findById(long ID);

    /**
     * Findet einen Benutzer anhand seiner E-Mail-Adresse.
     *
     * @param email Die E-Mail-Adresse des Benutzers.
     * @return Der Benutzer mit der angegebenen E-Mail-Adresse oder null, wenn kein Benutzer gefunden wurde.
     */
    User findByEmail(String email);

    /**
     * Speichert die Benutzerdaten.
     *
     * @param user Der Benutzer, dessen Daten gespeichert werden sollen.
     */
    void save(User user);

    /**
     * Löscht den angegebenen Benutzer.
     *
     * @param user Der Benutzer, der gelöscht werden soll.
     */
    void delete(User user);

    /**
     * Fügt ein Buch zur Bibliothek des Benutzers hinzu.
     *
     * @param boonInDBId Die ID des Buches in der Datenbank.
     * @param userId Die ID des Benutzers, dem das Buch hinzugefügt wird.
     */
    void addBookToUserLibrary(Long boonInDBId, Long userId);

    /**
     * Entfernt ein Buch aus der Bibliothek des Benutzers.
     *
     * @param bookIdInDB Die ID des zu entfernenden Buches in der Datenbank.
     * @param userId Die ID des Benutzers, dem das Buch entfernt wird.
     */
    boolean removeBookFromLibrary(Long bookIdInDB, Long userId);
}
