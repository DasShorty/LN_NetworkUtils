package com.laudynetwork.networkutils.api.queue;

import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public record QueueManager(SQLConnection sqlConnection) {

    private static final Map<Integer, Queue> QUEUE_MAP = new HashMap<>();

    private void checkTable() {
        this.sqlConnection.createTableFromSQL("create table if not exist minecraft_networkutils_queues(queue varchar(30) not null, destServer varchar(30) not null, waitingPlayers text not null, maxSlots int not null, minSlots int not null, id int auto_increment primary key);");
    }

    public Queue createQueue(String destinationServer, int maxSlots, int minSlots) {
        checkTable();
        this.sqlConnection.insert("minecraft_networkutils_queues",
                new SQLConnection.DataColumn("queue", destinationServer),
                new SQLConnection.DataColumn("destServer", destinationServer),
                new SQLConnection.DataColumn("waitingPlayers", "[]"),
                new SQLConnection.DataColumn("maxSlots", maxSlots),
                new SQLConnection.DataColumn("minSlots", minSlots));

        int id = (int) this.sqlConnection.getIntResultColumn("minecraft_networkutils_queues", "queue", destinationServer, "id").value();
        val queueName = destinationServer + "#" + id;

        this.sqlConnection.update("minecraft_networkutils_queues", "queue", destinationServer,
                new SQLConnection.DataColumn("queue", queueName));

        NamespacedKey namespacedKey = new NamespacedKey("queue-" + queueName, queueName);

        val bossbar = Bukkit.createBossBar(namespacedKey, queueName, BarColor.RED, BarStyle.SEGMENTED_10);
        bossbar.setProgress(0D);

        val queue = new Queue(destinationServer, id, new ArrayList<>(), destinationServer, bossbar, maxSlots, minSlots);

        QUEUE_MAP.put(id, queue);

        return queue;
    }

    public void removeQueue(Queue queue) {
        removeQueue(queue.id());
    }

    public void removeQueue(int id) {
        checkTable();
        QUEUE_MAP.remove(id);
        this.sqlConnection.delete("minecraft_networkutils_queues", "id", id);
    }

}
