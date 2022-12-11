package it.hero.politica.database;

import it.hero.politica.Politica;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    public void removePlayerFromParty(Player player){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("DELETE FROM parties_player_list WHERE player_uuid = ?");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ps.executeUpdate();
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
    public boolean existTable(){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM parties_player_list");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPlayerInExactParty(Player player, Player chatPlayer){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT player_party FROM parties_player_list WHERE player_uuid = ?");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                if(rs.getString(1).equalsIgnoreCase(getPlayerPartyName(chatPlayer))) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean isPlayerInExactParty(Player player, String partyName){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT player_uuid FROM parties_player_list WHERE player_party = ?");
            ps.setString(1, partyName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if(rs.getString(1).equalsIgnoreCase(String.valueOf(player.getUniqueId()))) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean existParty(String party){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM parties_storage WHERE party = ?");
            ps.setString(1, party);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getNOfPlayerInParty(String party) {
        PreparedStatement ps;
        int x=0;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM parties_player_list WHERE player_party = ?");
            ps.setString(1, party);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                ++x;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }
    public OfflinePlayer getPlayerById(String party, int id) {
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT player_uuid FROM parties_player_list WHERE player_party = ? AND id = ?");
            ps.setString(1, party);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(1)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int getFirstId(String party) {
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT id FROM parties_player_list WHERE player_party = ?");
            ps.setString(1, party);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
