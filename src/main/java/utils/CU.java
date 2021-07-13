package utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class CU {

    public static String t(String text) {
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    public static void Sendstrings(Player player ,String[] strings) {
        for (String i :strings) {
            player.sendMessage(i);
        }
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

}
