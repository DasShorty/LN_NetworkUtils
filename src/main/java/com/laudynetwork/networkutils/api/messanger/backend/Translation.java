package com.laudynetwork.networkutils.api.messanger.backend;

import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public record Translation(String key, TranslationLanguage language, String raw) {

    public MsgBuilder createBuilder() {
        return new MsgBuilder(this);
    }

    public MsgBuilder createBuilder(TagResolver... placeholder) {
        return new MsgBuilder(this, placeholder);
    }

    public static class MsgBuilder {

        private final List<Component> componentList = new ArrayList<>();

        public MsgBuilder(Translation translation, TagResolver... placeholder) {
            MiniMessage miniMessage = MiniMessage.miniMessage();
            val split = translation.raw.split("<br>");
            for (String s : split) {
                if (this.componentList.isEmpty()) {
                    this.componentList.add(miniMessage.deserialize(s, placeholder));
                } else {
                    this.componentList.add(Component.newline());
                    this.componentList.add(miniMessage.deserialize(s, placeholder));
                }
            }
        }

        public Component getData() {

            AtomicReference<Component> main = new AtomicReference<>(Component.empty());

            this.componentList.forEach(component -> main.set(main.get().append(component)));

            return main.get();
        }
    }

}
