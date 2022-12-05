package it.hero.politica.database;

import it.hero.politica.Politica;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private final Politica plugin = Politica.getPlugin(Politica.class);
    @Getter private Connection connection;

    private final String username = plugin.getConfig().getString("db.username");
    private final String password = plugin.getConfig().getString("db.password");
    private final String database = plugin.getConfig().getString("db.database");
    private final String host = plugin.getConfig().getString("db.host");
    private final int port = plugin.getConfig().getInt("db.port");

    public void createConnection(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void closeConnection() {
        try {
            getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isConnected() {
        return (connection != null);
    }
}
