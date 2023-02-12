package com.laudynetwork.networkutils.api.player.texturepack;

import com.google.common.hash.Hashing;
import com.laudynetwork.networkutils.api.player.ProtocolVersion;
import lombok.SneakyThrows;
import lombok.val;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TexturePack {

    @SneakyThrows
    public String getHashCodeFromUrl(URL url) {

        val path = Path.of(System.getProperty("user.home") + "/resource_pack/" + System.currentTimeMillis() + ".zip");

        File dir = new File(System.getProperty("user.home") + "/resource_pack/");
        if (!dir.exists())
            dir.mkdirs();

        File downloadedFile = new File(path.toString());

        downloadedFile.createNewFile();

        downloadUsingStream(url, path.toString());

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            downloadedFile.delete();
        }, 3, TimeUnit.SECONDS);

        return com.google.common.io.Files.asByteSource(new File(path.toString())).hash(Hashing.md5()).toString().toUpperCase();
    }

    @SneakyThrows
    private static void downloadUsingStream(URL url, String file){
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
