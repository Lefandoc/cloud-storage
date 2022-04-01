package ru.gb.lefandoc.cloudstorage.server.handler;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

@Slf4j
public class JdbcHandler {

    private Connection connection;
    private Statement statement;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:CSDatabase.db");
        connection.setAutoCommit(false);
        statement = connection.createStatement();
    }

    private void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(String.valueOf(e));
        }
    }

    public void createTable() throws SQLException {
        try {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS USERS_INFO (" +
                    "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "LOGIN VARCHAR NOT NULL," +
                    "PASSWORD VARCHAR" +
                    ");");
            log.info("Table users_info created or already exists");
        } catch (SQLException e) {
            log.error(String.valueOf(e));
            connection.rollback();
        }
    }

    public void insertAdmin() throws SQLException {
        try (final PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO USERS_INFO(ID, login, password) VALUES (?, ?, ?)")) {
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, "admin");
            preparedStatement.setString(3, "111");
            preparedStatement.executeUpdate();
        }
    }

    public boolean findUser(String name, String password) throws SQLException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users_info " +
                        "WHERE users_info.login like ?" +
                        "and users_info.password like ?"
        )) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                log.info("Found user: {} - {} - {}\n", resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
                return true;
            }
        }
        return false;
    }
}
