package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.datatype.ItemData;
import it.hero.politica.utils.ColorAPI;
import it.hero.politica.utils.CooldownAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class TesseraElettorale implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        if(!(sender instanceof Player)){
            sender.sendMessage(Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("electoralID.consoleInstance"))));
            return true;
        }
        Player player = (Player)sender;
        if(!player.hasPermission("politica.tessera")){
            player.sendMessage(Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("electoralID.noPermission")))
                    .replace("%name%", player.getName()));
            return true;
        }
        if(args.length != 2){
            player.sendMessage(Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("electoralID.wrongLength"))));
            return true;
        }
        if(!args[0].equalsIgnoreCase("give")){
            player.sendMessage(Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("electoralID.wrongLength"))));
            return true;
        }
        Player target = Bukkit.getServer().getPlayer(args[1]);
        if(target == null){
            player.sendMessage(Objects.requireNonNull(ColorAPI.color(plugin.getConfig().getString("electoralID.noOnlineTarget")))
                    .replace("%name%", args[1]));
            return true;
        }
        if(CooldownAPI.isInCooldown(player, player.getName())){
            player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("electoralID.timeToWait"))
                    .replace("%time%", String.valueOf(CooldownAPI.getTimeLeft(player, player.getName())))));
            return true;
        }else{
            List<ItemStack> items = Arrays.asList(target.getInventory().getStorageContents());
            OptionalInt optionalInt = IntStream.range(0, items.size()).
                    filter(s -> items.get(s) == null)
                    .findFirst();
            ItemData item = new ItemData(new ItemStack(Material.STICK));
            plugin.getConfig().getStringList("electoral.ID.lore").forEach(s -> ColorAPI.color(s).replace("%name%", player.getName()));
            item.setItem(161, ColorAPI.color(plugin.getConfig().getString("electoralID.ID.name"))
                            .replace("%name%", target.getName()),
                    plugin.getConfig().getStringList("electoralID.ID.lore"));
            if(!optionalInt.isPresent()){
                player.getWorld().dropItem(player.getLocation(), item.getItem());
                return true;
            }
            player.getInventory().setItem(optionalInt.getAsInt(), item.getItem());
            new CooldownAPI(player, player.getName(), plugin.getConfig().getInt("electoralID.timer")).start();
        }
        return true;
    }
}
