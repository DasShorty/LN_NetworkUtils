package com.laudynetwork.networkutils.api.chatutil;

import joptsimple.internal.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@SuppressWarnings("unused")
public class HexColor {

    private final Pattern pattern = Pattern.compile("#[a-fA-F\\d]{6}");

    public String translate(String message) {
        if (message == null) return Strings.EMPTY;
        if (message.contains("&x")) message = message.replaceAll("&x", "#");
        Matcher match = pattern.matcher(message);
        while (match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, ChatColor.getByChar(color) + "");
            match = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
