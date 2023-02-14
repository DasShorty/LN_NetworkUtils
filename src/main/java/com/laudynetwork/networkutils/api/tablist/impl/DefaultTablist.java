package com.laudynetwork.networkutils.api.tablist.impl;

import com.laudynetwork.networkutils.api.tablist.Tablist;
import com.laudynetwork.networkutils.api.tablist.TablistPlayer;
import com.laudynetwork.networkutils.essentials.vanish.VanishedPlayer;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class DefaultTablist implements Tablist {

    private final LuckPerms luckPerms;

    public DefaultTablist(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public void onUpdate(Scoreboard scoreboard, Player player) {
        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard()) || !player.getScoreboard().equals(scoreboard))
            player.setScoreboard(scoreboard);

        VanishedPlayer vanishedPlayer = new VanishedPlayer(player.getUniqueId());
        val tablistPlayer = new TablistPlayer(player, this.luckPerms);

        val vanishedTeam = getVanishedTeam(scoreboard, tablistPlayer);
        val rankTeam = getTeamFromRank(scoreboard, tablistPlayer);

        if (vanishedPlayer.isVanished()) {

            if (rankTeam.hasPlayer(player))
                rankTeam.removePlayer(player);

            if (!vanishedTeam.hasPlayer(player)) {
                vanishedTeam.addPlayer(player);
            }


        } else {

            if (vanishedTeam.hasPlayer(player))
                vanishedTeam.removePlayer(player);

            if (!rankTeam.hasPlayer(player)) {
                rankTeam.addPlayer(player);
            }
        }
    }

    private Team getVanishedTeam(Scoreboard scoreboard, TablistPlayer tablistPlayer) {
        val vanishWeight = tablistPlayer.getVanishWeight(tablistPlayer.getGroupName());
        var vanishedTeam = scoreboard.getTeam(vanishWeight);
        if (vanishedTeam == null)
            vanishedTeam = scoreboard.registerNewTeam(vanishWeight);

        vanishedTeam.prefix(
                Component.text(
                                Character.toString((char)
                                        Integer.parseInt("e10b", 16)))
                        .color(TextColor.color(0xFFFFFF)).append(Component.space())
                        .append(tablistPlayer.getGroupPrefix(tablistPlayer.getGroupName()))
        );
        vanishedTeam.color(NamedTextColor.GRAY);

        return vanishedTeam;
    }

    private Team getTeamFromRank(Scoreboard scoreboard, TablistPlayer tablistPlayer) {
        val groupName = tablistPlayer.getGroupName();
        Team team = scoreboard.getTeam(tablistPlayer.getWeight(groupName) + groupName);

        if (team == null)
            team = scoreboard.registerNewTeam(tablistPlayer.getWeight(groupName) + groupName);


        team.prefix(tablistPlayer.getGroupPrefix(groupName));
        team.color(NamedTextColor.GRAY);
        return team;
    }

}
