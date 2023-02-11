package com.laudynetwork.networkutils.api.player.texturepack;

import com.google.common.hash.Hashing;
import com.laudynetwork.networkutils.api.player.ProtocolVersion;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.SneakyThrows;
import lombok.val;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TexturePack {

    private final SQLConnection sqlConnection;

    public TexturePack(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
        createTable();
    }

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

    private void createTable() {
        this.sqlConnection.createTableWithPrimaryKey("minecraft_networkutils_resource_pack", "texturePackVersion",
                new SQLConnection.TableColumn("texturePackVersion", SQLConnection.ColumnType.INTEGER, 5),
                new SQLConnection.TableColumn("texturePackUrl", SQLConnection.ColumnType.VARCHAR, 255));
    }

    @SneakyThrows
    public URL getTexturePackFromVersion(ProtocolVersion protocolVersion) {

        val resultColumn = this.sqlConnection
                .getStringResultColumn("minecraft_networkutils_resource_pack", "texturePackVersion", protocolVersion.getProtocolVersion(), "texturePackUrl");

        return new URL(resultColumn.value().toString());
    }

//    @SneakyThrows
//    public List<URL> getListedTexturePacksFromDb() {
//
//        createTable();
//
//        val statement = this.sqlConnection.getMySQLConnection().createStatement();
//        statement.setQueryTimeout(30);
//
//        val resultSet = statement.executeQuery("SELECT * FROM `minecraft_networkutils_resource_pack`");
//
//        List<URL> storedTexturePacks = new ArrayList<>();
//        while (resultSet.next()) {
//            storedTexturePacks.add(new URL(resultSet.getString("texturePackUrl")));
//        }
//
//        statement.close();
//
//        return storedTexturePacks;
//    }

}
