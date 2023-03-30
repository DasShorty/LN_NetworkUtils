package com.laudynetwork.networkutils.registration;

import com.laudynetwork.networkutils.api.redis.Redis;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import lombok.SneakyThrows;
import lombok.val;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class WebsiteRegisterManager {

    private final SQLConnection sqlConnection;
    private final Redis redis;

    public WebsiteRegisterManager(SQLConnection sqlConnection, Redis redis) {
        this.sqlConnection = sqlConnection;
        this.redis = redis;
    }

    @SneakyThrows
    public CompletableFuture<RegisteredUser> addUser(UUID uuid, String email) {
        val future = new CompletableFuture<RegisteredUser>();

        if (this.sqlConnection.existsColumn("website_users", "uuid", uuid.toString())) {
            future.complete(new RegisteredUser(false, null));
            return future;
        }

        UUID token = UUID.randomUUID();

        this.sqlConnection.insert("website_users", new SQLConnection.DataColumn("uuid", uuid.toString()),
                new SQLConnection.DataColumn("email", email));

        val done = this.redis.getConnection().async().setex(token.toString(), Duration.ofHours(2).toSeconds(), email).isDone();

        future.complete(new RegisteredUser(true, token));
        return future;
    }

    public boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}
