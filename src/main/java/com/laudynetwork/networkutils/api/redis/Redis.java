package com.laudynetwork.networkutils.api.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import lombok.Getter;

@Getter
public class Redis {

    private final StatefulRedisConnection<String, String> connection;
    private final RedisClient client;

    public Redis() {
        this.client = RedisClient.create("redis://RlUD7l3AC5Lg9UyC3BTjxIsn6Lw984TG@89.163.129.221:6379");
        this.connection = this.client.connect();
    }

    public void shutdown() {
        this.connection.close();
        this.client.shutdown();
    }
}
