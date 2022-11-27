package com.laudynetwork.networkutils;

import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Getter
    private final MessageBackend msgBackend;

    public NetworkHandler() {

        logger.info("Loading config!");
        var utilsConfig = NetworkUtils.getInstance().getConfig();

        var languageStorage = new SQLConnection(utilsConfig.getString("language.jdbc"),
                utilsConfig.getString("language.user"),
                utilsConfig.getString("language.pwd"));

        msgBackend = new MessageBackend(languageStorage);


    }
}
