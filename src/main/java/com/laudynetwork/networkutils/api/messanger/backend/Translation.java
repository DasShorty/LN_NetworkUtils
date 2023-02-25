package com.laudynetwork.networkutils.api.messanger.backend;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public record Translation(String key, TranslationLanguage language, String raw) {

    public MsgBuilder createBuilder() {
        return new MsgBuilder(this);
    }
    public static class MsgBuilder {
        @Getter
        private Component data;
        private final Translation translation;
        private final MiniMessage miniMessage;

        public MsgBuilder(Translation translation) {
            this.translation = translation;
            this.miniMessage = MiniMessage.miniMessage();
            this.data = this.miniMessage.deserialize(translation.raw);
        }

        public MsgBuilder replaceString(TagResolver... placeholder) {
            this.data = this.miniMessage.deserialize(translation.raw, placeholder);
            return this;
        }
    }

}
