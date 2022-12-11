package it.hero.politica.events;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerPartiesStorage;
import it.hero.politica.gui.VoteGui;
import it.hero.politica.database.SQLControllerVotes;
import it.hero.politica.utils.ColorAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VoteEvent implements Listener {
    @EventHandler
    public void onMoveInventory(final InventoryClickEvent event){
        Politica plugin = Politica.getPlugin(Politica.class);
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);
        VoteGui inventory = new VoteGui();
        LinkedList<String> politicalPartiesName = new LinkedList<>(plugin.getConfig().getConfigurationSection("voti.gui.political-parties").getKeys(false));
        SQLControllerVotes controller = new SQLControllerVotes(plugin);
        SQLControllerPartiesStorage controllerPartiesStorage = new SQLControllerPartiesStorage(plugin);
        TextHologramLine textLine = null;
        if(event.getInventory().getType().name().equals(inventory.getInventory().getType().name())){
            api.deleteHolograms();
            List<String> lines = ColorAPI.color(plugin.getConfig().getStringList("elezioni.Hologram.text")
                    .stream().map(s -> s.replace("%voti%", PlaceholderAPI.setPlaceholders((Player) event.getWhoClicked(), "%Politica_votes%"))).collect(Collectors.toList()));
            for(String s : politicalPartiesName){
                if(plugin.getConfig().getInt("voti.gui.political-parties."+s+".position") == event.getSlot()){
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    event.getWhoClicked().sendMessage(ColorAPI.color(plugin.getConfig().getString("voti.voted")).replace("%partito%", s));
                    event.getInventory().close();
                    Player player = (Player) event.getWhoClicked();
                    player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("voti.votedSound")),
                            plugin.getConfig().getInt("voti.volume"),
                            plugin.getConfig().getInt("voti.pitch"));
                    controller.insertPlayer(player, s);
                    controllerPartiesStorage.updateVotes(s, controllerPartiesStorage.getVoteByParty(s)+1);
                }
            }
            Hologram holo = api.createHologram(new Location(Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("elezioni.Hologram.position.world"))),
                    plugin.getConfig().getInt("elezioni.Hologram.position.x"), plugin.getConfig()
                    .getInt("elezioni.Hologram.position.y"), plugin.getConfig().getInt("elezioni.Hologram.position.z")));
            for(int i=0; i<lines.size(); i++)
                textLine = holo.getLines().insertText(i, lines.get(i));
            event.setCancelled(true);
        }
    }
}
