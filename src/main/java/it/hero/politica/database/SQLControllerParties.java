package it.hero.politica.database;

import it.hero.politica.Politica;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLControllerParties {

    Politica plugin;

    public SQLControllerParties(Politica plugin) {
        this.plugin = plugin;
    }

    public void createTable(){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS parties_player_list (" +
                    "    id INTEGER(9) AUTO_INCREMENT," +
                    "    player_uuid VARCHAR(255) NOT NULL," +
                    "    player_party VARCHAR(255) NOT NULL," +
                    "    PRIMARY KEY (id)" +
                    ")");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayer(Player player, String party){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("INSERT INTO parties_player_list (player_uuid, player_party) VALUES (?, ?)");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ps.setString(2, party);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteParty(String party){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("DROP * FROM parties_player_list WHERE player_party = ?");
            ps.setString(1, party);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePlayerFromParty(Player player){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("DROP * FROM parties_player_list WHERE player_uuid = ?");
            ps.setString(1, String.valueOf(player.getUniqueId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerInParty(Player player){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM parties_player_list WHERE player_uuid = ?");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getPlayerPartyName(Player player){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM parties_player_list WHERE player_uuid = ?");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                return rs.getString("player_party");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
