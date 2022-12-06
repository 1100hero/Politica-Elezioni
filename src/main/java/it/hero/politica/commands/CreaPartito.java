package it.hero.politica.commands;

import it.hero.politica.Politica;
import it.hero.politica.database.SQLControllerParties;
import it.hero.politica.database.SQLControllerPartiesStorage;
import it.hero.politica.utils.ColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CreaPartito implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Politica plugin = Politica.getPlugin(Politica.class);
        SQLControllerParties controller = new SQLControllerParties(plugin);
        controller.createTable();
        if(!(sender instanceof Player)){
            sender.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.consoleInstance")));
            return true;
        }
        Player player = (Player)sender;
        if(!player.hasPermission("elezioni.partito.crea")){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.creaNoPermission")));
            return true;
        }
        if(args.length != 4){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.creaWrongLength")));
            return true;
        }
        if(plugin.getConfig().getBoolean("elezioni.data")){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.electionStarted")));
            return true;
        }
        if(controller.isPlayerInParty(player)){
            player.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.alreadyInParty"))
                    .replace("%partito%", controller.getPlayerPartyName(player))));
            return true;
        }
        if(!(args[1].equalsIgnoreCase("blu") || args[1].equalsIgnoreCase("ciano") || args[1].equalsIgnoreCase("viola") ||
                args[1].equalsIgnoreCase("verde") || args[1].equalsIgnoreCase("rosso") || args[1].equalsIgnoreCase("nero") ||
        args[1].equalsIgnoreCase("grigio") || args[1].equalsIgnoreCase("giallo"))){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.colorNotExists")));
            return true;
        }
        if(!(args[2].equalsIgnoreCase("sinistra") || args[2].equalsIgnoreCase("centro") || args[2].equalsIgnoreCase("destra"))){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("orientationNotExists")));
            return true;
        }
        Player leader = Bukkit.getPlayerExact(args[3]);
        if(leader == null){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.notOnlineOrExists")));
            return true;
        }
        SQLControllerPartiesStorage partiesStorage = new SQLControllerPartiesStorage(plugin);
        partiesStorage.createTable();
        if(controller.isPlayerInParty(leader) || partiesStorage.isOwner(player)){
            player.sendMessage(ColorAPI.color(plugin.getConfig().getString("partito.targetInParty")));
            return true;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+player.getName()+" permission set elezioni.partito.*");
        LinkedList<String> politicalPartiesName = new LinkedList<>(plugin.getConfig().getConfigurationSection("voti.gui.political-parties").getKeys(false));
        LinkedList<Integer> position = new LinkedList<>();
        politicalPartiesName.forEach(s -> {
            position.add(plugin.getConfig().getInt("voti.gui.political-parties." + s + ".position"));
        });
        LinkedList<String> material = new LinkedList<>();
        politicalPartiesName.forEach(s -> {
            material.add(plugin.getConfig().getString("voti.gui.political-parties." + s + ".material"));
        });
        LinkedList<String> name = new LinkedList<>();
        politicalPartiesName.forEach(s -> {
            name.add(ColorAPI.color(plugin.getConfig().getString("voti.gui.political-parties." + s + ".name")));
        });
        LinkedList<List<String>> lore = new LinkedList<>();
        politicalPartiesName.forEach(s -> {
            lore.add(plugin.getConfig().getStringList("voti.gui.political-parties." + s + ".lore"));
        });
        politicalPartiesName.add(args[0]);
        position.add(position.get(position.size()-1)+1);
        material.add("GREEN_CONCRETE");
        name.add("&7Vota &e"+args[0]);
        List<String> tempList = new LinkedList<>();
        tempList.add("&7clicca per votare il partito.");
        lore.add(tempList);
        plugin.getConfig().set("voti.gui.political-parties", politicalPartiesName);
        int x = 0;
        for(String s : politicalPartiesName){
            plugin.getConfig().set("voti.gui.political-parties."+s+".position", position.get(x));
            plugin.getConfig().set("voti.gui.political-parties."+s+".material", material.get(x));
            plugin.getConfig().set("voti.gui.political-parties."+s+".name", name.get(x));
            plugin.getConfig().set("voti.gui.political-parties."+s+".lore", lore.get(x));
            ++x;
        }
        plugin.saveConfig();
        partiesStorage.insertValues(args[0], args[2], args[1], leader, 0);
        controller.insertPlayer(leader, args[0]);
        leader.sendMessage(ColorAPI.color(Objects.requireNonNull(plugin.getConfig().getString("partito.created")).replace("%partito%", args[0])));
        return true;
    }
}
