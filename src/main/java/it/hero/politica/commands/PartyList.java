package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerPartiesStorage;
import it.hero.politica.gui.PartyListGui;
import it.hero.politica.utils.ColorAPI;
import it.hero.politica.utils.PlayerHead;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class PartyList implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        if(!(sender instanceof Player)){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("partyList.consoleInstance")));
            return true;
        }
        Player player = (Player)sender;
        if(!player.hasPermission("elezioni.partylist")){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partyList.noPermission")));
            return true;
        }
        if(args.length != 0){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("elezioni.wrongLength")));
            return true;
        }
        SQLControllerPartiesStorage partiesStorage = new SQLControllerPartiesStorage(plugin);
        if(!partiesStorage.existParty()){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partyList.noParty")));
            return true;
        }
        ArrayList<ItemStack> items = new ArrayList<>();
        for(int i = 0, j=1; i < partiesStorage.getNOfParties(); i++,j++) {
            items.add(PlayerHead.getHead(partiesStorage.getOwnerById(j)));
            ItemMeta meta = items.get(i).getItemMeta();
            String owner = partiesStorage.getOwnerById(j).getName();
            String party = partiesStorage.getOwnerParty(partiesStorage.getOwnerById(j));
            meta.setDisplayName(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partyList.headName"))
                    .replace("%owner%", owner)
                    .replace("%partito%", party)));
            meta.setLore(plugin.getConfig().getStringList("partyList.lore")
                    .stream()
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s.replace("%owner%", owner).replace("%party%", party)))
                    .collect(Collectors.toList()));
            items.get(i).setItemMeta(meta);
        }
        new PartyListGui(items, ColorAPI.color(plugin.getConfig().getString("partyList.guiName")), player);
        return true;
    }
}
