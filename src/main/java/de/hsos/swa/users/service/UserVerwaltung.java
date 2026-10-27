package de.hsos.swa.users.service;

import de.hsos.swa.books.entity.BookDTO;
import de.hsos.swa.trades.entity.TradeDTO;
import de.hsos.swa.users.entity.User;
import de.hsos.swa.users.entity.UserDTO;

import java.util.List;

/**
 * Schnittstelle zur Verwaltung von Benutzerdaten und Benutzerbibliotheken.
 * Diese Schnittstelle stellt Methoden bereit, um Benutzerinformationen abzurufen,
 * zu erstellen, zu löschen sowie Bücher zur Bibliothek eines Benutzers hinzuzufügen oder zu entfernen.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public interface UserVerwaltung {

    /**
     * Ruft einen Benutzer anhand seiner ID ab.
     *
     * @param id Die ID des Benutzers.
     * @return Der Benutzer mit der angegebenen ID.
     */
    User getUserById(long id);

    /**
     * Ruft einen Benutzer anhand seiner E-Mail-Adresse ab.
     *
     * @param email Die E-Mail-Adresse des Benutzers.
     * @return Der Benutzer mit der angegebenen E-Mail-Adresse.
     */
    User getUserByEmail(String email);

    /**
     * Erstellt einen neuen Benutzer anhand eines UserDTO.
     *
     * @param userDTO Das UserDTO, das die zu erstellenden Benutzerdaten enthält.
     * @return Der neu erstellte Benutzer.
     */
    User createUser(UserDTO userDTO);

    /**
     * Ruft die verfügbaren Bücher eines Benutzers ab, die für den Tausch in Betracht gezogen werden können.
     *
     * @param userId Die ID des Benutzers.
     * @param trades Eine Liste der TradeDTOs, die die aktuellen Tauschvorgänge repräsentieren.
     * @return Eine Liste von BookDTOs, die die verfügbaren Bücher des Benutzers darstellen.
     */
    List<BookDTO> getAvailableBooksByUser(long userId, List<TradeDTO> trades);

    /**
     * Entfernt ein Buch aus der Bibliothek eines Benutzers.
     *
     * @param bookIdInExternalApi Die ID des Buches, das entfernt werden soll.
     * @param userId Die ID des Benutzers, dessen Bibliothek das Buch enthält.
     */
    boolean removeBookFromLibrary(Long bookIdInExternalApi, Long userId);

    /**
     * Ruft die Bücher eines Benutzers ab.
     *
     * @param userId Die ID des Benutzers.
     * @return Eine Liste von BookDTOs, die die Bücher des Benutzers darstellen.
     */
    List<BookDTO> getBooksByUser(long userId);

    /**
     * Fügt ein Buch zur Bibliothek eines Benutzers hinzu.
     *
     * @param bookIdInDB Die ID des Buches in der Datenbank.
     * @param userId Die ID des Benutzers, dem das Buch hinzugefügt werden soll.
     */
    void addBookToLibrary(Long bookIdInDB, Long userId);

    /**
     * Löscht einen Benutzer anhand seiner ID.
     *
     * @param id Die ID des zu löschenden Benutzers.
     * @return true, wenn der Benutzer erfolgreich gelöscht wurde, andernfalls false.
     */
    boolean deleteUser(Long id);

}
