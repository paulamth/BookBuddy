package de.hsos.swa.books.entity;

import jakarta.persistence.*;

import java.util.List;

/**
 * Entitätsklasse für Bücher in der Datenbank.
 * Diese Klasse wird von JPA verwendet, um die Tabelle "books" zu verwalten.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;
    private String idInExternApi;
    public void setIdInExternApi(String idInExternApi) {
        this.idInExternApi = idInExternApi;
    }

    public String getIdInExternApi() {
        return idInExternApi;
    }

    public Long getDatabaseId() {
        return databaseId;
    }
}
