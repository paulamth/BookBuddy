package de.hsos.swa.books.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;

/**
 * Die Klasse BookDTO (Data Transfer Object) dient zur Übertragung von Buchinformationen
 * zwischen verschiedenen Systemkomponenten oder externen APIs.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
public class BookDTO {
    private Long databaseId;
    private String idInExternApi;
    private String isbn;
    private String title;
    private String authors;
    private String publishedDate;
    private String imageLink;
    /**
     * Erstellt ein Standard-BookDTO-Objekt mit voreingestellten Werten.
     * Dies wird verwendet, wenn kein Buch ausgewählt wurde.
     *
     * @return Dieses BookDTO-Objekt mit Standardwerten.
     */
    public BookDTO createDefault() {
        this.title = "Kein Buch ausgewählt";
        this.idInExternApi = "0";
        return this;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public String getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }


    public String getIdInExternApi() {
        return idInExternApi;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setIdInExternApi(String idInExternApi) {
        this.idInExternApi = idInExternApi;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
