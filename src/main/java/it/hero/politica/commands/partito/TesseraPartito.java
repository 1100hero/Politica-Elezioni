package it.hero.politica.commands.partito;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerParties;
import it.hero.politica.database.SQLControllerPartiesStorage;
import it.hero.politica.datatype.ItemData;
import it.hero.politica.enums.DataBlockID;
import it.hero.politica.gui.PartitoListGui;
import it.hero.politica.utils.ColorAPI;
import it.hero.politica.utils.CooldownAPI;

import it.hero.politica.utils.PlayerHead;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TesseraPartito implements CommandExecutor {

    private static HashMap<Player, Player> map = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        SQLControllerParties controller = new SQLControllerParties(plugin);
        SQLControllerPartiesStorage partiesStorage = new SQLControllerPartiesStorage(plugin);
        if(!(sender instanceof Player)){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.consoleInstance")));
            return true;
        }
        Player player = (Player)sender;
        if(args.length != 2){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.wrongLength")));
            return true;
        }
        if(args[0].equalsIgnoreCase("tessera")){
            if(!controller.existTable() || !controller.isPlayerInParty(player)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notInAParty")));
                return true;
            }
            if(!player.hasPermission("elezioni.partito.tessera")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.noPermissionTesseraPlayer")));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(PartitoChecker.isTargetNull(target)){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.notOnline")).replace("%name%", args[1])));
                return true;
            }
            if(PartitoChecker.isTargetEqualsToPlayer(player, target)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notSelfCmd")));
                return true;
            }
            if(controller.isPlayerInParty(target)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.targetInParty")));
                return true;
            }
            if(CooldownAPI.isInCooldown(player, player.getName())){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.timeLeft").replace("%time%",
                        String.valueOf(CooldownAPI.getTimeLeft(player, player.getName()))))));
            }else{
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.sentRequest")).replace("%name%", target.getName())));
                TextComponent yesMessage = new TextComponent(ColorAPI.color(plugin.getConfig().getString("partito.yesMessage")));
                yesMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.yesHoverMessage")).replace("%partito%", controller.getPlayerPartyName(player))))));
                yesMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/partito tesseramento accept"));
                TextComponent noMessage = new TextComponent(ColorAPI.color(plugin.getConfig().getString("partito.noMessage")));
                noMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ColorAPI.color(plugin.getConfig().getString("partito.noHoverMessage")).replace("%partito%", controller.getPlayerPartyName(player)))));
                noMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/partito tesseramento reject"));
                target.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.targetMsg")).replace("%partito%", controller.getPlayerPartyName(player)));
                TextComponent space = new TextComponent(" ");
                target.spigot().sendMessage(yesMessage,space,noMessage);
                map.put(target, player);
                new CooldownAPI(player, player.getName(), plugin.getConfig().getInt("partito.cooldownRequest")).start();
            }
            return true;
        }else if(args[0].equalsIgnoreCase("tesseramento")){
            if(!map.containsKey(player)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notInvited")));
                return true;
            }
            Player whoInvited = map.get(player);
            if(args[1].equalsIgnoreCase("accept")){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.acceptedTargetMsg")).replace("%partito%", controller
                        .getPlayerPartyName(whoInvited))));
                whoInvited.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.acceptedPlayerMsg")).replace("%name%", player.getName())));
                controller.insertPlayer(player, controller.getPlayerPartyName(whoInvited));
                List<ItemStack> items = Arrays.asList(player.getInventory().getStorageContents());
                OptionalInt optionalInt = IntStream.range(0, items.size()).
                        filter(s -> items.get(s) == null)
                        .findFirst();
                ItemData item = new ItemData(new ItemStack(Material.STICK));
                plugin.getConfig().getStringList("partito.item.lore").forEach(ColorAPI::color);
                item.setItem(DataBlockID.valueOf(partiesStorage.getColor(controller.getPlayerPartyName(whoInvited))).getValue(), ColorAPI.color(plugin.getConfig().getString("partito.item.name"))
                                .replace("%partito%", controller.getPlayerPartyName(whoInvited)),
                        plugin.getConfig().getStringList("partito.item.lore").stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s.replace("%orientation%", partiesStorage.getOrientation(controller.getPlayerPartyName(whoInvited))))).collect(Collectors.toList()));
                if(!optionalInt.isPresent()){
                    player.getWorld().dropItem(player.getLocation(), item.getItem());
                    return true;
                }
                player.getInventory().setItem(optionalInt.getAsInt(), item.getItem());
                map.remove(player);
            }else if(args[1].equalsIgnoreCase("reject")){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.refusedTargetMsg"))
                        .replace("%partito%", controller.getPlayerPartyName(whoInvited))));
                whoInvited.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.refusedPlayerMsg")).replace("%name%", player.getName())));
                map.remove(player);
            }else{
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.wrongLength")));
            }
            return true;
        }else if(args[0].equalsIgnoreCase("remove")){
            if(!controller.existTable() || !controller.isPlayerInParty(player)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notInAParty")));
                return true;
            }
            if(!player.hasPermission("elezioni.partito.remove")){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.noPermissionRemove"))));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(PartitoChecker.isTargetNull(target)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notOnline")).replace("%name%", args[1]));
                return true;
            }
            if (commonChecker(plugin, controller, partiesStorage, player, target)) return true;
            if(plugin.getConfig().getBoolean("elezioni.data")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.startedElections")));
                return true;
            }
            controller.removePlayerFromParty(target);
            player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.removedTarget")).replace("%name%", target.getName())));
        }else if(args[0].equalsIgnoreCase("promote")){
            if(!controller.existTable() || !controller.isPlayerInParty(player)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notInAParty")));
                return true;
            }
            if(!player.hasPermission("elezioni.partito.promote")){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.noPermissionPromote"))));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(PartitoChecker.isTargetNull(target)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notOnline")).replace("%name%", args[1]));
                return true;
            }
            if(PartitoChecker.isTargetEqualsToPlayer(player, target)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notSelfCmd")));
                return true;
            }
            if(!(controller.getPlayerPartyName(player).equals(controller.getPlayerPartyName(target)) || controller.isPlayerInParty(target))){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.anotherParty")));
                return true;
            }
            if(target.hasPermission("elezioni.partito.tessera")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.alreadyPromoted")));
                return true;
            }
            if(plugin.getConfig().getBoolean("elezioni.data")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.startedElections")));
                return true;
            }
            Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), "lp user " +target.getName()+ " permission set elezioni.partito.tessera true");
            player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.promotedTarget")).replace("%name%", target.getName())));
            target.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.promoted")));
        }else if(args[0].equalsIgnoreCase("demote")){
            if(!controller.existTable() || !controller.isPlayerInParty(player)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notInAParty")));
                return true;
            }
            if(!player.hasPermission("elezioni.partito.demote")){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.noPermissionDemote"))));
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(PartitoChecker.isTargetNull(target)){
                player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.notOnline")).replace("%name%", args[1])));
                return true;
            }
            if (commonChecker(plugin, controller, partiesStorage, player, target)) return true;
            if(!target.hasPermission("elezioni.partito.tessera")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.alreadyDemoted")));
                return true;
            }
            if(plugin.getConfig().getBoolean("elezioni.data")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.startedElections")));
                return true;
            }
            Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), "lp user "+target.getName()+" permission unset elezioni.partito.tessera");
            player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.demotedTarget")).replace("%name%", target.getName())));
        }else if(args[0].equalsIgnoreCase("lista")){
            if(!controller.existTable() || !controller.isPlayerInParty(player)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notInAParty")));
                return true;
            }
            if(!player.hasPermission("elezioni.partito.lista")){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.noPermissionList")));
                return true;
            }
            if(!controller.isPlayerInParty(player)){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notInAParty")));
                return true;
            }
            if(!controller.existParty(args[1])){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.partyNotExist")));
                return true;
            }
            if(!controller.existTable() || !controller.isPlayerInExactParty(player, args[1])){
                player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.wrongParty")));
                return true;
            }
            ArrayList<ItemStack> items = new ArrayList<>();
            for(int i = 0, j= controller.getFirstId(args[1]); i < controller.getNOfPlayerInParty(args[1]); i++,j++) {
                items.add(PlayerHead.getHead(controller.getPlayerById(args[1], j)));
                ItemMeta meta = items.get(i).getItemMeta();
                String name = controller.getPlayerById(args[1], j).getName();
                meta.setDisplayName(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.headName"))
                        .replace("%name%", name)
                        .replace("%partito%", args[1])));
                meta.setLore(plugin.getConfig().getStringList("partito.lore")
                        .stream()
                        .map(s -> ChatColor.translateAlternateColorCodes('&', s.replace("%name%", name).replace("%partito%", args[1])))
                        .collect(Collectors.toList()));
                items.get(i).setItemMeta(meta);
            }
            new PartitoListGui(items, ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.guiName")).replace("%partito%", args[1])), player);
        }else{
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.wrongLength")));
        }
        return true;
    }

    private boolean commonChecker(Politica plugin, SQLControllerParties controller, SQLControllerPartiesStorage partiesStorage, Player player, Player target) {
        if(PartitoChecker.isTargetEqualsToPlayer(player, target)){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notSelfCmd")));
            return true;
        }
        if(!(controller.getPlayerPartyName(player).equals(controller.getPlayerPartyName(target)) || controller.isPlayerInParty(target))){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.anotherParty")));
            return true;
        }
        if(partiesStorage.isOwner(target)){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.commandOnLeader")));
            return true;
        }
        return false;
    }
}
