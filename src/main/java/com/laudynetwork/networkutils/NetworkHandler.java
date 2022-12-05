package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Getter
    private final SQLConnection dbConnection;

    public NetworkHandler(NetworkUtils networkUtils) {

        logger.info("Loading config!");
        var utilsConfig = networkUtils.getConfig();

        logger.info("Creating Connection Pool...");
        dbConnection = new SQLConnection(utilsConfig.getString("language.jdbc"), utilsConfig.getString("language.user"), utilsConfig.getString("language.pwd"));

        logger.info("Finished creating connection pool!");
        logger.info("Ready for Db Handling!");

    }
}
