package de.hsos.swa.trades.service;

import de.hsos.swa.trades.entity.Trade;
import de.hsos.swa.trades.entity.TradeDTO;

import java.util.List;

/**
 * Schnittstelle für die Verwaltung von Trade-Daten. Sie bietet Methoden zur Verwaltung von
 * Trade-Objekten, einschließlich der Anzeige, Annahme oder Ablehnung von Trades,
 * der Bewertung von Trades und der Erstellung neuer Trades.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public interface TradeVerwaltung {

    /**
     * Ruft alle Trades ab.
     * Diese Methode gibt eine Liste aller Trades zurück, die im System existieren.
     *
     * @return Eine Liste von TradeDTOs, die alle Trades repräsentieren.
     */
    List<TradeDTO> getAllTrades();

    /**
     * Akzeptiert oder lehnt einen Trade ab.
     * Diese Methode akzeptiert oder lehnt einen Trade basierend auf der übergebenen Trade-ID
     * und der Akzeptanzentscheidung ab.
     *
     * @param tradeId Die ID des Trades, der akzeptiert oder abgelehnt werden soll.
     * @param accept Ein Boolean-Wert, der angibt, ob der Trade akzeptiert (true) oder abgelehnt (false) wird.
     */
    void acceptOrReject(Long tradeId, boolean accept);

    /**
     * Bewertet einen Trade.
     * Diese Methode ermöglicht es, einen Trade mit einer Bewertung zu versehen.
     *
     * @param tradeId Die ID des Trades, der bewertet werden soll.
     * @param rating Die Bewertung, die dem Trade zugewiesen wird.
     */
    void rateTrade(Long tradeId, String rating);

    /**
     * Ruft alle Trades für einen bestimmten Benutzer ab.
     * Diese Methode gibt eine Liste aller Trades zurück, die einem bestimmten Benutzer zugeordnet sind.
     *
     * @param userId Die ID des Benutzers, dessen Trades abgerufen werden sollen.
     * @return Eine Liste von TradeDTOs, die die Trades des angegebenen Benutzers repräsentieren.
     */
    List<TradeDTO> getTradesByUser(long userId);

    /**
     * Bietet ein Buch für einen Trade an.
     * Diese Methode ermöglicht es einem Benutzer, ein Buch in einen bestehenden Trade einzubringen.
     *
     * @param tradeId Die ID des Trades, dem das Buch angeboten werden soll.
     * @param userId Die ID des Benutzers, der das Buch anbietet.
     * @param bookDatabaseId Die ID des Buches in der Datenbank, das angeboten wird.
     */
    void offerBookToTrade(Long tradeId, Long userId, Long bookDatabaseId);

    /**
     * Erstellt einen neuen Trade.
     * Diese Methode erstellt einen neuen Trade basierend auf den angegebenen TradeDTO-Daten und
     * speichert diesen im System.
     *
     * @param tradeDTO Die TradeDTO-Daten, die den neuen Trade beschreiben.
     * @return Der neu erstellte Trade.
     */
    Trade createTrade(TradeDTO tradeDTO);

    /**
     * Löscht einen Trade.
     * Diese Methode löscht einen Trade anhand seiner ID.
     *
     * @param id Die ID des zu löschenden Trades.
     * @return Ein Boolean-Wert, der angibt, ob das Löschen erfolgreich war.
     */
    boolean deleteTrade(Long id);
}
