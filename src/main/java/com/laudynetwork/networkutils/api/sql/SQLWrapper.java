package com.laudynetwork.networkutils.api.sql;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class SQLWrapper {

  public static String fromMapToString(Map<String, String> values) {
    return Joiner.on(",").withKeyValueSeparator("=").join(values);
  }

  public static String fromListToString(List<String> value) {

    StringBuilder builder = new StringBuilder();

    for (String s : value) {
      builder.append(s).append(",");
    }

    return builder.substring(0, builder.toString().length() - 1);
  }

  public static Map<String, String> fromStringToMap(String input) {
    return Splitter.on(",").withKeyValueSeparator("=").split(input);
  }

  public static List<String> fromStringToList(String input) {
    return new ArrayList<>(Arrays.asList(input.split(",")));
  }

  public static String fromLocationToString(Location loc) {
    return loc.getWorld().getUID()+";"+loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ();
  }

  public static Location fromStringToLocation(String str) {
    String[] split = str.split(";");
    var world = Bukkit.getWorld(UUID.fromString(split[0]));
    var x = Integer.parseInt(split[1]);
    var y = Integer.parseInt(split[2]);
    var z = Integer.parseInt(split[3]);
    return new Location(world, x, y, z);
  }

}
