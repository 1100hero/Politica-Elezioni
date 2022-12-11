package it.hero.politica.events;

import it.hero.politica.Politica;
import it.hero.politica.gui.VoteGui;
import it.hero.politica.database.SQLControllerVotes;
import it.hero.politica.utils.ColorAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ClickedOnBlock implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Politica plugin = Politica.getPlugin(Politica.class);
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND &&
                Objects.requireNonNull(event.getClickedBlock()).getType() == Material.valueOf(plugin.getConfig().getString("elezioni.block"))){
                if(player.getInventory().getItemInMainHand().getItemMeta().getCustomModelData() == 161 && Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta().getLore())
                        .stream()
                        .anyMatch(s -> s.contains(player.getName()))){
                    AtomicInteger utilizzi= new AtomicInteger();
                    player.getInventory().getItemInMainHand().getItemMeta().getLore().forEach(s -> {
                        if(s.startsWith("Utilizzi:")){
                            utilizzi.set(s.charAt(16));
                        }
                    });
                    //if(utilizzi.get() == 0) return;
                    if(!plugin.getConfig().getBoolean("elezioni.data")){
                        player.sendMessage(ColorAPI.color(plugin.getConfig().getString("elezioni.notStarted")));
                        return;
                    }
                    SQLControllerVotes controller = new SQLControllerVotes(plugin);
                    if(controller.existPlayer(player)){
                        player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("voti.alreadyVoted"))));
                        return;
                    }
                    VoteGui voteGui = new VoteGui();
                    voteGui.openInventory(player);
                }
        }
    }
}
