package com.laudynetwork.networkutils.api.proxybar;

import com.google.common.io.ByteStreams;
import lombok.val;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ProxyBarListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {

        if (!channel.equals("proxybar"))
            return;

        val byteArray = ByteStreams.newDataInput(message);

        val uuid = byteArray.readUTF();
        val title = byteArray.readUTF();

        if (!UUID.fromString(uuid).equals(player.getUniqueId()))
            return;

        val bossBar = BossBar.bossBar(LegacyComponentSerializer.legacyAmpersand().deserialize(title), 0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);

        player.showBossBar(bossBar);

    }
}
