package me.timeox2k.smasherMCLanguageAPI.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LanguageUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String language;
    private final Player player;

    public LanguageUpdateEvent(String language, Player player) {
        this.language = language;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public String getLanguage() {
        return language;
    }

    public Player getPlayer() {
        return player;
    }
}
