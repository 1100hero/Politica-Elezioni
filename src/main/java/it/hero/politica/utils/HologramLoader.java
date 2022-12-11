package it.hero.politica.utils;

import it.hero.politica.Politica;
import me.clip.placeholderapi.PlaceholderAPI;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HologramLoader {
    public static void checkStartedElections(){
        Politica plugin = Politica.getPlugin(Politica.class);
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);
        TextHologramLine text;
        if(plugin.getConfig().getBoolean("elezioni.data")){
            if(api.getHolograms().size() == 0){
                api.createHologram(new Location(Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("elezioni.Hologram.position.world"))),
                        plugin.getConfig().getInt("elezioni.Hologram.position.x"), plugin.getConfig()
                        .getInt("elezioni.Hologram.position.y"), plugin.getConfig().getInt("elezioni.Hologram.position.z")));
                Hologram holo = api.createHologram(new Location(Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("elezioni.Hologram.position.world"))),
                        plugin.getConfig().getInt("elezioni.Hologram.position.x"), plugin.getConfig()
                        .getInt("elezioni.Hologram.position.y"), plugin.getConfig().getInt("elezioni.Hologram.position.z")));
                List<String> lines = plugin.getConfig().getStringList("elezioni.Hologram.text")
                        .stream().map(s -> ColorAPI.color(s).replace("%voti%", PlaceholderAPI.setPlaceholders(null, "%Politica_votes%"))).collect(Collectors.toList());
                for(int i=0; i<lines.size(); i++)
                    text = holo.getLines().insertText(i, lines.get(i));
            }
        }
    }
}
