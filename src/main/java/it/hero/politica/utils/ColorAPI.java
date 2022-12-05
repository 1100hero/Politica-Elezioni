package it.hero.politica.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ColorAPI {
    public static String color(final String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String decolor(final String s) {
        return ChatColor.stripColor(ChatColor.RESET + s);
    }

    public static List<String> color(final List<String> list) {
        return list.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    public static List<String> decolor(final List<String> list) {
        return list.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }
}
