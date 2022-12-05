package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerParties;
import it.hero.politica.datatype.ItemData;
import it.hero.politica.utils.ColorAPI;
import it.hero.politica.utils.CooldownAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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

public class TesseraPartito implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        SQLControllerParties controller = new SQLControllerParties(plugin);
        if(!(sender instanceof Player)){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.consoleInstance")));
            return true;
        }
        Player player = (Player)sender;
        if(args.length != 2){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.manageWronghLength")));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if(args[0].equalsIgnoreCase("tessera")){
            if(!player.hasPermission("elezioni.partito.tessera")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("noPermissionTesseraPlayer")));
                return true;
            }
            if(target == null){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.notOnline")).replace("%name%", args[1])));
                return true;
            }
            if(CooldownAPI.isInCooldown(player, player.getName())){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("timeLeft").replace("%time%",
                        String.valueOf(CooldownAPI.getTimeLeft(player, player.getName()))))));
            }else{
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.sentRequest")).replace("%name%", target.getName())));
                TextComponent yesMessage = new TextComponent(ColorAPI.color(plugin.getConfig().getString("partito.yesMessage")));
                yesMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.yesHoverMessage")).replace("%partito%", controller.getPlayerPartyName(player))))));
                yesMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/politica tesseramento accept"));
                TextComponent noMessage = new TextComponent(ColorAPI.color("partito.noMessage"));
                noMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ColorAPI.color(plugin.getConfig().getString("partito.noHoverMessage")).replace("%partito%", controller.getPlayerPartyName(player)))));
                noMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/politica tesseramento reject"));
                target.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.targetMsg")).replace("%partito%", controller.getPlayerPartyName(player)));
                TextComponent space = new TextComponent(" ");
                target.spigot().sendMessage(yesMessage,space,noMessage);
                new CooldownAPI(player, player.getName(), plugin.getConfig().getInt("partito.cooldownRequest")).start();
            }
            return true;
        }else if(args[0].equalsIgnoreCase("tesseramento")){
            if(args[1].equalsIgnoreCase("accept")){
                target.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("acceptedTargetMsg")).replace("%partito%", controller.getPlayerPartyName(player))));
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("acceptedPlayerMsg")).replace("%name%", target.getName())));
                controller.insertPlayer(target, controller.getPlayerPartyName(player));
                List<ItemStack> items = Arrays.asList(target.getInventory().getStorageContents());
                OptionalInt optionalInt = IntStream.range(0, items.size()).
                        filter(s -> items.get(s) == null)
                        .findFirst();
                ItemData item = new ItemData(new ItemStack(Material.STICK));
                plugin.getConfig().getStringList("partito.item.lore").forEach(s -> ColorAPI.color(s).replace("%name%", target.getName()));
                item.setItem(161, ColorAPI.color(plugin.getConfig().getString("partito.item.name"))
                                .replace("%partito%", controller.getPlayerPartyName(player)),
                        plugin.getConfig().getStringList("partito.item.lore"));
                if(!optionalInt.isPresent()){
                    player.getWorld().dropItem(player.getLocation(), item.getItem());
                    return true;
                }
                player.getInventory().setItem(optionalInt.getAsInt(), item.getItem());
            }else if(args[1].equalsIgnoreCase("reject")){
                target.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("refusedTargetMsg")).replace("%partito%", controller.getPlayerPartyName(player))));
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("refusedPlayerMsg")).replace("%name%", target.getName())));
            }else{
                target.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.wrongLength")));
            }
            return true;
        }
        return true;
    }
}
