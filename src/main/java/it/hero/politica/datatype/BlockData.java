package it.hero.politica.datatype;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BlockData {

    @Getter private ItemStack block;

    public BlockData(ItemStack block) {
        this.block = block;
    }

    public void setItem(int id, String name, List<String> lore){
        ItemMeta meta = block.getItemMeta();
        meta.setCustomModelData(id);
        meta.setDisplayName(name);
        meta.setLore(lore);
        block.setItemMeta(meta);
    }

    public String getDisplayName() {
        return getBlock().getItemMeta().getDisplayName();
    }
}
