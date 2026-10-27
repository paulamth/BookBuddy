package de.hsos.swa.trades.entity;

import jakarta.persistence.*;

/**
 * Repräsentiert einen Tausch zwischen zwei Benutzern.
 * Diese Entität speichert Informationen über einen Initiator und einen Akzeptor
 * sowie eine eindeutige Identifikation des Trades.
 *
 * Ein Trade kann als Angebot von einem Benutzer (Initiator) und der Annahme
 * eines anderen Benutzers (Akzeptor) betrachtet werden.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@Entity
@Table(name = "trades")
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long initiatorUserId;
    private Long acceptorUserId;
    private Long databaseBookIdFirstBook;
    private Long databaseBookIdSecondBook;
    private String bewertung;
    private int status;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDatabaseBookIdFirstBook() {
        return databaseBookIdFirstBook;
    }

    public Long getDatabaseBookIdSecondBook() {
        return databaseBookIdSecondBook;
    }

    public Long getAcceptorUserId() {
        return acceptorUserId;
    }

    public String getBewertung() {
        return bewertung;
    }

    public void setBewertung(String bewertung) {
        this.bewertung = bewertung;
    }

    public void setDatabaseBookIdSecondBook(Long databaseBookIdSecondBook) {
        this.databaseBookIdSecondBook = databaseBookIdSecondBook;
    }

    public void setAcceptorUserId(Long acceptorUserId) {
        this.acceptorUserId = acceptorUserId;
    }

    public void setInitiatorUserId(Long initiatorUserId) {
        this.initiatorUserId = initiatorUserId;
    }

    public Long getInitiatorUserId() {
        return initiatorUserId;
    }

    public void setStatus(String status) {
        if (status.equals("CREATED")) {
            this.status = 0;
        } else if (status.equals("OTHER_USER_OFFERED")) {
            this.status = 1;
        } else if (status.equals("COMPLETED")) {
            this.status = 2;
        } else {
            throw new IllegalArgumentException("Invalid status value");
        }
    }

    public void setDatabaseBookIdFirstBook(Long databaseBookIdFirstBook) {
        this.databaseBookIdFirstBook = databaseBookIdFirstBook;
    }

    public String getStatus() {
        if (status == 0) {
            return "CREATED";
        } else if (status == 1)  {
            return "OTHER_USER_OFFERED";
        } else if (status == 2) {
            return "COMPLETED";
        }
        return null;
    }
}
