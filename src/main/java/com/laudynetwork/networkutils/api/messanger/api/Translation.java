package com.laudynetwork.networkutils.api.messanger.api;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

import java.util.Arrays;

public record Translation(String key, TranslationLanguage language, String raw) {

    public MsgBuilder createBuilder() {
        return new MsgBuilder(this);
    }
    public static class MsgBuilder {

        private final Translation translation;
        private String rawData;

        public MsgBuilder(Translation translation) {
            this.translation = translation;
            this.rawData = translation.key();
        }

        public MsgBuilder replaceString(Replacement... replacements) {
            Arrays.stream(replacements).toList().forEach(replacement -> {
                rawData = translation.raw.replaceAll(replacement.before(), replacement.after().toString());
            });
            return this;
        }

        public Component build() {
            return Component.text(ChatColor.translateAlternateColorCodes('&', rawData));
        }
    }

}
