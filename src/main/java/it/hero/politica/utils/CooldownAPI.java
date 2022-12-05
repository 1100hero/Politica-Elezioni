package it.hero.politica.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownAPI {

    private final static Map<String, CooldownAPI> cooldowns = new HashMap<>();

    private UUID uuid;
    private String cooldownName;
    private long start;
    private int timeInSeconds;

    public CooldownAPI(Player player, String cooldownName, int timeInSeconds) {
        this.uuid = player.getUniqueId();
        this.cooldownName = cooldownName;
        this.timeInSeconds = timeInSeconds;
    }

    public void start() {
        this.start = System.currentTimeMillis();
        cooldowns.put(uuid + cooldownName, this);
    }

    //

    private static void stop(Player player, String cooldownName) {
        cooldowns.remove(player.getUniqueId() + cooldownName);
    }

    private static CooldownAPI getCooldown(Player player, String cooldownName) {
        return cooldowns.get(player.getUniqueId() + cooldownName);
    }

    public static int getTimeLeft(Player player, String cooldownName) {
        CooldownAPI cooldown = getCooldown(player, cooldownName);
        int f = -1;
        if (cooldown != null) {
            long now = System.currentTimeMillis();
            long cooldownTime = cooldown.start;
            int totalTime = cooldown.timeInSeconds;
            int r = (int) (now - cooldownTime) / 1000;
            f = (r - totalTime) * -1;
        }
        return f;
    }

    public static boolean isInCooldown(Player player, String cooldownName) {
        if (getTimeLeft(player, cooldownName) >= 1) {
            return true;
        }
        stop(player, cooldownName);
        return false;
    }
}