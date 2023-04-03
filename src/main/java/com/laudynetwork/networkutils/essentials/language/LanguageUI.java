package com.laudynetwork.networkutils.essentials.language;

import com.laudynetwork.networkutils.api.gui.GUI;
import com.laudynetwork.networkutils.api.item.itembuilder.HeadBuilder;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LanguageUI extends GUI {

    private final MessageAPI msgApi;
    private final MessageBackend msgBackend;

    public LanguageUI(Player player, Component displayName, MessageAPI msgApi, MessageBackend msgBackend) {
        super(player, displayName, 27);
        this.msgApi = msgApi;
        this.msgBackend = msgBackend;
    }

    @Override
    public void generateGUI(Player player) {

        val networkPlayer = new NetworkPlayer(this.msgBackend.getSql(), player.getUniqueId());

        val language = networkPlayer.getLanguage();

        set(11, getItem(LanguageHead.GERMAN.getHeadTexture(), "networkutils.language.ui.german", language));
        set(15, getItem(LanguageHead.ENGLISH.getHeadTexture(), "networkutils.language.ui.english", language));

        setBackground(Material.GRAY_STAINED_GLASS_PANE);
    }

    private HeadBuilder getItem(String headTexture, String key, TranslationLanguage language) {
        val headBuilder = new HeadBuilder().skullOwner(headTexture);
        return headBuilder.displayName(this.msgApi.getTranslation(language, key + ".title"))
                .lore(getLore(language, key + ".lore"));
    }

    private ArrayList<Component> getLore(TranslationLanguage language, String key) {

        val translation = this.msgBackend.getTranslation(language, key);
        val split = translation.raw().split(";");

        MiniMessage miniMessage = MiniMessage.miniMessage();
        ArrayList<Component> components = new ArrayList<>();

        for (String s : split) {
            components.add(miniMessage.deserialize(s).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        return components;
    }

    @Override
    public void onClose(Player player) {

    }
}
