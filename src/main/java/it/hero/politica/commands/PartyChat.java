package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerParties;
import it.hero.politica.database.SQLControllerPartiesStorage;
import it.hero.politica.enums.ColorID;
import it.hero.politica.utils.ColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PartyChat implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        if(!(sender instanceof Player)){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("partyChat.consoleInstance")));
            return true;
        }
        Player player = (Player) sender;
        SQLControllerParties controller = new SQLControllerParties(plugin);
        if(!controller.isPlayerInParty(player) || !controller.existTable()){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partyChat.notInAParty")));
            return true;
        }
        if(args.length == 0) return true;
        SQLControllerPartiesStorage controllerParties = new SQLControllerPartiesStorage(plugin);
        List<String> list = new ArrayList<>(Arrays.asList(args));
        StringBuilder message = new StringBuilder();
        list.forEach(s->message.append(" ").append(s));
        for (Player p : Bukkit.getOnlinePlayers()){
            if(!controller.isPlayerInParty(p)) continue;
            if(!controller.isPlayerInExactParty(p, player)) continue;
            p.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partyChat.message"))
                    .replace("%color%", ColorID.valueOf(controllerParties.getColor(controller.getPlayerPartyName(player))).getColorID())
                    .replace("%party%", controller.getPlayerPartyName(player).toUpperCase())
                    .replace("%name%", player.getName())
                    .replace("%message%", message.substring(1))));
        }
        return true;
    }
}
