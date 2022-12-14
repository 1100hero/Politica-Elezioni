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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ClickedOnBlock implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Politica plugin = Politica.getPlugin(Politica.class);
        Player player = event.getPlayer();
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND ||
                Objects.requireNonNull(event.getClickedBlock()).getType() != Material.valueOf(plugin.getConfig().getString("elezioni.block"))){
            return;
        }
        if(player.getInventory().getItemInMainHand().getType() != Material.STICK)
            return;
        if(!player.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData())
            return;
        if(player.getInventory().getItemInMainHand().getItemMeta().getCustomModelData() != 161)
            return;
        if(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta().getLore())
                .stream()
                .noneMatch(s -> s.contains(player.getName())))
            return;
        Date date = new Date();
        for(String s : player.getInventory().getItemInMainHand().getItemMeta().getLore()){
            if(!s.contains("Scadenza:")) continue;
            try{
                date = new SimpleDateFormat("dd/MM/yyyy").parse(s.substring(15));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(new Date().compareTo(date) > 0) {
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("voti.expiredID")));
            return;
        }
        int utilizzi=-1;
        for (String s : player.getInventory().getItemInMainHand().getItemMeta().getLore()){
            if(!s.contains("Utilizzi:")) continue;
            utilizzi = Integer.parseInt(String.valueOf(s.charAt(15)));
        }
        if(utilizzi == 0) {
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("voti.expiredUtilizzi")));
            return;
        }
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
