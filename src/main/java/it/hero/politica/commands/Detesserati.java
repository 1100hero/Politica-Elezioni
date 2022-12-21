package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerParties;
import it.hero.politica.database.SQLControllerPartiesStorage;
import it.hero.politica.utils.ColorAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Detesserati implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        if(!(sender instanceof Player)){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("detesserati.consoleInstance")));
            return true;
        }
        Player player = (Player)sender;
        if(!player.hasPermission("elezioni.detesserati")){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("detesserati.noPermission")));
            return true;
        }
        if(args.length != 0){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("elezioni.wrongLength")));
            return true;
        }
        SQLControllerParties controller = new SQLControllerParties(plugin);
        if(!controller.existTable()) return true;
        if(!controller.isPlayerInParty(player)){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("detesserati.notInAParty")));
            return true;
        }
        if(new SQLControllerPartiesStorage(plugin).isOwner(player)){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("detesserati.leaderBlock")));
            return true;
        }
        player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("detesserati.success"))
                .replace("%partito%", controller.getPlayerPartyName(player))));
        controller.removePlayerFromParty(player);
        return true;
    }
}
