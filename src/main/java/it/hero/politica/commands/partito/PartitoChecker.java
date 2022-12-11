package it.hero.politica.commands.partito;

import org.bukkit.entity.Player;

public class PartitoChecker {

    protected static boolean isTargetEqualsToPlayer(Player player, Player target){
        return (target == player);
    }

    protected static boolean isTargetNull(Player target){
        return (target == null);
    }
}
