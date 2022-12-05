package it.hero.politica.database;

import it.hero.politica.Politica;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLControllerVotes {

    private Politica plugin;
    public SQLControllerVotes(Politica plugin) {
        this.plugin = plugin;
    }

    public void createTable(){
        PreparedStatement ps;
        try {
            ps = plugin.sql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS voted_player (" +
                    "    id INT(9) NOT NULL AUTO_INCREMENT," +
                    "    player_uuid VARCHAR(255) NOT NULL," +
                    "    political_party VARCHAR(255) NOT NULL," +
                    "    PRIMARY KEY (id)" +
                    ")");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayer(Player player, String partito){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("INSERT INTO voted_player (player_uuid, political_party) VALUES (?,?)");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ps.setString(2, partito);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean existPlayer(Player player){
        PreparedStatement ps;
        try {
            ps = plugin.sql.getConnection().prepareStatement("SELECT * FROM voted_player WHERE player_uuid = ?");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteTable(){
        PreparedStatement ps;
        try{
            ps = plugin.sql.getConnection().prepareStatement("DROP TABLE voted_player");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
