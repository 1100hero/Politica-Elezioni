package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerPartiesStorage;
import it.hero.politica.database.SQLControllerVotes;
import it.hero.politica.utils.ColorAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Elezioni implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        SQLControllerVotes controller = new SQLControllerVotes(plugin);
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);
        TextHologramLine textLine;
        if(!(sender instanceof Player)){
            sender.sendMessage(Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("elezioni.consoleInstance"))));
            return true;
        }
        Player player = (Player)sender;
        final String WRONG_LENGTH = Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("elezioni.wrongLength")));
        if(args.length != 1){
            player.sendMessage(WRONG_LENGTH);
            return true;
        }
        if(!(args[0].equalsIgnoreCase("inizia") || args[0].equalsIgnoreCase("termina"))){
            player.sendMessage(WRONG_LENGTH);
            return true;
        }
        SQLControllerPartiesStorage partiesStorage = new SQLControllerPartiesStorage(plugin);
        if(args[0].equalsIgnoreCase("inizia")){
            if(!player.hasPermission("elezioni.inizia")){
                player.sendMessage(Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("elezioni.iniziaNoPermission"))));
                return true;
            }
            if(!partiesStorage.existParty()){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("elezioni.notParties")));
                return true;
            }
            if(plugin.getConfig().getBoolean("elezioni.data")){
                player.sendMessage(Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("elezioni.alreadyStarted"))));
                return true;
            }
            Bukkit.getOnlinePlayers()
                    .forEach(p -> plugin.getConfig().getStringList("elezioni.iniziaBroadcast")
                            .forEach(msg -> p.sendMessage(ColorAPI.color(msg))));
            Hologram holo = api.createHologram(new Location(Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("elezioni.Hologram.position.world"))),
                    plugin.getConfig().getInt("elezioni.Hologram.position.x"), plugin.getConfig()
                    .getInt("elezioni.Hologram.position.y"), plugin.getConfig().getInt("elezioni.Hologram.position.z")));
            List<String> lines = plugin.getConfig().getStringList("elezioni.Hologram.text")
                    .stream().map(s -> ColorAPI.color(s).replace("%voti%", PlaceholderAPI.setPlaceholders(player, "%Politica_votes%"))).collect(Collectors.toList());
            for(int i=0; i<lines.size(); i++)
                textLine = holo.getLines().insertText(i, lines.get(i));
            plugin.getConfig().set("elezioni.data", true);
            plugin.saveDefaultConfig();
            controller.createTable();
        }else{
            if(!player.hasPermission("elezioni.termina")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("elezioni.terminaNoPermission")));
                return true;
            }
            if(!plugin.getConfig().getBoolean("elezioni.data")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("elezioni.notStarted")));
                return true;
            }
            api.deleteHolograms();
            plugin.getConfig().set("elezioni.data", false);
            plugin.saveDefaultConfig();
            Bukkit.getOnlinePlayers()
                    .forEach(p -> plugin.getConfig().getStringList("elezioni.terminaBroadcast")
                            .forEach(m -> p.sendMessage(ColorAPI.color(m))));
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("elezioni.percentage"))
                    p.sendMessage(ColorAPI.color(plugin.getConfig().getString("elezioni.electionPercentage")).replace("%voti%", PlaceholderAPI.setPlaceholders(player, "%Politica_votes%")));
            });
            partiesStorage.resetVotes();
            controller.deleteTable();
        }
        return true;
    }
}
