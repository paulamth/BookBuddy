package de.hsos.swa.trades.service;

import de.hsos.swa.trades.entity.Trade;
import de.hsos.swa.trades.entity.TradeDTO;
import de.hsos.swa.trades.gateway.TradeSpeicher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Service-Klasse für die Verwaltung von Trades.
 * Diese Klasse implementiert die Logik zur Erstellung, Verwaltung und Bearbeitung von Trades
 * und interagiert mit dem Repository für den Datenzugriff.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@ApplicationScoped
public class TradeService implements TradeVerwaltung {

    @Inject
    private TradeSpeicher repository;

    @Inject
    private TradeDTOToEntityConverter tradeDTOToEntityConverter;

    /**
     * Ruft alle Trades ab.
     * Diese Methode ruft alle Trades aus dem Repository ab und konvertiert sie in TradeDTOs.
     *
     * @return Eine Liste von TradeDTOs, die alle Trades repräsentieren.
     */
    @Override
    public List<TradeDTO> getAllTrades() {
        return tradeDTOToEntityConverter.convertEntitiesToDTOs(repository.findAll());
    }

    /**
     * Akzeptiert oder lehnt einen Trade ab.
     * Wenn der Trade akzeptiert wird, wird sein Status auf "COMPLETED" gesetzt.
     * Wenn der Trade abgelehnt wird, werden die Angaben zum akzeptierenden Benutzer
     * und zum zweiten Buch zurückgesetzt.
     *
     * @param tradeId Die ID des Trades, der akzeptiert oder abgelehnt werden soll.
     * @param accept Ein Boolean-Wert, der angibt, ob der Trade akzeptiert (true) oder
     *               abgelehnt (false) wird.
     */
    @Override
    public void acceptOrReject(Long tradeId, boolean accept) {
        Trade trade = repository.findById(tradeId);
        if (accept) {
            trade.setStatus("COMPLETED");
        } else {
            trade.setAcceptorUserId(0L);
            trade.setDatabaseBookIdSecondBook(0L);
            trade.setStatus("CREATED");
        }
        repository.save(trade);
    }

    /**
     * Bewertet einen Trade.
     * Diese Methode fügt dem Trade eine Bewertung hinzu, die zu bestehenden Bewertungen ergänzt wird,
     * falls bereits eine vorhanden ist.
     *
     * @param tradeId Die ID des Trades, der bewertet werden soll.
     * @param rating Die Bewertung, die dem Trade zugewiesen wird.
     */
    @Override
    public void rateTrade(Long tradeId, String rating) {
        Trade trade = repository.findById(tradeId);
        String bewertung = trade.getBewertung();
        if (bewertung != null && !bewertung.isEmpty()) {
            bewertung += ", " + rating;
        } else {
            bewertung = rating;
        }
        trade.setBewertung(bewertung);
        repository.save(trade);
    }

    /**
     * Ruft alle Trades eines bestimmten Benutzers ab.
     * Diese Methode gibt eine Liste von Trades zurück, die einem bestimmten Benutzer zugeordnet sind.
     *
     * @param userId Die ID des Benutzers, dessen Trades abgerufen werden sollen.
     * @return Eine Liste von TradeDTOs, die die Trades des angegebenen Benutzers repräsentieren.
     */
    @Override
    public List<TradeDTO> getTradesByUser(long userId) {
        return tradeDTOToEntityConverter.convertEntitiesToDTOs(repository.getTradesByUser(userId));
    }

    /**
     * Bietet ein Buch für einen Trade an.
     * Diese Methode aktualisiert einen Trade, um ein Buch von einem Benutzer für den Trade anzubieten.
     * Der Status des Trades wird auf "OTHER_USER_OFFERED" gesetzt.
     *
     * @param tradeId Die ID des Trades, dem das Buch angeboten werden soll.
     * @param userId Die ID des Benutzers, der das Buch anbietet.
     * @param bookDatabaseId Die ID des Buches in der Datenbank, das angeboten wird.
     */
    @Override
    public void offerBookToTrade(Long tradeId, Long userId, Long bookDatabaseId) {
        Trade trade = repository.findById(tradeId);
        trade.setAcceptorUserId(userId);
        trade.setDatabaseBookIdSecondBook(bookDatabaseId);
        trade.setStatus("OTHER_USER_OFFERED");
        repository.save(trade);
    }

    /**
     * Erstellt einen neuen Trade.
     * Diese Methode erstellt einen neuen Trade basierend auf den angegebenen TradeDTO-Daten
     * und speichert diesen im Repository.
     *
     * @param tradeDTO Die TradeDTO-Daten, die den neuen Trade beschreiben.
     * @return Der neu erstellte Trade.
     */
    @Override
    public Trade createTrade(TradeDTO tradeDTO) {
        Trade trade = new Trade();
        trade.setStatus(tradeDTO.getStatus());
        trade.setInitiatorUserId(tradeDTO.getInitiatorUserId());
        trade.setDatabaseBookIdFirstBook(tradeDTO.getDatabaseBookIdFirstBook());
        trade.setDatabaseBookIdSecondBook(tradeDTO.getDatabaseBookIdSecondBook());
        trade.setAcceptorUserId(tradeDTO.getAcceptorUserId());
        repository.save(trade);
        return trade;
    }

    /**
     * Löscht einen Trade.
     * Diese Methode löscht einen Trade anhand seiner ID, wenn er im Repository gefunden wird.
     *
     * @param id Die ID des zu löschenden Trades.
     * @return Ein Boolean-Wert, der angibt, ob das Löschen erfolgreich war.
     */
    @Override
    public boolean deleteTrade(Long id) {
        Trade trade = repository.findById(id);
        if (trade != null) {
            repository.delete(trade);
            return true;
        }
        return false;
    }
}
