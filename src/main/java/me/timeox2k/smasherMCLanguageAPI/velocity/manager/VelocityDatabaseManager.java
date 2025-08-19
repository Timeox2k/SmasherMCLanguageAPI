package me.timeox2k.smasherMCLanguageAPI.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.timeox2k.smasherMCLanguageAPI.velocity.SmasherMCLanguageVelocityAPI;

import java.sql.*;

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

}