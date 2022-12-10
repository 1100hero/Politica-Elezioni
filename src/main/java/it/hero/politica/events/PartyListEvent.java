package it.hero.politica.events;

import it.hero.politica.gui.PartyListGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class PartyListEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        if(!PartyListGui.getUsers().containsKey(p.getUniqueId())) return;
        PartyListGui inv = PartyListGui.getUsers().get(p.getUniqueId());
        if(Objects.requireNonNull(event.getCurrentItem()).getItemMeta().getDisplayName().equals(PartyListGui.getNextPage())){
            event.setCancelled(true);
            if(inv.currPage >= inv.getPages().size()-1){
                return;
            }else{
                ++inv.currPage;
                p.openInventory(inv.getPages().get(inv.currPage));
            }
        }else if(event.getCurrentItem().getItemMeta().getDisplayName().equals(PartyListGui.getPrevPage())){
            event.setCancelled(true);
            if(inv.currPage > 0){
                --inv.currPage;
                p.openInventory(inv.getPages().get(inv.currPage));
            }
        }
    }
}
