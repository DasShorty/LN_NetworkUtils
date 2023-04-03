package com.laudynetwork.networkutils.registration;

import com.laudynetwork.database.mysql.MySQL;
import com.laudynetwork.database.mysql.utils.Select;
import com.laudynetwork.database.redis.Redis;
import lombok.SneakyThrows;
import lombok.val;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class WebsiteRegisterManager {

    private final MySQL sql;
    private final Redis redis;

    public WebsiteRegisterManager(MySQL sql, Redis redis) {
        this.sql = sql;
        this.redis = redis;
    }

    @SneakyThrows
    public CompletableFuture<RegisteredUser> addUser(UUID uuid, String email) {
        val future = new CompletableFuture<RegisteredUser>();

        if (this.sql.rowExist(new Select("website_users", "*", String.format("uuid = '%s'", uuid.toString())))) {
            future.complete(new RegisteredUser(false, null));
            return future;
        }

        UUID token = UUID.randomUUID();

        this.sql.tableInsert("website_users", "uuid, email", uuid.toString(), email);

        this.redis.getConnection().async().setex(token.toString(), Duration.ofHours(2).toSeconds(), email).isDone();

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
