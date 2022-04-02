package ru.gb.lefandoc.cloudstorage.server.handler;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class JdbcHandler {

    private Connection connection;
    private Statement statement;

    public JdbcHandler() {
        try {
            connect();
            createTable();
            insertAdmin();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            log.error(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void createTable() throws SQLException {
        try {
            String sqlCreate = "CREATE TABLE IF NOT EXISTS users_info (\n"
                    + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                    + "	login text NOT NULL,\n"
                    + "	password text\n"
                    + ");";
            statement.executeUpdate(sqlCreate);
        } catch (SQLException e) {
            log.error(e.getClass().getName() + ": " + e.getMessage());
            connection.rollback();
        }
        connection.commit();
        log.info("Table users_info created or already exists");
    }

    public void insertAdmin() throws SQLException {
        try (final PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO users_info(login, password) VALUES (?, ?)")) {
            preparedStatement.setString(1, "admin");
            preparedStatement.setString(2, "admin");
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            connection.rollback();
        }
        connection.commit();
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
