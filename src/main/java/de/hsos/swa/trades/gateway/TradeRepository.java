package de.hsos.swa.trades.gateway;

import de.hsos.swa.trades.entity.Trade;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

/**
 * Implementierung der TradeSpeicher-Schnittstelle für den Zugriff auf die Trade-Daten in einer
 * relationalen Datenbank. Diese Klasse nutzt die JPA-API, um mit der Datenbank zu interagieren
 * und Trade-Objekte zu speichern, abzurufen und zu löschen.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@RequestScoped
public class TradeRepository implements TradeSpeicher {

    @Inject
    private EntityManager entityManager;

    /**
     * Gibt eine Liste aller Trades im System zurück.
     *
     * @return Eine Liste von allen Trades.
     */
    @Override
    public List<Trade> findAll() {
        return entityManager.createQuery("SELECT t FROM Trade t", Trade.class).getResultList();
    }

    /**
     * Gibt eine Liste von Trades zurück, die mit einem bestimmten Benutzer verknüpft sind, entweder
     * als Initiator oder als Akzeptor.
     *
     * @param userId Die Benutzer-ID, nach der die Trades gefiltert werden.
     * @return Eine Liste der Trades, die mit dem angegebenen Benutzer verknüpft sind.
     */
    @Override
    public List<Trade> getTradesByUser(long userId) {
        return entityManager.createQuery(
                        "SELECT t FROM Trade t WHERE t.initiatorUserId = :userId OR t.acceptorUserId = :userId", Trade.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Sucht einen Trade anhand der angegebenen ID.
     *
     * @param id Die eindeutige ID des Trades.
     * @return Der Trade mit der angegebenen ID.
     * @throws EntityNotFoundException Wenn der Trade mit der angegebenen ID nicht gefunden wird.
     */
    @Override
    public Trade findById(long id) {
        Trade trade = entityManager.find(Trade.class, id);
        if (trade == null) {
            throw new EntityNotFoundException("Trade mit ID " + id + " nicht gefunden");
        }
        return trade;
    }

    /**
     * Speichert einen neuen oder geänderten Trade im System.
     *
     * @param trade Der Trade, der gespeichert werden soll.
     */
    @Override
    public void save(Trade trade) {
        entityManager.persist(trade);
    }

    /**
     * Löscht den angegebenen Trade aus dem System.
     *
     * @param trade Der Trade, der gelöscht werden soll.
     */
    @Override
    public void delete(Trade trade) {
        entityManager.remove(trade);
    }
}
