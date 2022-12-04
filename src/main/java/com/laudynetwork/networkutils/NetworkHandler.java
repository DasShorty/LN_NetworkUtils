package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public NetworkHandler() {

        logger.info("Loading config!");
        var utilsConfig = NetworkUtils.getInstance().getConfig();

        var languageStorage = new SQLConnection(utilsConfig.getString("language.jdbc"),
                utilsConfig.getString("language.user"),
                utilsConfig.getString("language.pwd"));


    }
}
