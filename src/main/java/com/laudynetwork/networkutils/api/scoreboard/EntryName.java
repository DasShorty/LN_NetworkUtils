package com.laudynetwork.networkutils.api.scoreboard;

import org.bukkit.ChatColor;

public enum EntryName {

    ENTRY_0(0, ChatColor.GRAY.toString()),
    ENTRY_1(1, ChatColor.BOLD.toString()),
    ENTRY_2(2, ChatColor.GREEN.toString()),
    ENTRY_3(3, ChatColor.BLUE.toString()),
    ENTRY_4(4, ChatColor.RED.toString()),
    ENTRY_5(5, ChatColor.WHITE.toString()),
    ENTRY_6(6, ChatColor.DARK_GRAY.toString()),
    ENTRY_7(7, ChatColor.DARK_GREEN.toString()),
    ENTRY_8(8, ChatColor.LIGHT_PURPLE.toString()),
    ENTRY_9(9, ChatColor.DARK_RED.toString()),
    ENTRY_10(10, ChatColor.UNDERLINE.toString()),
    ENTRY_11(11, ChatColor.MAGIC.toString()),
    ENTRY_12(12, ChatColor.DARK_BLUE.toString());


    private final int entry;
    private final String entryName;

    EntryName(int entry, String name) {
        this.entry = entry;
        this.entryName = name;
    }

    public int getEntry() {
        return entry;
    }

    public String getEntryName() {
        return entryName;
    }
}