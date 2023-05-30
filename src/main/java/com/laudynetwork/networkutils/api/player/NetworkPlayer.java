package com.laudynetwork.networkutils.api.player;

import com.laudynetwork.database.mysql.MySQL;
import com.laudynetwork.database.mysql.utils.Select;
import com.laudynetwork.database.mysql.utils.UpdateValue;
import com.viaversion.viaversion.api.Via;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@SuppressWarnings("SqlResolve")
public class NetworkPlayer {

    private final MySQL sql;
    @Getter
    private final UUID uuid;
    private final Logger logger;

    public NetworkPlayer(MySQL sql, UUID uuid) {
        this.sql = sql;
        this.uuid = uuid;

        this.logger = LoggerFactory.getLogger("NetworkPlayer");
    }

    public ProtocolVersion getPlayerVersion() {
        return ProtocolVersion.getByProtocolVersion(Via.getAPI().getPlayerVersion(uuid));
    }

    @SneakyThrows
    public String getLanguage() {

        var select = new Select("minecraft_general_playerData", "*", "uuid = '" + uuid.toString() + "'");

        if (!sql.rowExist(select)) {
            setLanguage("en");
            return "en";
        }

        var result = sql.rowSelect(select);

        return (String) result.getRows().get(0).get("language");
    }

    public void setLanguage(String language) {
        var select = new Select("minecraft_general_playerData", "*", "uuid = '" + uuid.toString() + "'");
        if (sql.rowExist(select)) {
            logger.info("Updating language for [" + uuid + "] to " + language);
            sql.rowUpdate("minecraft_general_playerData", "uuid = '" + uuid + "'", new UpdateValue("language", language));
        } else {
            logger.info("Creating language for [" + uuid + "] with language " + language);
            sql.tableInsert("minecraft_general_playerData", "uuid, language", uuid.toString(), language);
        }
    }
}
