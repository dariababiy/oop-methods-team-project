package com.example.oop.methods.team.repository;

import com.example.oop.methods.team.model.Author;
import com.example.oop.methods.team.model.Book;
import com.example.oop.methods.team.model.Library;
import com.example.oop.methods.team.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LibraryRepository {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public LibraryRepository(UserRepository userRepository, AuthorRepository authorRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * CRUD
     */

    public Long createLibrary(
            User user
    ) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        DatabaseConfiguration.INSERT_LIBRARY_QUERY,
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setLong(1, user.getId());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating library failed, no rows affected");
            }
            Long savedId = null;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    savedId = keys.getLong(1);
                } else {
                    throw new SQLException("Creating library failed, no ID obtained");
                }
            }
            return savedId;
        }
    }

    public Library getLibraryById(Long id) throws SQLException, ParseException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.GET_LIBRARY_BY_ID_QUERY)
        ) {
            Library library = new Library();
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    library.setId(rs.getLong("id"));
                    library.setOwner(this.userRepository.getUserById(rs.getLong("owner_id")));
                    this.initBooksAndAuthorsForLibrary(library);
                }
            }
            return library;
        }
    }

    private void initBooksAndAuthorsForLibrary(Library library) throws SQLException, ParseException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps1 = c.prepareStatement(DatabaseConfiguration.GET_ALL_BOOKS_FOR_LIBRARY);
                PreparedStatement ps2 = c.prepareStatement(DatabaseConfiguration.GET_ALL_AUTHORS_FOR_LIBRARY);
        ) {
            ps1.setLong(1, library.getId());
            ps2.setLong(1, library.getId());
            ResultSet rs1 = ps1.executeQuery();
            ResultSet rs2 = ps2.executeQuery();
            List<Author> authors = new ArrayList<>();
            List<Book> books = new ArrayList<>();
            while (rs1.next()) {
                long bookId = rs1.getLong("book_id");
                Book book = this.bookRepository.getBookById(bookId);
                books.add(book);
            }
            while (rs2.next()) {
                long authorId = rs2.getLong("author_id");
                Author author = this.authorRepository.getAuthorById(authorId);
                authors.add(author);
            }
            library.setAuthors(authors);
            library.setBooks(books);
            rs1.close();
            rs2.close();
        }
    }

    public Integer updateLibrary(
            Long id,
            User user
    ) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.UPDATE_LIBRARY_QUERY)
        ) {
            ps.setLong(1, id);
            ps.setLong(2, user.getId());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating library failed, no rows affected");
            }
            return affectedRows;
        }
    }

    public boolean deleteLibrary(Long id) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.DELETE_LIBRARY_QUERY)
        ) {
            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                throw new SQLException("Deleting library failed, no rows affected");
            }
        }
    }

    public List<Library> getAllLibraries() throws SQLException, ParseException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.GET_ALL_LIBRARIES_QUERY);
                ResultSet rs = ps.executeQuery();
        ) {
            List<Library> libraries = new ArrayList<>();
            while (rs.next()) {
                Library library = this.getLibraryById(rs.getLong("id"));
                libraries.add(library);
            }
            return libraries;
        }
    }

    /**
     * OTHER METHODS
     */

    public boolean addBookToLibrary(Book book, Library library) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps1 = c.prepareStatement(DatabaseConfiguration.ADD_BOOK_TO_LIBRARY);
                ) {
            ps1.setLong(1, book.getId());
            ps1.setLong(2, library.getId());
            int ar1 = ps1.executeUpdate();
            if (ar1 == 0) {
                throw new SQLException("Adding the book to the library failed, no rows affected");
            }
            return true;
        }
    }

    public boolean removeBookFromLibrary(Book book, Library library) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps1 = c.prepareStatement(DatabaseConfiguration.REMOVE_BOOK_FROM_LIBRARY);
        ) {
            ps1.setLong(1, book.getId());
            ps1.setLong(2, library.getId());
            int ar1 = ps1.executeUpdate();
            if (ar1 == 0) {
                throw new SQLException("Removing the book from the library failed, no rows affected");
            }
            return true;
        }
    }

    public boolean removeAuthorFromLibrary(Author author, Library library) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps1 = c.prepareStatement(DatabaseConfiguration.REMOVE_AUTHOR_FROM_LIBRARY);
        ) {
            ps1.setLong(1, author.getId());
            ps1.setLong(2, library.getId());
            int ar1 = ps1.executeUpdate();
            if (ar1 == 0) {
                throw new SQLException("Removing the author from the library failed, no rows affected");
            }
            return true;
        }
    }

    public Library getLibraryByOwnerId(Long id) throws SQLException, ParseException {
        return this.getAllLibraries().stream()
                .filter(l -> l.getOwner().getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Book> getBooksByAuthorId(Long libraryId, Long authorId) throws SQLException, ParseException {
        return this.getLibraryById(libraryId).getBooks().stream()
                .filter(b -> b.getAuthor().getId().equals(authorId))
                .collect(Collectors.toList());
    }

    public boolean addAuthorToLibrary(Author author, Library library) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps2 = c.prepareStatement(DatabaseConfiguration.ADD_AUTHOR_TO_LIBRARY)
        ) {
            ps2.setLong(1, author.getId());
            ps2.setLong(2, library.getId());
            int ar2 = ps2.executeUpdate();
            if (ar2 == 0) {
                throw new SQLException("Adding the author to the library failed, no rows affected");
            }
            return true;
        }
    }
}
