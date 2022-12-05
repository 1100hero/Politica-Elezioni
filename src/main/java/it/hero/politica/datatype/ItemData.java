package it.hero.politica.datatype;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemData {

    @Getter private ItemStack item;

    public ItemData(ItemStack item) {
        this.item = item;
    }

    public void setItem(int id, String name, List<String> lore){
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(id);
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public String getDisplayName() {
        return getItem().getItemMeta().getDisplayName();
    }
}
