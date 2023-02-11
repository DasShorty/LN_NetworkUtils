/*
    --------------------------
    Project : ServerTechnology
    Package : de.shortexception.servertechnology.clan.tablist
    Date 23.08.2022
    @author ShortException
    --------------------------
*/


package com.laudynetwork.networkutils.api.tablist;

import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TablistManager implements Listener {
  private final Scoreboard scoreboard;
  private final Plugin plugin;
  private final LuckPerms luckPerms;

  public TablistManager(Plugin plugin, LuckPerms luckPerms) {
    this.plugin = plugin;
    this.luckPerms = luckPerms;
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    updateScoreboard();
  }

  public void updateScoreboard() {
    Bukkit.getOnlinePlayers().forEach(player -> {
      if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard()) || !player.getScoreboard().equals(scoreboard))
        player.setScoreboard(scoreboard);

      val tablistPlayer = new TablistPlayer(player, this.luckPerms);

      val groupName = tablistPlayer.getGroupName();
      Team team = scoreboard.getTeam(tablistPlayer.getWeight(groupName) + groupName);

      if (team == null)
        team = scoreboard.registerNewTeam(tablistPlayer.getWeight(groupName) + groupName);

      team.prefix(tablistPlayer.getGroupPrefix(groupName));
      team.color(NamedTextColor.GRAY);

      if (!team.hasPlayer(player))
        team.addPlayer(player);

    });
  }

}
