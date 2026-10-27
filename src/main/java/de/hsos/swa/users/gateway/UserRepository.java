package de.hsos.swa.users.gateway;

import de.hsos.swa.users.entity.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

/**
 * Repository-Klasse für den Zugriff auf Benutzerdaten in der Datenbank.
 * Diese Klasse implementiert die UserSpeicher-Schnittstelle und stellt Methoden zum Abrufen,
 * Speichern und Löschen von Benutzern sowie zum Verwalten der Bibliothek eines Benutzers bereit.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@RequestScoped
public class UserRepository implements UserSpeicher {

    @Inject
    private EntityManager entityManager;

    /**
     * Findet einen Benutzer anhand seiner ID.
     *
     * @param id Die ID des Benutzers.
     * @return Der Benutzer mit der angegebenen ID oder null, wenn kein Benutzer gefunden wurde.
     */
    @Override
    public User findById(long id) {
        return entityManager.find(User.class, id);
    }

    /**
     * Findet einen Benutzer anhand seiner E-Mail-Adresse.
     *
     * @param email Die E-Mail-Adresse des Benutzers.
     * @return Der Benutzer mit der angegebenen E-Mail-Adresse oder null, wenn kein Benutzer gefunden wurde.
     */
    @Override
    public User findByEmail(String email) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Speichert die Benutzerdaten in der Datenbank.
     *
     * @param user Der Benutzer, dessen Daten gespeichert werden sollen.
     */
    @Override
    public void save(User user) {
        entityManager.persist(user);
    }

    /**
     * Löscht den angegebenen Benutzer aus der Datenbank.
     *
     * @param user Der Benutzer, der gelöscht werden soll.
     */
    @Override
    public void delete(User user) {
        entityManager.remove(user);
    }

    /**
     * Fügt ein Buch zur Bibliothek eines Benutzers hinzu.
     *
     * @param boonInDBId Die ID des Buches in der Datenbank.
     * @param userId Die ID des Benutzers, dem das Buch hinzugefügt wird.
     */
    @Override
    public void addBookToUserLibrary(Long boonInDBId, Long userId) {
        User user = findById(userId);
        user.getBesitzendeBuecher().add(boonInDBId);
        entityManager.persist(user);
    }

    /**
     * Entfernt ein Buch aus der Bibliothek eines Benutzers.
     *
     * @param bookIdInDB Die ID des zu entfernenden Buches in der Datenbank.
     * @param userId Die ID des Benutzers, dem das Buch entfernt wird.
     */
    @Override
    public boolean removeBookFromLibrary(Long bookIdInDB, Long userId) {
        User user = findById(userId);
        if (user == null) {
            return false;
        }
        boolean bookRemoved = user.getBesitzendeBuecher().remove(bookIdInDB);
        if (!bookRemoved) {
            return false;
        }
        entityManager.persist(user);
        return true;
    }
}
