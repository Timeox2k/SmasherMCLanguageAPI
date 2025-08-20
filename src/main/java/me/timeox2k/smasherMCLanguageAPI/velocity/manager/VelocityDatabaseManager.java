package me.timeox2k.smasherMCLanguageAPI.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.timeox2k.smasherMCLanguageAPI.velocity.SmasherMCLanguageVelocityAPI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VelocityDatabaseManager {
    private final HikariDataSource dataSource;

    public VelocityDatabaseManager() {
        final YamlDocument fileConfig = SmasherMCLanguageVelocityAPI.getInstance().getConfig();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(fileConfig.getString("MySQL.host"));
        config.setUsername(fileConfig.getString("MySQL.username"));
        config.setPassword(fileConfig.getString("MySQL.password"));
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(600000);
        dataSource = new HikariDataSource(config);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
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
        }

        return languages;
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
        }

        return 2;
    }

    public static class Language {
        private final int id;
        private final String internationalName;
        public Language(int id, String internationalName ) {
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