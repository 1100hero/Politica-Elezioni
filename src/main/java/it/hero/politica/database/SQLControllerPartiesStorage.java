package it.hero.politica.database;

import it.hero.politica.Politica;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLControllerPartiesStorage {
    private Politica plugin;

    public SQLControllerPartiesStorage(Politica plugin) {
        this.plugin = plugin;
    }

    public void createTable(){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS parties_storage (" +
                    "    id int(9) NOT NULL AUTO_INCREMENT," +
                    "    party varchar(255) NOT NULL," +
                    "    orientation varchar(255) NOT NULL," +
                    "    color varchar(255) NOT NULL," +
                    "    votes int(9)," +
                    "    owner_uuid varchar(255) NOT NULL," +
                    "    primary key(id)" +
                    ")");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertValues(String partyName, String orientation, String color, Player owner, int votes){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("INSERT INTO parties_storage(party, orientation, color, owner_uuid, votes) VALUES (?,?,?,?,?)");
            ps.setString(1, partyName);
            ps.setString(2, orientation);
            ps.setString(3, color);
            ps.setString(4, String.valueOf(owner.getUniqueId()));
            ps.setInt(5, votes);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateVotes(String partyName, int votes) {
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("UPDATE parties_storage SET votes=? WHERE party=?");
            ps.setInt(1, votes);
            ps.setString(2, partyName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean existParty(){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM parties_storage");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getAllVotes(){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT SUM(votes) FROM parties_storage");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getVoteByParty(String party) {
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT votes FROM parties_storage WHERE party=?");
            ps.setString(1, party);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public String getColor(String party){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT color FROM parties_storage WHERE party=?");
            ps.setString(1, party);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                return rs.getString(1).toUpperCase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void resetVotes(){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("UPDATE parties_storage SET votes=?");
            ps.setInt(1, 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isOwner(Player player){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM parties_storage WHERE owner_uuid=?");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getNOfParties(){
        PreparedStatement ps;
        int x=0;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM parties_storage");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                ++x;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }
    public OfflinePlayer getOwnerById(int id){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT owner_uuid FROM parties_storage WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                return Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(1)));
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public String getOwnerParty(OfflinePlayer player){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT party FROM parties_storage WHERE owner_uuid = ?");
            ps.setString(1, String.valueOf(Bukkit.getOfflinePlayer(player.getUniqueId()).getUniqueId()));
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOrientation(String party){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("SELECT orientation FROM parties_storage WHERE party=?");
            ps.setString(1, party);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
