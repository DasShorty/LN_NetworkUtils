package com.laudynetwork.networkutils.api.messanger.api;

import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.Replacement;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.sql.SQLConnection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageAPI {
    private final MessageBackend messageBackend;
    private final Component prefix;

    public MessageAPI(MessageBackend messageBackend, String prefixKey) {
        this.messageBackend = messageBackend;

        this.messageBackend.getConnection().createTable("minecraft_general_prefix",
                new SQLConnection.TableColumn("prefixKey", SQLConnection.ColumnType.VARCHAR, 100),
                new SQLConnection.TableColumn("prefix", SQLConnection.ColumnType.VARCHAR, 255));

        this.prefix = LegacyComponentSerializer.legacyAmpersand().deserialize(messageBackend.getConnection()
                .getStringResultColumn("minecraft_general_prefix", "prefixKey", prefixKey, "prefix").value().toString());
    }

    public Component asHighlight(Component component) {
        return component.color(TextColor.fromHexString("#FFAA00"));
    }

    public Component withPrefix(Component component) {
        return prefix.append(component);
    }

    public Component getTranslation(TranslationLanguage language, String key) {
        return this.messageBackend.getTranslation(language, key).createBuilder().build();
    }

    public Component getTranslation(TranslationLanguage language, String key, Replacement... replacements) {
        return this.messageBackend.getTranslation(language, key).createBuilder().replaceString(replacements).build();
    }

    public Component getMessage(TranslationLanguage language, String key) {
        return this.prefix.append(this.messageBackend.getTranslation(language, key).createBuilder().build());
    }

    public Component getMessage(TranslationLanguage language, String key, Replacement... replacements) {
        return this.prefix.append(this.messageBackend.getTranslation(language, key).createBuilder().replaceString(replacements).build());
    }
}
