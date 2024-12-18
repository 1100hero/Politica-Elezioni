package it.hero.politica;

import it.hero.politica.commands.*;
import it.hero.politica.commands.help.PoliticaHelp;
import it.hero.politica.commands.help.PoliticaHelpAdmin;
import it.hero.politica.commands.partito.TesseraPartito;
import it.hero.politica.database.MySQL;
import it.hero.politica.database.SQLControllerVotes;
import it.hero.politica.events.ClickedOnBlock;
import it.hero.politica.events.PartyListEvent;
import it.hero.politica.events.VoteEvent;
import it.hero.politica.utils.HologramLoader;
import it.hero.politica.utils.PAPIExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Politica extends JavaPlugin {

    public MySQL sql;
    public SQLControllerVotes controller;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerCommands();
        registerEvents();
        HologramLoader.checkStartedElections();
        new PAPIExpansion().register();
        this.sql = new MySQL();
        sql.createConnection();
        if(sql.isConnected()){
            Bukkit.getLogger().info("Database Politica connesso.");
        }
        this.controller = new SQLControllerVotes(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Politica disabilitata.");
        sql.closeConnection();
    }
    private void registerCommands(){
        this.getCommand("elezioni").setExecutor(new Elezioni());
        this.getCommand("politica").setExecutor(new ReloadConfig());
        this.getCommand("creapartito").setExecutor(new CreaPartito());
        this.getCommand("givevoteblock").setExecutor(new GiveCustomVoteBlock());
        this.getCommand("partito").setExecutor(new TesseraPartito());
        this.getCommand("partychat").setExecutor(new PartyChat());
        this.getCommand("politicahelp").setExecutor(new PoliticaHelp());
        this.getCommand("partylist").setExecutor(new PartyList());
        this.getCommand("politicahelpadmin").setExecutor(new PoliticaHelpAdmin());
        this.getCommand("detesserati").setExecutor(new Detesserati());
    }
    private void registerEvents(){
        this.getServer().getPluginManager().registerEvents(new ClickedOnBlock(), this);
        this.getServer().getPluginManager().registerEvents(new VoteEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PartyListEvent(), this);
    }
}
