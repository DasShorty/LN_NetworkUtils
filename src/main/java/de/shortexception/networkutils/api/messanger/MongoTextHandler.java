package de.shortexception.networkutils.api.messanger;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public record MongoTextHandler(MongoMessageManager handler, String key) {

    @SneakyThrows
    public String getMessage(Language language, Section section) {

        var completableFuture = new CompletableFuture<String>();

        Executors.newSingleThreadExecutor().submit(() -> {
            var collection = handler.getConnection().getDatabase().getCollection(section.getCollectionName());

            Document document = collection.find(Filters.eq("key", key)).first();
            if (document == null)
                throw new IllegalStateException("document can't be null » MongoCollection " + section.getCollectionName() + ", key " + key);
            completableFuture.complete(String.valueOf(document.get(language.getLanguage())));
        });

        return completableFuture.get(3L, TimeUnit.SECONDS);
    }

    public enum Language {
        GERMAN("german"),
        ENGLISH("english"),
        FRANCE("france"),
        SPANISH("spanish"),
        CHINESE("chinese"),
        KOREAN("korean"),
        JAPANESE("japanese");

        @Getter
        private final String language;

        Language(String language) {
            this.language = language;
        }
    }

    public enum Section {
        CHAT("chat"),
        SCOREBOARD("scoreboard"),
        GUI("gui"),
        TABLIST("tablist");

        @Getter
        private final String collectionName;


        Section(String collectionName) {
            this.collectionName = collectionName;
        }
    }

}
