package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public NetworkHandler() {

        var utilsConfig = NetworkUtils.getInstance().getConfig();

        var sqlConnection = new SQLConnection(utilsConfig.getString("playerLanguageData.jdbc"),
                utilsConfig.getString("playerLanguageData.user"),
                utilsConfig.getString("playerLanguageData.pwd"));
    }
}
