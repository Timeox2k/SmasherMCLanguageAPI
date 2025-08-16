package me.timeox2k.smasherMCLanguageAPI.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LanguageUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String language;

    public LanguageUpdateEvent(String language) {
        this.language = language;
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
}
