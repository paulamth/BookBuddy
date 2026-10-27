package de.hsos.swa.users.service;

import de.hsos.swa.books.acl.BookInfoProvider;
import de.hsos.swa.books.entity.BookDTO;
import de.hsos.swa.trades.entity.TradeDTO;
import de.hsos.swa.users.entity.User;
import de.hsos.swa.users.entity.UserDTO;
import de.hsos.swa.users.gateway.UserSpeicher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service-Klasse zur Verwaltung von Benutzern und deren Bibliothek.
 * Diese Klasse implementiert die UserVerwaltung-Schnittstelle und bietet Methoden
 * zum Abrufen von Benutzerdaten, zum Erstellen und Löschen von Benutzern sowie
 * zum Verwalten der Bücher eines Benutzers, einschließlich des Hinzufügens und Entfernens von Büchern.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@ApplicationScoped
public class UserService implements UserVerwaltung {

    @Inject
    private UserSpeicher repository;

    @Inject
    private BookInfoProvider bookInfoProvider;

    /**
     * Ruft einen Benutzer anhand seiner ID ab.
     *
     * @param Id Die ID des Benutzers.
     * @return Der Benutzer mit der angegebenen ID.
     */
    @Override
    public User getUserById(long Id) {
        return repository.findById(Id);
    }

    /**
     * Ruft einen Benutzer anhand seiner E-Mail-Adresse ab.
     *
     * @param email Die E-Mail-Adresse des Benutzers.
     * @return Der Benutzer mit der angegebenen E-Mail-Adresse.
     */
    @Override
    public User getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Erstellt einen neuen Benutzer anhand eines UserDTO.
     *
     * @param userDTO Das UserDTO, das die zu erstellenden Benutzerdaten enthält.
     * @return Der neu erstellte Benutzer.
     */
    @Override
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setRole("USER");
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        repository.save(user);
        System.out.println("Service: User erstellt"+ user + user.getName());
        return user;
    }

    /**
     * Ruft die verfügbaren Bücher eines Benutzers ab, die für den Tausch in Betracht gezogen werden können.
     *
     * @param userId Die ID des Benutzers.
     * @param trades Eine Liste der TradeDTOs, die die aktuellen Tauschvorgänge repräsentieren.
     * @return Eine Liste von BookDTOs, die die verfügbaren Bücher des Benutzers darstellen.
     */
    @Override
    public List<BookDTO> getAvailableBooksByUser(long userId, List<TradeDTO> trades) {
        List<BookDTO> booksOfUser = getBooksByUser(userId);
        List<BookDTO> availableBooksOfUser = new ArrayList<>();
        for (BookDTO bookDTO : booksOfUser) {
            boolean notInATradeAtTheMoment = true;
            for (TradeDTO tradeDTO : trades) {
                String status = tradeDTO.getStatus();
                if (status.equals("CREATED") || status.equals("OTHER_USER_OFFERED") ) {
                    if ((tradeDTO.getInitiatorUserId() == userId && tradeDTO.getDatabaseBookIdFirstBook() == bookDTO.getDatabaseId()) ||
                            (tradeDTO.getAcceptorUserId() == userId && tradeDTO.getDatabaseBookIdSecondBook() == bookDTO.getDatabaseId())) {
                        notInATradeAtTheMoment = false;
                    }
                }
            }
            if (notInATradeAtTheMoment) {
                availableBooksOfUser.add(bookDTO);
            }
        }
        return availableBooksOfUser;
    }

    /**
     * Entfernt ein Buch aus der Bibliothek eines Benutzers.
     *
     * @param bookIdInExternalApi Die ID des Buches, das entfernt werden soll.
     * @param userId Die ID des Benutzers, dessen Bibliothek das Buch enthält.
     */
    @Override
    public boolean removeBookFromLibrary(Long bookIdInExternalApi, Long userId) {
        return repository.removeBookFromLibrary(bookIdInExternalApi, userId);
    }

    /**
     * Ruft die Bücher eines Benutzers ab.
     *
     * @param userId Die ID des Benutzers.
     * @return Eine Liste von BookDTOs, die die Bücher des Benutzers darstellen.
     */
    @Override
    public List<BookDTO> getBooksByUser(long userId) {
        List<Long> bookIdsInDB = repository.findById(userId).getBesitzendeBuecher();
        List<BookDTO> dtos = bookInfoProvider.getBookDTOsByDBIds(bookIdsInDB);
        for (BookDTO bookDTO : dtos) {
            System.out.println(bookDTO.getTitle());
        }
        return dtos;
    }

    /**
     * Fügt ein Buch zur Bibliothek eines Benutzers hinzu.
     *
     * @param bookIdInDB Die ID des Buches in der Datenbank.
     * @param userId Die ID des Benutzers, dem das Buch hinzugefügt werden soll.
     */
    @Override
    public void addBookToLibrary(Long bookIdInDB, Long userId) {
        repository.addBookToUserLibrary(bookIdInDB, userId);
    }

    /**
     * Löscht einen Benutzer anhand seiner ID.
     *
     * @param id Die ID des zu löschenden Benutzers.
     * @return true, wenn der Benutzer erfolgreich gelöscht wurde, andernfalls false.
     */
    @Override
    public boolean deleteUser(Long id) {
        User user = repository.findById(id);
        if (user != null) {
            repository.delete(user);
            return true;
        }
        return false;
    }
}
