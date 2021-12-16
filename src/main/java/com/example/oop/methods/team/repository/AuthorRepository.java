package com.example.oop.methods.team.repository;

import com.example.oop.methods.team.model.Author;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuthorRepository {

    /**
     * CRUD
     */

    public Long createAuthor(
            String fullName,
            String birthday,
            String lang,
            String country
    ) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        DatabaseConfiguration.INSERT_AUTHOR_QUERY,
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, fullName);
            ps.setString(2, birthday);
            ps.setString(3, lang);
            ps.setString(4, country);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating author failed, no rows affected");
            }
            Long savedId = null;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    savedId = keys.getLong(1);
                } else {
                    throw new SQLException("Creating author failed, no ID obtained");
                }
            }
            return savedId;
        }
    }

    public Author getAuthorById(Long id) throws SQLException, ParseException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.GET_AUTHOR_BY_ID_QUERY)
        ) {
            Author author = new Author();
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    author.setId(rs.getLong("id"));
                    author.setFullName(rs.getString("full_name"));
                    author.setBirthDay(rs.getString("birthday"));
                    author.setLang(rs.getString("lang"));
                    author.setCountry(rs.getString("country"));
                }
            }
            return author;
        }
    }

    public Integer updateAuthor(
            Long id,
            String fullName,
            String birthday,
            String lang,
            String country
    ) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.UPDATE_AUTHOR_QUERY)
        ) {
            ps.setString(1, fullName);
            ps.setString(2, birthday);
            ps.setString(3, lang);
            ps.setString(4, country);
            ps.setLong(5, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating author failed, no rows affected");
            }
            return affectedRows;
        }
    }

    public boolean deleteAuthor(Long id) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.DELETE_AUTHOR_QUERY)
        ) {
            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                throw new SQLException("Deleting author failed, no rows affected");
            }
        }
    }

    public List<Author> getAllAuthors() throws SQLException, ParseException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.GET_ALL_AUTHORS_QUERY);
                ResultSet rs = ps.executeQuery();
        ) {
            List<Author> authors = new ArrayList<>();
            while (rs.next()) {
                Author author = new Author();
                author.setId(rs.getLong("id"));
                author.setFullName(rs.getString("full_name"));
                author.setBirthDay(rs.getString("birthday"));
                author.setLang(rs.getString("lang"));
                author.setCountry(rs.getString("country"));
                authors.add(author);
            }
            return authors;
        }
    }

    /**
     * OTHER METHODS
     */

    public Long getAuthorIdByFullName(String fullName) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement("SELECT id FROM authors WHERE full_name = ?")
        ) {
            ps.setString(1, fullName);
            Long id = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getLong("id");
                } else {
                    throw new SQLException("Obtaining author's full name failed");
                }
            }
            return id;
        }
    }
}