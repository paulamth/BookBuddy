package de.hsos.swa.books.service;

import de.hsos.swa.books.boundary.BookAPIClient;
import de.hsos.swa.books.entity.Book;
import de.hsos.swa.books.entity.BookDTO;
import de.hsos.swa.books.gateway.BookSpeicher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service-Klasse zur Verwaltung von Buchinformationen.
 * Diese Klasse bietet Funktionen zum Abrufen, Filtern, Speichern und Löschen von Büchern
 * sowie zur Interaktion mit einer externen API zur Buchsuche.
 *
 * @author Paula Matthies
 * @author Rieke Lübbe
 *
 */
@ApplicationScoped
public class BookService implements BookVerwaltung {

    @Inject
    private BookSpeicher repository;
    @Inject
    @RestClient
    private BookAPIClient bookAPIClient;
    @Inject
    @ConfigProperty(name = "BOOK_API_KEY")
    Optional<String> apiKey;

    /**
     * Holt die Datenbank-ID eines Buches anhand der externen API-ID.
     *
     * @param bookExternApiId Die externe API-ID des Buches.
     * @return Die Datenbank-ID des Buches.
     */
    @Override
    public Long getBookDBIdByExternId(String bookExternApiId) {
        return repository.findByExternalApiId(bookExternApiId).getDatabaseId();
    }

    /**
     * Filtert die Bücher anhand der angegebenen Filterparameter und ruft die Daten von der externen API ab.
     *
     * @param title Der Titel des Buches (optional).
     * @param author Der Autor des Buches (optional).
     * @param publishedDate Das Veröffentlichungsdatum des Buches (optional).
     * @return Eine Liste von BookDTO-Objekten, die den Filterkriterien entsprechen.
     */
    @Override
    public List<BookDTO> getFilteredBooksFromAPI(String title, String author, String publishedDate) {
        StringBuilder queryBuilder = new StringBuilder();

        if (title != null && !title.isEmpty()) {
            queryBuilder.append("intitle:").append(title).append("+");
        }
        if (author != null && !author.isEmpty()) {
            queryBuilder.append("inauthor:").append(author).append("+");
        }
        if (publishedDate != null && !publishedDate.isEmpty()) {
            queryBuilder.append("inpublisher:").append(publishedDate).append("+");
        }

        String query = queryBuilder.length() > 0 ? queryBuilder.substring(0, queryBuilder.length() - 1) : "";

        return BookAPIResponseToDTOConverter.convertApiResponseToDTO(bookAPIClient.searchBooks(query, apiKey.orElse(null), 0, 5));
    }

    /**
     * Holt alle Bücher aus der Datenbank anhand einer Liste von Datenbank-IDs.
     *
     * @param databaseIds Eine Liste von Datenbank-IDs.
     * @return Eine Liste von BookDTO-Objekten, die den angegebenen IDs entsprechen.
     */
    @Override
    public List<BookDTO> getAllBooksByIds(List<Long> databaseIds) {
        List<Book> books = repository.findAll().stream()
                .filter(book -> databaseIds.contains(book.getDatabaseId()))
                .collect(Collectors.toList());
        List<BookDTO> dtos = new ArrayList<>();
        for (Book book : books) {
            String apiAnswer = bookAPIClient.getBookById(book.getIdInExternApi(), apiKey.orElse(null));
            BookDTO bookDTO = BookAPIResponseToDTOConverter.convertApiResponseToDTOForSingleBook(apiAnswer);
            bookDTO.setDatabaseId(book.getDatabaseId());
            dtos.add(bookDTO);
        }
        return dtos;
    }

    /**
     * Holt ein einzelnes Buch anhand seiner Datenbank-ID.
     *
     * @param idInDatabase Die Datenbank-ID des Buches.
     * @return Ein BookDTO-Objekt mit den Buchinformationen.
     */
    @Override
    public BookDTO getBookByDatabaseId(Long idInDatabase) {
        BookDTO bookDTO = new BookDTO();
        if (idInDatabase != 0) {
            Book book = repository.findByDatabaseId(idInDatabase);
            String apiAnswer = bookAPIClient.getBookById(book.getIdInExternApi(), apiKey.orElse(null));
            bookDTO = BookAPIResponseToDTOConverter.convertApiResponseToDTOForSingleBook(apiAnswer);
            bookDTO.setDatabaseId(book.getDatabaseId());
            return bookDTO;
        }
        return bookDTO.createDefault();
    }

    /**
     * Holt den Titel eines Buches anhand seiner Datenbank-ID.
     *
     * @param bookInDatabaseId Die Datenbank-ID des Buches.
     * @return Der Titel des Buches.
     */
    @Override
    public String getTitleByBook(Long bookInDatabaseId) {
        return getBookByDatabaseId(bookInDatabaseId).getTitle();
    }
}
