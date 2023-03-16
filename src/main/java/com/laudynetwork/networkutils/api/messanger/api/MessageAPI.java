package com.laudynetwork.networkutils.api.messanger.api;

import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@SuppressWarnings("unused")
public class MessageAPI {
    private final MessageBackend messageBackend;
    private final Component prefix;

    public MessageAPI(MessageBackend messageBackend, PrefixType prefixType) {
        this.messageBackend = messageBackend;
        this.prefix = LegacyComponentSerializer.legacyAmpersand().deserialize(prefixType.getPrefix());
    }

    public Component asHighlight(Component component) {
        return component.color(TextColor.fromHexString("#FFAA00"));
    }

    public Component withPrefix(Component component) {
        return prefix.append(component);
    }

    public Component getTranslation(TranslationLanguage language, String key) {
        return this.messageBackend.getTranslation(language, key).createBuilder().getData();
    }

    public Component getTranslation(TranslationLanguage language, String key, TagResolver... replacements) {
        return this.messageBackend.getTranslation(language, key).createBuilder(replacements).getData();
    }

    public Component getMessage(TranslationLanguage language, String key) {
        return this.prefix.append(this.messageBackend.getTranslation(language, key).createBuilder().getData());
    }

    public Component getMessage(TranslationLanguage language, String key, TagResolver... replacements) {
        return this.prefix.append(this.messageBackend.getTranslation(language, key).createBuilder(replacements).getData());
    }

    public boolean existsTranslation(String key) {
        return this.messageBackend.existsTranslation(key);
    }

    public enum PrefixType {
        SYSTEM("&x&e&d&c&1&0&0System &8» "),
        CLAN("&x&c&c&7&a&1&6Clan &8» "),
        FRIEND("&x&0&2&d&d&3&cFriends &8» "),
        RACE("&x&0&0&D&2&E&5Race &8» "),
        PARTY("&x&b&3&0&2&e&0Party &8» ");

        @Getter
        private final String prefix;

        PrefixType(String prefix) {
            this.prefix = prefix;
        }
    }
}
