package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.utils.ColorAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadConfig implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        if(!sender.hasPermission("elezioni.reload")){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("reload.noPermission")));
            return true;
        }
        if(args.length != 1 || !args[0].equalsIgnoreCase("reload")){
           sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("reload.wrongLength")));
            return true;
        }
        plugin.reloadConfig();
        sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("reload.configReloaded")));
        return true;
    }
}
