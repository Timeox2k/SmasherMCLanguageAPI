package me.timeox2k.smasherMCLanguageAPI.commands;

import me.timeox2k.smasherMCLanguageAPI.SmasherMCLanguageAPI;
import me.timeox2k.smasherMCLanguageAPI.manager.DatabaseManager;
import me.timeox2k.smasherMCLanguageAPI.manager.LanguageManager;
import me.timeox2k.smasherMCLanguageAPI.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class language_command implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) return true;

        DatabaseManager databaseManager = SmasherMCLanguageAPI.getDatabaseManager();
        LanguageManager languageManager = SmasherMCLanguageAPI.getLanguageManager();
        List<DatabaseManager.Language> languages = databaseManager.getAllLanguages();
        int currentLanguage = databaseManager.getPlayerLanguage(player);

        Inventory inventory = Bukkit.createInventory(null, 9, languageManager.getMessage(player, "messages.inventory-title"));

        int slot = 0;
        for (DatabaseManager.Language language : languages) {
            Material material = (language.getId() == currentLanguage) ? Material.ENCHANTED_BOOK : Material.PAPER;

            String currentLanguageText = languageManager.getMessage(player, "messages.current-language");
            String displayName = (language.getId() == currentLanguage) ?
                    "§a" + language.getInternationalName() + " §7" + currentLanguageText :
                    "§6" + language.getInternationalName();

            String clickToSelectText = languageManager.getMessage(player, "messages.click-to-select");
            ItemBuilder item = new ItemBuilder(material).setName(displayName).addLore(clickToSelectText);

            if (language.getId() == currentLanguage) {
                String alreadySelectedLore = languageManager.getMessage(player, "messages.already-selected-lore");
                item.addLore(alreadySelectedLore);
            }

            inventory.setItem(slot++, item.get());
        }

        player.openInventory(inventory);
        return true;
    }
}
