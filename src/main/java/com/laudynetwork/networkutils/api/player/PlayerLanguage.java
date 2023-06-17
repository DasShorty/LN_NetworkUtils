package com.laudynetwork.networkutils.api.player;

import com.google.gson.Gson;

import java.util.UUID;

/**
 * Class made by DasShorty ~Anthony
 */
public record PlayerLanguage(UUID uuid, String language) {

    public static PlayerLanguage fromJson(String json) {
        return new Gson().fromJson(json, PlayerLanguage.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
