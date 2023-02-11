package com.laudynetwork.networkutils.api.messanger.backend;

import lombok.Getter;

public enum TranslationLanguage {

    ENGLISH("en"), GERMAN("de"), JAPANESE("jp"), RUSSIAN("ru");

    @Getter
    private final String dbName;

    public static TranslationLanguage getFromDBName(String dbName) {

        for (TranslationLanguage language : TranslationLanguage.values()) {
            if (language.dbName.equals(dbName))
                return language;
        }

        return TranslationLanguage.ENGLISH;
    }

    TranslationLanguage(String dbName) {
        this.dbName = dbName;
    }
}
