package me.timeox2k.smasherMCLanguageAPI.listeners;

import me.timeox2k.smasherMCLanguageAPI.SmasherMCLanguageAPI;
import me.timeox2k.smasherMCLanguageAPI.manager.DatabaseManager;
import me.timeox2k.smasherMCLanguageAPI.manager.LanguageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCurrentItem();
        if (item.getItemMeta() == null) return;

        LanguageManager languageManager = SmasherMCLanguageAPI.getLanguageManager();

        String expectedTitle = languageManager.getMessage(player, "inventory-title");

        if (event.getView().title().equals(expectedTitle)) {
            event.setCancelled(true);

            if (!item.getItemMeta().hasDisplayName()) return;

            String displayName = item.getItemMeta().getDisplayName();
            DatabaseManager databaseManager = SmasherMCLanguageAPI.getDatabaseManager();

            String currentLanguageText = languageManager.getMessage(player, "current-language");
            if (displayName.contains(currentLanguageText)) {
                player.sendMessage(languageManager.getMessage(player, "already-selected"));
                return;
            }

            String languageName = displayName.substring(2);

            int languageId = databaseManager.getLanguageIdByName(languageName);

            if (languageId != -1) {
                databaseManager.setPlayerLanguage(player, languageId);

                player.sendMessage(languageManager.getMessage(player, "language-changed", "language", languageName));

                player.closeInventory();
            } else {
                player.sendMessage(languageManager.getMessage(player, "language-error"));
            }
        }
    }
}
