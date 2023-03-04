package com.laudynetwork.networkutils.api.tablist;

import com.laudynetwork.networkutils.api.resourcePackAPI.ResourcePackAPI;
import com.laudynetwork.networkutils.api.resourcePackAPI.ResourcePackInterface;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public record TablistPlayer(Player player, net.luckperms.api.LuckPerms luckPerms) {
    public boolean isInGroup(String groupName) {
        return this.player.hasPermission("group." + groupName);
    }

    public String getGroupName() {
        val lpUser = this.luckPerms.getUserManager().getUser(player.getUniqueId());
        assert lpUser != null;
        return lpUser.getPrimaryGroup();
    }

    public String getVanishWeight(String groupName) {
        switch (groupName) {
            case "admin" -> {
                return "aVANISH";
            }
            case "developer" -> {
                return "bVANISH";
            }

            case "staff" -> {
                return "cVANISH";
            }

            case "content" -> {
                return "dVANISH";
            }

            case "builder" -> {
                return "eVANISH";
            }

            case "creator" -> {
                return "fVANISH";
            }

            case "vip" -> {
                return "gVANISH";
            }

            case "spark" -> {
                return "hVANISH";
            }

            case "prime" -> {
                return "iVANISH";
            }

            case "premium" -> {
                return "jVANISH";
            }

            default -> {
                return "kVANISH";
            }
        }
    }

    public Component getGroupPrefix(String groupName) {
        Group group = this.luckPerms.getGroupManager().getGroup(groupName);

        if (group == null)
            return Component.empty().color(getRankColor(groupName));

        String prefix = Objects.requireNonNull(group.getCachedData().getMetaData().getPrefix());
        TextColor rankColor = getRankColor(groupName);

        Map<String, ResourcePackInterface> data = ResourcePackAPI.parse(prefix);
        if(data.size() != 0) {
            String totalPrefix = prefix;

            for (ResourcePackInterface packInterface : data.values()){
                String toReplace = data.keySet().stream().filter(s -> data.get(s).equals(packInterface)).toList().get(0);
                totalPrefix = totalPrefix.replace(toReplace, "" + packInterface.get(packInterface.parseName(toReplace)));
                data.remove(toReplace);
            }

            return Component.text(totalPrefix)
                    .color(TextColor.color(0xFFFFFF))
                    .append(Component.space());
        }
        else
            return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix).color(rankColor);
    }

    public String getWeight(String groupName) {

        switch (groupName) {
            case "admin" -> {
                return "a";
            }
            case "developer" -> {
                return "b";
            }

            case "staff" -> {
                return "c";
            }

            case "content" -> {
                return "d";
            }

            case "builder" -> {
                return "e";
            }

            case "creator" -> {
                return "f";
            }

            case "vip" -> {
                return "g";
            }

            case "spark" -> {
                return "h";
            }

            case "prime" -> {
                return "i";
            }

            case "premium" -> {
                return "j";
            }

            default -> {
                return "k";
            }
        }
    }

    public TextColor getRankColor(String groupName) {
        val suffix = Objects.requireNonNull(this.luckPerms.getGroupManager().getGroup(groupName)).getCachedData().getMetaData().getSuffix();

        assert suffix != null;
        return TextColor.fromHexString(suffix);
    }

}
