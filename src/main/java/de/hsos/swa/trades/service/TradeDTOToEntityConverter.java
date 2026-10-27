package de.hsos.swa.trades.service;

import de.hsos.swa.books.acl.BookInfoDelivery;
import de.hsos.swa.books.acl.BookInfoProvider;
import de.hsos.swa.books.entity.Book;
import de.hsos.swa.books.entity.BookDTO;
import de.hsos.swa.trades.entity.Trade;
import de.hsos.swa.trades.entity.TradeDTO;
import de.hsos.swa.users.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Service, der Trade-Entitäten in TradeDTOs umwandelt. Die Klasse ist zuständig für die
 * Konvertierung von Trade-Entitäten in die entsprechende DTO-Darstellung. Sie kann Einzel-
 * und Listenumwandlungen durchführen und nutzt dabei Buchinformationen vom BookInfoProvider.
 *
 * @author Rieke Lübbe
 *
 */
@ApplicationScoped
public class TradeDTOToEntityConverter {

    @Inject
    private BookInfoDelivery bookInfoProvider;

    /**
     * Konvertiert eine Trade-Entität in ein TradeDTO.
     * Diese Methode extrahiert relevante Informationen aus der Trade-Entität und ruft zusätzliche Buchinformationen
     * vom BookInfoProvider ab, um ein vollständiges TradeDTO zu erstellen.
     *
     * @param trade Die Trade-Entität, die in ein TradeDTO umgewandelt werden soll.
     * @return Das konvertierte TradeDTO.
     */
    public TradeDTO convertEntityToDTO(Trade trade) {
        TradeDTO tradeDTO = new TradeDTO(trade.getDatabaseBookIdFirstBook(), trade.getInitiatorUserId());
        tradeDTO.setBewertung(trade.getBewertung());
        tradeDTO.setDatabaseBookIdSecondBook(trade.getDatabaseBookIdSecondBook());
        tradeDTO.setId(trade.getId());
        tradeDTO.setAcceptorUserId(trade.getAcceptorUserId());
        tradeDTO.setStatus(trade.getStatus());
        tradeDTO.setBookTitleFirstBook(bookInfoProvider.getTitleByBookId(trade.getDatabaseBookIdFirstBook()));
        tradeDTO.setBookTitleSecondBook(bookInfoProvider.getTitleByBookId(trade.getDatabaseBookIdSecondBook()));
        return tradeDTO;
    }

    /**
     * Konvertiert eine Liste von Trade-Entitäten in eine Liste von TradeDTOs.
     * Diese Methode ruft die convertEntityToDTO-Methode auf, um jedes einzelne Trade-Objekt in ein TradeDTO zu konvertieren.
     *
     * @param trades Eine Liste von Trade-Entitäten, die in TradeDTOs umgewandelt werden sollen.
     * @return Eine Liste der konvertierten TradeDTOs.
     */
    public List<TradeDTO> convertEntitiesToDTOs(List<Trade> trades) {
        List<TradeDTO> dtos = new ArrayList<>();
        for (Trade trade : trades) {
            dtos.add(convertEntityToDTO(trade));
        }
        return dtos;
    }
}
