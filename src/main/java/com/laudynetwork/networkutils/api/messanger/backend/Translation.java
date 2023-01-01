package com.laudynetwork.networkutils.api.messanger.backend;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public record Translation(String key, TranslationLanguage language, String raw) {

    public MsgBuilder createBuilder() {
        return new MsgBuilder(this);
    }
    public static class MsgBuilder {
        private Component data;
        private final Translation translation;

        public MsgBuilder(Translation translation) {
            this.data = LegacyComponentSerializer.legacyAmpersand().deserialize(translation.raw()).color(TextColor.fromHexString("#CCCCCC"));
            this.translation = translation;
        }

        public MsgBuilder replaceString(Replacement... replacements) {

            String msg = LegacyComponentSerializer.legacyAmpersand().serialize(this.data);

            for (Replacement replacement : replacements) {
                msg = msg.replaceAll(replacement.before(), String.valueOf(replacement.after()));
            }

            this.data = LegacyComponentSerializer.legacyAmpersand().deserialize(msg);

            return this;
        }

        public Component build() {
            return this.data;
        }
    }

}
