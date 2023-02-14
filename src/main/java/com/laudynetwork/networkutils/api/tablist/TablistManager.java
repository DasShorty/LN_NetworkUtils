/*
    --------------------------
    Project : ServerTechnology
    Package : de.shortexception.servertechnology.clan.tablist
    Date 23.08.2022
    @author ShortException
    --------------------------
*/


package com.laudynetwork.networkutils.api.tablist;

import com.laudynetwork.networkutils.api.tablist.impl.DefaultTablist;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

public class TablistManager implements Listener {
  private final Scoreboard scoreboard;
  private final Plugin plugin;
  private Tablist activeTablist;

  public TablistManager(Plugin plugin, LuckPerms luckPerms) {
    this.plugin = plugin;
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    this.activeTablist = new DefaultTablist(luckPerms);

    Bukkit.getPluginManager().registerEvents(this, plugin);
    runTablistUpdater();
  }

  public void setTablist(Tablist tablist) {
    this.activeTablist = tablist;
  }

  private void runTablistUpdater() {
    Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this::updateScoreboard, 0, 100);
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    updateScoreboard();
  }


  public void updateScoreboard() {
    Bukkit.getOnlinePlayers().forEach(player -> {
      activeTablist.onUpdate(scoreboard, player);
    });
  }

}
