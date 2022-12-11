package it.hero.politica.gui;

import it.hero.politica.Politica;
import it.hero.politica.utils.ColorAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PartitoListGui {
    @Getter
    private ArrayList<Inventory> pages = new ArrayList<>();
    @Getter private static HashMap<UUID, PartitoListGui> users = new HashMap<>();
    public int currPage = 0;
    private UUID id;

    private static Politica plugin = Politica.getPlugin(Politica.class);

    public PartitoListGui(ArrayList<ItemStack> items, String name, Player player) {
        this.id = UUID.randomUUID();
        Inventory page = getBlankPage(name);
        for(int i = 0; i < items.size(); i++) {
            if(page.firstEmpty() == 46){
                pages.add(page);
                page = getBlankPage(name);
                page.setItem(i, items.get(i));
            }else{
                page.setItem(i, items.get(i));
            }
            pages.add(page);
            player.openInventory(pages.get(currPage));
            users.put(player.getUniqueId(), this);
        }
    }

    @Getter private static String nextPage = ColorAPI.color(plugin.getConfig().getString("partito.nextPage"));
    @Getter private static String prevPage = ColorAPI.color(plugin.getConfig().getString("partito.prevPage"));

    private Inventory getBlankPage(String name) {
        Inventory page = Bukkit.createInventory(null, 54, name);

        ItemStack nextPage = new ItemStack(Material.valueOf(plugin.getConfig().getString("partito.prevPageItem")), 1, (byte) 2);
        ItemMeta meta = nextPage.getItemMeta();
        meta.setDisplayName(getNextPage());
        meta.setCustomModelData(plugin.getConfig().getInt("partito.nextData"));
        nextPage.setItemMeta(meta);

        ItemStack prevPage = new ItemStack(Material.valueOf(plugin.getConfig().getString("partito.nextPageItem")), 1, (byte) 2);
        meta = prevPage.getItemMeta();
        meta.setDisplayName(getPrevPage());
        meta.setCustomModelData(plugin.getConfig().getInt("partito.prevData"));
        prevPage.setItemMeta(meta);

        page.setItem(plugin.getConfig().getInt("partito.nextItemPosition"), nextPage);
        page.setItem(plugin.getConfig().getInt("partito.prevItemPosition"), prevPage);
        return page;
    }
}
