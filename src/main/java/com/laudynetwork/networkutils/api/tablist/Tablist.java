package com.laudynetwork.networkutils.api.tablist;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public interface Tablist {

    void onUpdate(Scoreboard scoreboard, Player player);

}
