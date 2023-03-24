package com.laudynetwork.networkutils.api.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import lombok.Getter;

@Getter
public class Redis {

    private final RedisConnection<String, String> connection;
    private final RedisClient client;

    public Redis() {
        this.client = new RedisClient(
                RedisURI.create("redis://RlUD7l3AC5Lg9UyC3BTjxIsn6Lw984TG@89.163.129.221:6379"));
        this.connection = this.client.connect();

        System.out.println("Connected to Redis");
    }

    public void shutdown() {
        this.connection.close();
        this.client.shutdown();
    }
}
