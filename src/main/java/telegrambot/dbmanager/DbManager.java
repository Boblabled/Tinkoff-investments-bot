package telegrambot.dbmanager;

import telegrambot.user.ProcessMode;
import telegrambot.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DbManager {

    private final Logger logger = LoggerFactory.getLogger(DbManager.class);

    private Connection connection;

    public void connect() {
        String driver = "org.postgresql.Driver";
        String url = "jdbc:postgresql://localhost:5432/tb";
        String superuser = "postgres";
        String password = "1234";
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, superuser, password);
            logger.info("Соединение с базой данных установлена");
        } catch (ClassNotFoundException e) {
            logger.error("Нету драйверов для БД, соединение не установленно");
        } catch (SQLException e) {
            logger.error("Невозможно установить соединение с БД");
        }
    }

    public void disconnect() {
        try {
            connection.close();
            logger.debug("Соединение с БД разорвано");
        } catch (SQLException e) {
            logger.error("Соединение с БД и так разорвано");
        }
    }

    public void createUsersTable() {
        StringBuilder processModeLine = new StringBuilder();
        ProcessMode[] values = ProcessMode.values();
        for (int i = 0; i < values.length; i++) {
            processModeLine.append("'");
            processModeLine.append(values[i]);
            processModeLine.append("'");
            if (i != values.length - 1) {
                processModeLine.append(", ");
            }
        }
        String requestSQL = "DROP TABLE IF EXISTS users;" +
                "CREATE TABLE users(" +
                "chatId BIGINT PRIMARY KEY, " +
                "token VARCHAR NOT NULL, " +
                "brokerAccountId VARCHAR, " +
                "processMode VARCHAR NOT NULL " +
                "CHECK (processMode IN ( " +
                processModeLine + " )));";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(requestSQL);
            statement.close();
            logger.debug("Запрос на создание таблицы выполнен");
        } catch (SQLException e) {
            logger.error("Неверный sql запрос: {}", e.getMessage());
        }
    }

    public void addUser(Long chatId, String token) {
        String requestSQL = "INSERT INTO users " +
                "(chatId, token, brokerAccountId, processMode) " +
                "VALUES (?, ?, NULL, 'START')";
        try {
            PreparedStatement statement = connection.prepareStatement(requestSQL);
            statement.setLong(1, chatId);
            statement.setString(2, token);
            statement.executeUpdate();
            statement.close();
            logger.debug("Запрос на добавление пользователя выполнен");
        } catch (SQLException e) {
            logger.error("Неверный sql запрос: {}", e.getMessage());
        }
    }

    public User getUser(Long chatId) {
        String requestSQL = "SELECT * FROM users WHERE chatId = ?";
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement(requestSQL);
            statement.setLong(1, chatId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                user = new User(result.getLong("chatId"),
                        result.getString("token"),
                        result.getString("brokerAccountId"),
                        ProcessMode.valueOf(result.getString("processMode")));
            }
            result.close();
            statement.close();
            logger.debug("Запрос на получение юзера выполнен");
        } catch (SQLException e) {
            logger.error("Неверный sql запрос: {}", e.getMessage());
        }
        return user;
    }

    public void updateUserField(Long chatId, String field, String value){
        String requestSQL = "UPDATE users SET " + field + " = ? WHERE chatId = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(requestSQL);
            statement.setString(1, value);
            statement.setLong(2, chatId);
            statement.executeUpdate();
            statement.close();
            logger.debug("Запрос на изменение поля выполнен");
        } catch (SQLException e) {
            logger.error("Неверный sql запрос: {}", e.getMessage());
        }
    }
}
