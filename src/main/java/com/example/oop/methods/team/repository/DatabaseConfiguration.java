package com.example.oop.methods.team.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfiguration {

    public static final String URL = "jdbc:mysql://localhost:3306/library";
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";

    public static final String INSERT_AUTHOR_QUERY =
            "INSERT INTO authors(full_name, birthday, lang, country) VALUES (?, ?, ?, ?)";
    public static final String INSERT_BOOK_QUERY =
            "INSERT INTO books(id_author, title, genre, _year) VALUES (?, ?, ?, ?)";
    public static final String INSERT_USER_QUERY =
            "INSERT INTO users(username, password, user_role) VALUES (?, ?, ?)";
    public static final String INSERT_LIBRARY_QUERY =
            "INSERT INTO libraries(owner_id) VALUES (?)";

    public static final String GET_AUTHOR_BY_ID_QUERY =
            "SELECT * FROM authors WHERE id = ?";
    public static final String GET_BOOK_BY_ID_QUERY =
            "SELECT * FROM books WHERE id = ?";
    public static final String GET_USER_BY_ID_QUERY =
            "SELECT * FROM users WHERE id = ?";
    public static final String GET_LIBRARY_BY_ID_QUERY =
            "SELECT * FROM libraries WHERE id = ?";

    public static final String UPDATE_AUTHOR_QUERY =
            "UPDATE authors SET full_name = ?, birthday = ?, lang = ?, country = ? WHERE id = ?";
    public static final String UPDATE_BOOK_QUERY =
            "UPDATE books SET id_author = ?, title = ?, genre = ?, _year = ? WHERE id = ?";
    public static final String UPDATE_USER_QUERY =
            "UPDATE users SET username = ?, password = ? WHERE id = ?";
    public static final String UPDATE_LIBRARY_QUERY =
            "UPDATE libraries SET owner_id = ? WHERE id = ?";

    public static final String DELETE_AUTHOR_QUERY =
            "DELETE FROM authors WHERE id = ?";
    public static final String DELETE_BOOK_QUERY =
            "DELETE FROM books WHERE id = ?";
    public static final String DELETE_USER_QUERY =
            "DELETE FROM users WHERE id = ?";
    public static final String DELETE_LIBRARY_QUERY =
            "DELETE FROM libraries WHERE id = ?";

    public static final String GET_ALL_AUTHORS_QUERY =
            "SELECT * FROM authors";
    public static final String GET_ALL_BOOKS_QUERY =
            "SELECT * FROM books";
    public static final String GET_ALL_USERS_QUERY =
            "SELECT * FROM users";
    public static final String GET_ALL_LIBRARIES_QUERY =
            "SELECT * FROM libraries";

    public static final String ADD_BOOK_TO_LIBRARY =
            "INSERT INTO books_to_libraries(book_id, library_id) VALUES (?, ?)";
    public static final String ADD_AUTHOR_TO_LIBRARY =
            "INSERT INTO authors_to_libraries(author_id, library_id) VALUES (?, ?)";
    public static final String REMOVE_BOOK_FROM_LIBRARY =
            "DELETE FROM books_to_libraries WHERE book_id = ? AND library_id = ?";
    public static final String REMOVE_AUTHOR_FROM_LIBRARY =
            "DELETE FROM authors_to_libraries WHERE author_id = ? AND library_id = ?";

    public static final String GET_ALL_BOOKS_FOR_LIBRARY =
            "SELECT btl.book_id FROM books_to_libraries btl " +
            "INNER JOIN books b ON btl.book_id = b.id WHERE btl.library_id = ?";
    public static final String GET_ALL_AUTHORS_FOR_LIBRARY =
            "SELECT atl.author_id FROM authors_to_libraries atl " +
            "INNER JOIN authors a ON atl.author_id = a.id WHERE atl.library_id = ?";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }

}