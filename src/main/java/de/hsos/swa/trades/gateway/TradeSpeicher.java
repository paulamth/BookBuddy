package de.hsos.swa.trades.gateway;

import de.hsos.swa.trades.entity.Trade;

import java.util.List;

/**
 * Interface für den Zugriff auf die Trade-Daten im Speicher.
 * Diese Schnittstelle definiert Methoden zum Abrufen, Speichern und Löschen von Trade-Daten.
 * Sie dient als Schnittstelle zwischen der Anwendung und der Datenquelle für Trade-Objekte.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public interface TradeSpeicher {

    /**
     * Gibt eine Liste aller Trades im System zurück.
     *
     * @return Eine Liste von allen Trades.
     */
    List<Trade> findAll();

    /**
     * Sucht einen Trade anhand der angegebenen ID.
     *
     * @param ID Die eindeutige ID des Trades.
     * @return Der Trade mit der angegebenen ID.
     */
    Trade findById(long ID);

    /**
     * Speichert einen neuen oder geänderten Trade im System.
     *
     * @param trade Der Trade, der gespeichert werden soll.
     */
    void save(Trade trade);

    /**
     * Löscht den angegebenen Trade aus dem System.
     *
     * @param trade Der Trade, der gelöscht werden soll.
     */
    void delete(Trade trade);

    /**
     * Gibt eine Liste von Trades zurück, die mit einem bestimmten Benutzer verknüpft sind.
     *
     * @param userId Die Benutzer-ID, nach der die Trades gefiltert werden.
     * @return Eine Liste der Trades, die mit dem angegebenen Benutzer verknüpft sind.
     */
    List<Trade> getTradesByUser(long userId);
}
