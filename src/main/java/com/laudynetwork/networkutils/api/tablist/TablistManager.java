/*
    --------------------------
    Project : ServerTechnology
    Package : de.shortexception.servertechnology.clan.tablist
    Date 23.08.2022
    @author ShortException
    --------------------------
*/


package com.laudynetwork.networkutils.api.tablist;

import com.laudynetwork.networkutils.essentials.vanish.VanishedPlayer;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
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
    runTablistUpdater();
  }

  private void runTablistUpdater() {
    Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this::updateScoreboard, 0, 100);
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    updateScoreboard();
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

  public void updateScoreboard() {
    Bukkit.getOnlinePlayers().forEach(player -> {
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


    });
  }

}
