package com.laudynetwork.networkutils.api.player;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
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
}
