package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.datatype.BlockData;
import it.hero.politica.utils.ColorAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GiveCustomVoteBlock implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        if(!(sender instanceof Player)){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("givevoteblock.consoleInstance")));
            return true;
        }
        Player player = (Player)sender;
        if(!player.hasPermission("elezioni.givevoteblock")){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("givevoteblock.noPermission")));
            return true;
        }
        if(args.length != 0){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("givevoteblock.wrongLength")));
            return true;
        }
        BlockData blockData = new BlockData(new ItemStack(Material.valueOf(plugin.getConfig().getString("elezioni.block"))));
        blockData.setItem(plugin.getConfig().getInt("voteblock.data"),
                ColorAPI.color(plugin.getConfig().getString("voteblock.blockName")),
                plugin.getConfig().getStringList("voteblock.lore").stream()
                        .map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        List<ItemStack> items = Arrays.asList(player.getInventory().getStorageContents());
        OptionalInt optionalInt = IntStream.range(0, items.size()).
                filter(s -> items.get(s) == null)
                .findFirst();
        if(!optionalInt.isPresent()){
            player.getWorld().dropItem(player.getLocation(), blockData.getBlock());
            return true;
        }
        player.getInventory().setItem(optionalInt.getAsInt(), blockData.getBlock());
        player.sendMessage(ColorAPI.color(plugin.getConfig().getString("voteblock.givedBlock")));
        return true;
    }
}
