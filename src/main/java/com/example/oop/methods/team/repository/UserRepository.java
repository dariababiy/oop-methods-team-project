package com.example.oop.methods.team.repository;

import com.example.oop.methods.team.model.Author;
import com.example.oop.methods.team.model.User;
import com.example.oop.methods.team.model.UserRole;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    /**
     * CRUD
     */

    public Long createUser(
            String username,
            String password,
            UserRole userRole
    ) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        DatabaseConfiguration.INSERT_USER_QUERY,
                        Statement.RETURN_GENERATED_KEYS)
                ) {
            if (this.userAlreadyExists(username)) {
                throw new SQLException("Username '" + username + "' already taken, please select another one");
            }
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, userRole.toString());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected");
            }
            Long savedId = null;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    savedId = keys.getLong(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained");
                }
            }
            return savedId;
        }
    }

    private boolean userAlreadyExists(String username) throws SQLException {
        return this.getUserByUsername(username) != null;
    }

    public User getUserById(Long id) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.GET_USER_BY_ID_QUERY)
        ) {
            User user = new User();
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(UserRole.valueOf(rs.getString("user_role")));
                }
            }
            return user;
        }
    }

    public Integer updateUser(
            Long id,
            String username,
            String password
    ) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.UPDATE_USER_QUERY)
        ) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setLong(3, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected");
            }
            return affectedRows;
        }
    }

    public boolean deleteUser(Long id) throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.DELETE_USER_QUERY)
        ) {
            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                throw new SQLException("Deleting user failed, no rows affected");
            }
        }
    }

    public List<User> getAllUsers() throws SQLException {
        try (
                Connection c = DatabaseConfiguration.getConnection();
                PreparedStatement ps = c.prepareStatement(DatabaseConfiguration.GET_ALL_USERS_QUERY);
                ResultSet rs = ps.executeQuery();
        ) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(UserRole.valueOf(rs.getString("user_role")));
                users.add(user);
            }
            return users;
        }
    }

    /**
     * OTHER METHODS
     */

    public User getUserByUsername(String username) throws SQLException {
        return this.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public boolean approveLogin(String username, String password) throws SQLException {
        User dbUser = this.getUserByUsername(username);
        return dbUser.getPassword().equals(password);
    }
}
