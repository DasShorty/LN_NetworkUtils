package com.laudynetwork.networkutils.api.messanger.api;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.messanger.backend.MessageCache;
import com.laudynetwork.networkutils.api.messanger.backend.Translation;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Class made by DasShorty ~Anthony
 */
public class MessageAPI {

    private final MessageCache messageCache;
    private final PrefixType prefixType;

    public MessageAPI(MessageCache messageCache, PrefixType prefixType) {
        this.messageCache = messageCache;
        this.prefixType = prefixType;
    }

    public boolean existTranslation(String key) {
        return messageCache.existTranslation(key);
    }

    public Translation getRaw(String language, String key) {
        return messageCache.getTranslation(language, key);
    }

    public Component getTranslation(String language, String key) {
        return MiniMessage.miniMessage().deserialize(messageCache.getTranslation(language, key).rawTranslation());
    }

    public Component getTranslation(String language, String key, TagResolver... tagResolvers) {
        return MiniMessage.miniMessage().deserialize(messageCache.getTranslation(language, key).rawTranslation(), tagResolvers);
    }

    public Component getMessage(String language, String key) {
        return getPrefix().append(getTranslation(language, key));
    }

    public Component getMessage(String language, String key, TagResolver... tagResolvers) {
        return getPrefix().append(getTranslation(language, key, tagResolvers));
    }

    private Component getPrefix() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefixType.getPrefix());
    }

    public enum PrefixType {
        SYSTEM("&x&e&d&c&1&0&0System &8» "),
        MANHUNT("&x&d&f&3&7&3&7M&x&e&4&4&1&4&1a&x&e&9&4&a&4&an&x&e&e&5&4&5&4h&x&f&3&5&d&5&du&x&f&8&6&7&6&7n&x&f&d&7&0&7&0t &8» "),
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
