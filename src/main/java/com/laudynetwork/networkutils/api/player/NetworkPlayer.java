package com.laudynetwork.networkutils.api.player;

import com.laudynetwork.networkutils.api.messanger.api.TranslationLanguage;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@SuppressWarnings("SqlResolve")
public class NetworkPlayer {

    private final SQLConnection connection;
    private final UUID uuid;
    private final Logger logger;

    public NetworkPlayer(SQLConnection connection, UUID uuid) {
        this.connection = connection;
        this.uuid = uuid;
        this.logger = LoggerFactory.getLogger("NetworkPlayer");

        connection.createTableWithPrimaryKey("playerData", "uid", new SQLConnection.TableColumn("uid", SQLConnection.ColumnType.VARCHAR, 35),
                new SQLConnection.TableColumn("language", SQLConnection.ColumnType.VARCHAR, 20));
    }

    public TranslationLanguage getLanguage() {
        return TranslationLanguage.valueOf(connection.getStringResultColumn("playerData", "uid", uuid.toString(), "language").value().toString().toUpperCase());
    }

    public void setLanguage(TranslationLanguage language) {
        if (connection.existsColumn("playerData", "uid", uuid.toString())) {
            logger.info("Updating language for ["+uuid+"] to " + language.name());
            connection.update("playerData", "uid", uuid.toString(), new SQLConnection.DataColumn("language", language.name()));
        } else {
            logger.info("Creating language for ["+uuid+"] with language " + language.name());
            connection.insert("playerData", new SQLConnection.DataColumn("uid", uuid.toString()), new SQLConnection.DataColumn("language", language.name()));
        }
    }
}
