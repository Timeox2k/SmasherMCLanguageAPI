package me.timeox2k.smasherMCLanguageAPI.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.timeox2k.smasherMCLanguageAPI.SmasherMCLanguageAPI;
import me.timeox2k.smasherMCLanguageAPI.events.LanguageUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final HikariDataSource dataSource;

    public DatabaseManager() {
        final FileConfiguration configuration = SmasherMCLanguageAPI.getInstance().getConfig();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configuration.getString("MySQL.host"));
        config.setUsername(configuration.getString("MySQL.username"));
        config.setPassword(configuration.getString("MySQL.password"));
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(600000);
        dataSource = new HikariDataSource(config);

        createTables();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    private void createTables() {
        String selectedLanguageTable = "CREATE TABLE IF NOT EXISTS selected_language (" + "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " + "name VARCHAR(16) NOT NULL, " + "language INT DEFAULT 0" + ")";

        String languagesTable = "CREATE TABLE IF NOT EXISTS languages (" + "id INT AUTO_INCREMENT PRIMARY KEY, " + "international_name VARCHAR(50) NOT NULL" + ")";

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {

            statement.executeUpdate(selectedLanguageTable);
            statement.executeUpdate(languagesTable);

            insertDefaultLanguages(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDefaultLanguages(Connection connection) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM languages";
        String insertSql = "INSERT INTO languages (international_name) VALUES (?)";

        try (Statement checkStatement = connection.createStatement(); ResultSet rs = checkStatement.executeQuery(checkSql)) {

            rs.next();
            if (rs.getInt(1) == 0) {
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setString(1, "German");
                    insertStatement.executeUpdate();

                    insertStatement.setString(1, "English");
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    public void createPlayer(Player player) {
        String sql = "INSERT INTO selected_language (uuid, name, language) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name)";

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setInt(3, 1);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            SmasherMCLanguageAPI.getInstance().getLogger().severe("Failed to create player: " + e.getMessage());
        }
    }

    public int getPlayerLanguage(Player player) {
        return getPlayerLanguage(player.getUniqueId().toString());
    }

    public List<Language> getAllLanguages() {
        List<Language> languages = new ArrayList<>();
        String sql = "SELECT id, international_name FROM languages ORDER BY id";

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                languages.add(new Language(resultSet.getInt("id"), resultSet.getString("international_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SmasherMCLanguageAPI.getInstance().getLogger().severe("Failed to get languages: " + e.getMessage());
        }

        return languages;
    }

    public void setPlayerLanguage(Player player, int languageId) {
        String sql = "INSERT INTO selected_language (uuid, name, language) VALUES (?, ?, ?) " + "ON DUPLICATE KEY UPDATE language = VALUES(language), name = VALUES(name)";

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setInt(3, languageId);
            statement.executeUpdate();

            LanguageUpdateEvent languageUpdateEvent = new LanguageUpdateEvent(getAllLanguages().get(languageId - 1).getInternationalName(), player);
            Bukkit.getPluginManager().callEvent(languageUpdateEvent);
        } catch (SQLException e) {
            e.printStackTrace();
            SmasherMCLanguageAPI.getInstance().getLogger().severe("Failed to set player language: " + e.getMessage());
        }
    }

    public int getLanguageIdByName(String languageName) {
        String sql = "SELECT id FROM languages WHERE international_name = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, languageName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SmasherMCLanguageAPI.getInstance().getLogger().severe("Failed to get language ID by name: " + e.getMessage());
        }

        return -1;
    }


    public int getPlayerLanguage(String uuid) {
        String sql = "SELECT language FROM selected_language WHERE uuid = ?";

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("language");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            SmasherMCLanguageAPI.getInstance().getLogger().severe("Failed to get player language: " + e.getMessage());
        }

        return 2;
    }

    public static class Language {
        private final int id;
        private final String internationalName;

        public Language(int id, String internationalName) {
            this.id = id;
            this.internationalName = internationalName;
        }

        public int getId() {
            return id;
        }

        public String getInternationalName() {
            return internationalName;
        }
    }

}