package com.laudynetwork.networkutils.api.player;

import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import com.viaversion.viaversion.api.Via;
import lombok.SneakyThrows;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SqlResolve")
public class NetworkPlayer {

    private final SQLConnection connection;
    private final UUID uuid;
    private final Logger logger;

    public NetworkPlayer(SQLConnection connection, UUID uuid) {
        this.connection = connection;
        this.uuid = uuid;
        this.logger = LoggerFactory.getLogger("NetworkPlayer");

        connection.createTableWithPrimaryKey("minecraft_general_playerData", "uuid", new SQLConnection.TableColumn("uuid", SQLConnection.ColumnType.VARCHAR, 35),
                new SQLConnection.TableColumn("language", SQLConnection.ColumnType.VARCHAR, 20));
    }

    public ProtocolVersion getPlayerVersion() {
        return ProtocolVersion.getByProtocolVersion(Via.getAPI().getPlayerVersion(uuid));
    }

    @SneakyThrows
    public TranslationLanguage getLanguage() {

        if (!connection.existsColumn("minecraft_general_playerData", "uuid", uuid)) {
            setLanguage(TranslationLanguage.ENGLISH);
            return TranslationLanguage.ENGLISH;
        }

        val future = new CompletableFuture<String>();

        connection.resultSet("SELECT * FROM minecraft_general_playerData WHERE uuid = " + uuid, resultSet -> {
            try {
                while (resultSet.next()) {

                    future.complete(resultSet.getString("language"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return TranslationLanguage.getFromDBName(future.get().toUpperCase());
    }

    public void setLanguage(TranslationLanguage language) {
        if (connection.existsColumn("minecraft_general_playerData", "uuid", uuid.toString())) {
            logger.info("Updating language for [" + uuid + "] to " + language.name());
            connection.update("minecraft_general_playerData", "uuid", uuid.toString(), new SQLConnection.DataColumn("language", language.getDbName()));
        } else {
            logger.info("Creating language for [" + uuid + "] with language " + language.name());
            connection.insert("minecraft_general_playerData", new SQLConnection.DataColumn("uuid", uuid.toString()), new SQLConnection.DataColumn("language", language.getDbName()));
        }
    }
}
