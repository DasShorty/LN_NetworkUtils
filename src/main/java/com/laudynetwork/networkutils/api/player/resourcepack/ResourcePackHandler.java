package com.laudynetwork.networkutils.api.player.resourcepack;

import com.google.common.hash.Hashing;
import com.laudynetwork.networkutils.api.player.ProtocolVersion;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ResourcePackHandler {

    private final Plugin plugin;
    @Getter
    private HashMap<ProtocolVersion, String> resourcePackHash = new HashMap<>();

    public ResourcePackHandler(Plugin plugin) {
        this.plugin = plugin;

        run();
    }

    private void run() {

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {

            for (ProtocolVersion value : ProtocolVersion.values()) {
                val url = getTexturePackFromVersion(value);
                val hashCodeFromUrl = getHashCodeFromUrl(url);
                resourcePackHash.put(value, hashCodeFromUrl);
            }

        }, 0, 72000);

    }

    @SneakyThrows
    private String getHashCodeFromUrl(URL url) {

        val path = Path.of(System.getProperty("user.home") + "/resource_pack/" + System.currentTimeMillis() + ".zip");

        File dir = new File(System.getProperty("user.home") + "/resource_pack/");
        if (!dir.exists())
            dir.mkdirs();

        File downloadedFile = new File(path.toString());

        downloadedFile.createNewFile();

        downloadFile(url, path.toString());

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            downloadedFile.delete();
        }, 3, TimeUnit.SECONDS);

        return com.google.common.io.Files.asByteSource(new File(path.toString())).hash(Hashing.md5()).toString().toUpperCase();
    }

    @SneakyThrows
    private static void downloadFile(URL url, String file){
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    @SneakyThrows
    public URL getTexturePackFromVersion(ProtocolVersion protocolVersion) {
        return new URL("https://cdn.laudynetwork.com/rp-"+protocolVersion.getVersionName()+".zip");
    }

}
