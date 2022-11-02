package de.shortexception.networkutils.api.player;

import de.shortexception.networkutils.api.messanger.SQLTextHandler;
import de.shortexception.networkutils.api.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
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
    }

    public void createNetworkData(SQLTextHandler.Language language) {
        logger.info("attempting to create network data");
        if (existsNot()) {
            logger.info("creating network data for user " + uuid);
        }

    }

    public boolean existsNot() {
        return !connection.existsColumn("playerLanguageStorage", "uuid", uuid.toString());
    }

    public SQLTextHandler.Language getSelectedLanguage() {

        if (existsNot()) {
            connection.insert("playerLanguageStorage", new SQLConnection.DataColumn("uuid", uuid.toString()),
                    new SQLConnection.DataColumn("language", SQLTextHandler.Language.ENGLISH));
            return SQLTextHandler.Language.ENGLISH;
        }

        logger.info("get selected language from user " + uuid);

        SQLTextHandler.Language language = SQLTextHandler.Language.ENGLISH;

        try {
            var ps = connection.getConnection().prepareStatement("SELECT * FROM `playerLanguageStorage` WHERE `uuid` = " + uuid);
            var resultSet = ps.executeQuery();
            while (resultSet.next()) {
                language = SQLTextHandler.Language.valueOf(resultSet.getString("language").toUpperCase());
            }

            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return language;
    }

    public void setLanguage(SQLTextHandler.Language newLanguage) {
        logger.info("language updated from user " + uuid + " new language "+ newLanguage.name());
        connection.update("playerLanguageStorage", "uuid", uuid.toString(),
                new SQLConnection.DataColumn("language", newLanguage.name()));
    }
}
