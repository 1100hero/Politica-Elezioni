package it.hero.politica.gui;

import it.hero.politica.Politica;
import it.hero.politica.utils.ColorAPI;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class VoteGui implements Listener {
    @Getter private Inventory inventory;
    private Politica plugin = Politica.getPlugin(Politica.class);

    public VoteGui(){
        inventory = Bukkit.createInventory(null, plugin.getConfig().getInt("voti.gui.size"),
                ColorAPI.color(plugin.getConfig().getString("voti.gui.name")));
        addItems();
    }
    private void addItems(){
        LinkedList<String> politicalPartiesName = new LinkedList<>(plugin.getConfig().getConfigurationSection("voti.gui.political-parties").getKeys(false));
        politicalPartiesName.forEach(s -> {
            inventory.setItem(plugin.getConfig().getInt("voti.gui.political-parties."+s+".position"),
                    createGuiItem(Material.valueOf(plugin.getConfig().getString("voti.gui.political-parties."+s+".material")),
                            Objects.requireNonNull(plugin.getConfig().getString("voti.gui.political-parties." + s + ".name")).replace("%partito%",s),
                            plugin.getConfig().getStringList("voti.gui.political-parties."+s+".lore")));
        });
    }
    private ItemStack createGuiItem(final Material material, final String name, final List<String> lore){
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorAPI.color(name));
        meta.setLore(ColorAPI.color(lore));
        item.setItemMeta(meta);
        return item;
    }

    public void openInventory(final HumanEntity entity){
        entity.openInventory(inventory);
    }

}
