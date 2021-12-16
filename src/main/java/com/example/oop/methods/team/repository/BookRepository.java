package com.example.oop.methods.team.repository;

import com.example.oop.methods.team.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository {

    private final AuthorRepository authorRepository;

    @Autowired
    public BookRepository() {
        this.authorRepository = new AuthorRepository();
    }

    /**
     * CRUD
     */

    public Long createBook(
            String authorName,
            String title,
            String genre,
            Integer year
    ) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        DatabaseConfiguration.INSERT_BOOK_QUERY,
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            Long authorId = this.authorRepository.getAuthorIdByFullName(authorName);
            ps.setLong(1, authorId);
            ps.setString(2, title);
            ps.setString(3, genre);
            ps.setInt(4, year);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected");
            }
            Long savedId = null;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    savedId = keys.getLong(1);
                } else {
                    throw new SQLException("Creating book failed, no ID obtained");
                }
            }
            return savedId;
        }
    }

    public Book getBookById(Long id) throws SQLException, ParseException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.GET_BOOK_BY_ID_QUERY)
        ) {
            Book book = new Book();
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    book.setId(rs.getLong("id"));
                    book.setTitle(rs.getString("title"));
                    book.setGenre(rs.getString("genre"));
                    book.setYear(rs.getInt("_year"));
                    book.setAuthor(this.authorRepository.getAuthorById(rs.getLong("id_author")));
                }
            }
            return book;
        }
    }

    public Integer updateBook(
            Long id,
            String authorName,
            String title,
            String genre,
            Integer year
    ) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.UPDATE_BOOK_QUERY)
        ) {
            Long authorId = this.authorRepository.getAuthorIdByFullName(authorName);
            ps.setLong(1, authorId);
            ps.setString(2, title);
            ps.setString(3, genre);
            ps.setInt(4, year);
            ps.setLong(5, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating book failed, no rows affected");
            }
            return affectedRows;
        }
    }

    public boolean deleteBook(Long id) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.DELETE_BOOK_QUERY)
        ) {
            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                throw new SQLException("Deleting book failed, no rows affected");
            }
        }
    }

    public List<Book> getAllBooks() throws SQLException, ParseException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.GET_ALL_BOOKS_QUERY);
                ResultSet rs = ps.executeQuery();
        ) {
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                book.setGenre(rs.getString("genre"));
                book.setYear(rs.getInt("_year"));
                book.setAuthor(this.authorRepository.getAuthorById(rs.getLong("id_author")));
                books.add(book);
            }
            return books;
        }
    }

    /**
     * OTHER METHODS
     */

}