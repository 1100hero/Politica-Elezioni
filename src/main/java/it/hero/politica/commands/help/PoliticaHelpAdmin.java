package it.hero.politica.commands.help;

import it.hero.politica.Politica;
import it.hero.politica.utils.ColorAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PoliticaHelpAdmin implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        if(!(sender instanceof Player)){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("help.consoleInstance")));
            return true;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("elezioni.listacomandiadmin")){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("help.noPermissionAdmin")));
            return true;
        }
        if(args.length != 0){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("help.wrongLength")));
            return true;
        }
        plugin.getConfig().getStringList("help.admin-message").forEach(msg -> player.sendMessage(ColorAPI.color(msg)));
        return true;
    }
}
