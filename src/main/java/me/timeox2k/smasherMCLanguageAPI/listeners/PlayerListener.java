package me.timeox2k.smasherMCLanguageAPI.listeners;

import me.timeox2k.smasherMCLanguageAPI.SmasherMCLanguageAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        final Player player = event.getPlayer();

        SmasherMCLanguageAPI.getDatabaseManager().createPlayer(player);
    }

}
