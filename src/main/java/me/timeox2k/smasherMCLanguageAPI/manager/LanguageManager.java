package me.timeox2k.smasherMCLanguageAPI.manager;

import me.timeox2k.smasherMCLanguageAPI.SmasherMCLanguageAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private final Map<String, FileConfiguration> languageFiles = new HashMap<>();
    private final SmasherMCLanguageAPI plugin;

    public LanguageManager(SmasherMCLanguageAPI plugin) {
        this.plugin = plugin;
        loadLanguageFiles();
    }

    private void loadLanguageFiles() {
        createLanguageFile("german.yml");
        createLanguageFile("english.yml");

        loadLanguageFile("german");
        loadLanguageFile("english");
    }

    private void createLanguageFile(String fileName) {
        File langFile = new File(plugin.getDataFolder(), "languages/" + fileName);
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            try (InputStream inputStream = plugin.getResource("languages/" + fileName)) {
                if (inputStream != null) {
                    Files.copy(inputStream, langFile.toPath());
                } else {
                    createDefaultLanguageFile(langFile, fileName);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create language file " + fileName + ": " + e.getMessage());
            }
        }
    }

    private void createDefaultLanguageFile(File file, String fileName) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (fileName.equals("german.yml")) {
            config.set("messages.language-changed", "§aSpache erfolgreich zu {language} geändert!");
            config.set("messages.language-error", "§cFehler: Sprache konnte nicht geändert werden!");
            config.set("messages.already-selected", "§7Du hast bereits diese Sprache ausgewählt!");
            config.set("messages.current-language", "(Aktuelle Sprache)");
            config.set("messages.inventory-title", "§6Wähle deine neue Sprache");
            config.set("messages.click-to-select", "§7Klicke um diese Sprache auszuwählen");
            config.set("messages.already-selected-lore", "§a✓ Bereits ausgewählt");
        } else if (fileName.equals("english.yml")) {
            config.set("messages.language-changed", "§aLanguage successfully changed to {language}!");
            config.set("messages.language-error", "§cError: Could not change language!");
            config.set("messages.already-selected", "§7You already have this language selected!");
            config.set("messages.current-language", "(Current Language)");
            config.set("messages.inventory-title", "§6Select your new Language");
            config.set("messages.click-to-select", "§7Click to select this language");
            config.set("messages.already-selected-lore", "§a✓ Already selected");
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save language file " + fileName + ": " + e.getMessage());
        }
    }

    private void loadLanguageFile(String languageName) {
        File langFile = new File(plugin.getDataFolder(), "languages/" + languageName + ".yml");
        if (langFile.exists()) {
            languageFiles.put(languageName, YamlConfiguration.loadConfiguration(langFile));
        }
    }

    public String getMessage(Player player, String key) {
        int languageId = SmasherMCLanguageAPI.getDatabaseManager().getPlayerLanguage(player);
        String languageName = getLanguageNameById(languageId);
        return getMessage(languageName, key);
    }

    public String getMessage(String languageName, String key) {
        FileConfiguration langConfig = languageFiles.get(languageName.toLowerCase());

        if (langConfig != null) {
            String result = null;

            if (langConfig.isConfigurationSection(key)) {
                result = langConfig.getString(key + ".name", null);
            } else {
                result = langConfig.getString(key, null);
            }

            if (result == null) {
                result = "Message not found: " + key;
            }

            return result;
        }

        FileConfiguration englishConfig = languageFiles.get("english");
        if (englishConfig != null) {
            String result = null;

            if (englishConfig.isConfigurationSection(key)) {
                result = englishConfig.getString(key + ".name", null);
            } else {
                result = englishConfig.getString(key, null);
            }

            if (result == null) {
                result = "Message not found: " + key;
            }
            return result;
        }

        return "Message not found: " + key;
    }
    public String getMessage(Player player, String key, String... placeholders) {
        String message = getMessage(player, key);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return message;
    }

    private String getLanguageNameById(int languageId) {
       return SmasherMCLanguageAPI.getDatabaseManager().getAllLanguages().get(languageId - 1).getInternationalName().toLowerCase();
    }
}