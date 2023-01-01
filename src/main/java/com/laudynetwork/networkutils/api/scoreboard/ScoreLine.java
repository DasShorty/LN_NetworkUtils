package com.laudynetwork.networkutils.api.scoreboard;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum ScoreLine {

    ENTRY_1(ChatColor.BLUE.toString(), 1),
    ENTRY_2(ChatColor.RED.toString(), 2),
    ENTRY_3(ChatColor.YELLOW.toString(), 3),
    ENTRY_4(ChatColor.GREEN.toString(), 4),
    ENTRY_5(ChatColor.DARK_GREEN.toString(), 5),
    ENTRY_6(ChatColor.DARK_BLUE.toString(), 6),
    ENTRY_7(ChatColor.DARK_RED.toString(), 7),
    ENTRY_8(ChatColor.AQUA.toString(), 8),
    ENTRY_9(ChatColor.BLACK.toString(), 9),
    ENTRY_10(ChatColor.WHITE.toString(), 10),
    ENTRY_11(ChatColor.DARK_AQUA.toString(), 11),
    ENTRY_12(ChatColor.DARK_GRAY.toString(), 12),
    ENTRY_13(ChatColor.DARK_PURPLE.toString(), 13),
    ENTRY_14(ChatColor.GOLD.toString(), 14),
    ENTRY_15(ChatColor.GRAY.toString(), 15),
    ENTRY_16(ChatColor.LIGHT_PURPLE.toString(), 16);

    private final String placeholder;
    private final int line;

    ScoreLine(String placeholder, int line) {

        this.placeholder = placeholder;
        this.line = line;
    }
}
