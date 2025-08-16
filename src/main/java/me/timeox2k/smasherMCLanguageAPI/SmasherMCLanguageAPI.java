package me.timeox2k.smasherMCLanguageAPI;

import me.timeox2k.smasherMCLanguageAPI.commands.language_command;
import me.timeox2k.smasherMCLanguageAPI.listeners.InventoryListener;
import me.timeox2k.smasherMCLanguageAPI.manager.DatabaseManager;
import me.timeox2k.smasherMCLanguageAPI.manager.LanguageManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class SmasherMCLanguageAPI extends JavaPlugin {

    private static SmasherMCLanguageAPI instance;
    private static DatabaseManager databaseManager;
    private static LanguageManager languageManager;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("language").setExecutor(new language_command());

        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        final FileConfiguration config = getConfig();

        getConfig().addDefault("MySQL.host", "localhost");
        getConfig().addDefault("MySQL.username", "root");
        getConfig().addDefault("MySQL.password", "password");
        config.options().copyDefaults(true);
        saveConfig();

        databaseManager = new DatabaseManager();
        languageManager = new LanguageManager(this);

    }

    public static SmasherMCLanguageAPI getInstance() {
        return instance;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static LanguageManager getLanguageManager() {
        return languageManager;
    }

    @Override
    public void onDisable() {
        getDatabaseManager().getDataSource().close();
    }
}
