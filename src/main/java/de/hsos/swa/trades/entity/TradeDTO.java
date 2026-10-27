package de.hsos.swa.trades.entity;

/**
 * Data Transfer Object (DTO) für einen Tausch.
 * Diese Klasse dient dazu, Informationen über einen Tausch zwischen zwei Benutzern zu übertragen.
 * Sie enthält Details zu den beteiligten Büchern, Benutzern und dem Status des Tausch.
 *
 * Ein TradeDTO wird verwendet, um den Tausch zwischen einem Initiator und einem Akzeptor zu modellieren,
 * mit Informationen über die beiden Bücher und den Status des Tauschs.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public class TradeDTO {
    private Long id;
    private Long databaseBookIdFirstBook;
    private Long databaseBookIdSecondBook;
    private String status;
    private Long initiatorUserId;
    private Long acceptorUserId;
    private String bookTitleFirstBook;
    private String bewertung;
    private String bookTitleSecondBook;
    public TradeDTO(Long databaseBookIdFirstBook, Long initiatorUserId) {
        this.status = "CREATED";
        this.databaseBookIdFirstBook = databaseBookIdFirstBook;
        this.initiatorUserId = initiatorUserId;
        this.acceptorUserId = 0L;
        this.databaseBookIdSecondBook = 0L;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBewertung() {
        return bewertung;
    }

    public Long getInitiatorUserId() {
        return initiatorUserId;
    }

    public void setInitiatorUserId(Long initiatorUserId) {
        this.initiatorUserId = initiatorUserId;
    }

    public void setAcceptorUserId(Long acceptorUserId) {
        this.acceptorUserId = acceptorUserId;
    }

    public Long getAcceptorUserId() {
        return acceptorUserId;
    }

    public void setBewertung(String bewertung) {
        this.bewertung = bewertung;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Long getDatabaseBookIdFirstBook() {
        return databaseBookIdFirstBook;
    }

    public Long getDatabaseBookIdSecondBook() {
        return databaseBookIdSecondBook;
    }

    public void setDatabaseBookIdFirstBook(Long databaseBookIdFirstBook) {
        this.databaseBookIdFirstBook = databaseBookIdFirstBook;
    }

    public void setDatabaseBookIdSecondBook(Long databaseBookIdSecondBook) {
        this.databaseBookIdSecondBook = databaseBookIdSecondBook;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookTitleFirstBook() {
        return bookTitleFirstBook;
    }

    public void setBookTitleSecondBook(String bookTitleSecondBook) {
        this.bookTitleSecondBook = bookTitleSecondBook;
    }

    public void setBookTitleFirstBook(String bookTitleFirstBook) {
        this.bookTitleFirstBook = bookTitleFirstBook;
    }

    public String getBookTitleSecondBook() {
        return bookTitleSecondBook;
    }
}
