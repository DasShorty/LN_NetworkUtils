package com.laudynetwork.networkutils.api.queue;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.Getter;

import java.util.UUID;

public class QueuePlayer {
    @Getter
    private final UUID uuid;
    private final SQLConnection sqlConnection;

    public QueuePlayer(UUID uuid, SQLConnection sqlConnection) {
        this.uuid = uuid;
        this.sqlConnection = sqlConnection;


    }

    public void sendToDestination(Queue queue) {

        // TODO - implement feature

    }

    public int getID() {
        checkTable();
        if (this.sqlConnection.existsColumn("minecraft_networkutils_queue_player", "uuid", this.uuid.toString())) {
            return ((int) this.sqlConnection.getIntResultColumn("minecraft_networkutils_queue_player", "uuid", this.uuid.toString(), "id")
                    .value());
        }
        return -1;

    }

    public String getQueueName() {
        checkTable();
        if (!this.isPlayerInQueue())
            return null;

        return (String) this.sqlConnection.getStringResultColumn("minecraft_networkutils_queue_player", "uuid", this.uuid.toString(), "queue").value();
    }

    public boolean isPlayerInQueue() {
        checkTable();
        return this.sqlConnection.existsColumn("minecraft_networkutils_queue_player", "uuid", this.uuid.toString());
    }

    private void checkTable() {
        this.sqlConnection
                .createTableFromSQL("create table if not exists minecraft_networkutils_queue_player(`id` int auto_increment primary key, `uuid` varchar(30) not null unique, `queue` int not null)");

    }

}
