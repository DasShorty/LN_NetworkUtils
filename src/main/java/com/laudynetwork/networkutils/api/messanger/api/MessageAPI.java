package com.laudynetwork.networkutils.api.messanger.api;

import com.laudynetwork.networkutils.api.messanger.backend.MessageCache;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Class made by DasShorty ~Anthony
 */
@RequiredArgsConstructor
public class MessageAPI {

    @MessagePrefix(prefix = PrefixType.SYSTEM)
    private final MessageCache messageCache;
    private final PrefixType prefixType;

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
