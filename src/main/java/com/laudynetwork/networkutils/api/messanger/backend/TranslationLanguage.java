package com.laudynetwork.networkutils.api.messanger.backend;

import lombok.Getter;

public enum TranslationLanguage {

    ENGLISH("en"), GERMAN("de"), JAPANESE("jp"), RUSSIAN("ru");

    @Getter
    private final String dbName;

    TranslationLanguage(String dbName) {

        this.dbName = dbName;
    }
}
