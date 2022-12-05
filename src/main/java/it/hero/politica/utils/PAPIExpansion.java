package it.hero.politica.utils;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerPartiesStorage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class PAPIExpansion extends PlaceholderExpansion {
    private final Politica plugin = Politica.getPlugin(Politica.class);
    @Override
    public @NotNull String getIdentifier() {
        return plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        SQLControllerPartiesStorage partiesStorage = new SQLControllerPartiesStorage(plugin);
        if(identifier.equals("votes")){
            int votes = partiesStorage.getAllVotes();
            return ""+votes;
        }
        return null;
    }
}
