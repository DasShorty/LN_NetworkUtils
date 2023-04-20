package com.laudynetwork.networkutils.essentials.language;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.gui.GUI;
import com.laudynetwork.networkutils.api.gui.GUIItem;
import com.laudynetwork.networkutils.api.item.itembuilder.HeadBuilder;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.backend.MessageBackend;
import com.laudynetwork.networkutils.api.messanger.backend.TranslationLanguage;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;
import com.laudynetwork.networkutils.api.player.event.PlayerChangeLanguageEvent;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LanguageUI extends GUI {

    private final MessageAPI msgApi;
    private final MessageBackend msgBackend;
    private final TranslationLanguage language;
    private final NetworkPlayer networkPlayer;

    public LanguageUI(Player player, Component displayName, MessageAPI msgApi, MessageBackend msgBackend) {
        super(player, displayName, 27);
        this.msgApi = msgApi;
        this.msgBackend = msgBackend;
        this.networkPlayer = new NetworkPlayer(this.msgBackend.getSql(), player.getUniqueId());
        this.language = this.networkPlayer.getLanguage();
    }

    @Override
    public void generateGUI(Player player) {

        set(11, getItem(LanguageHead.GERMAN.getHeadTexture(), "networkutils.language.ui.german", language), (clicker, clickedItem, clickType) -> {
            changeLanguage(TranslationLanguage.GERMAN, clicker);
            return GUIItem.GUIAction.CANCEL;
        });
        set(15, getItem(LanguageHead.ENGLISH.getHeadTexture(), "networkutils.language.ui.english", language), (clicker, clickedItem, clickType) -> {
            changeLanguage(TranslationLanguage.ENGLISH, clicker);
            return GUIItem.GUIAction.CANCEL;
        });

        setBackground(Material.GRAY_STAINED_GLASS_PANE);
    }

    private void changeLanguage(TranslationLanguage language, Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(NetworkUtils.getINSTANCE(), () -> {
            networkPlayer.setLanguage(language);

            val languageName = this.msgApi.getTranslation(language, "networkutils.language." + language.getDbName());
            player.sendMessage(this.msgApi.getMessage(networkPlayer.getLanguage(), "networkutils.language.changed", Placeholder.component("language", languageName)));
        });
    }

    private HeadBuilder getItem(String headTexture, String key, TranslationLanguage language) {
        var headBuilder = new HeadBuilder().skullOwner(headTexture);

        if (this.msgBackend.existsTranslation(key + ".lore"))
            headBuilder = headBuilder.lore(getLore(language, key + ".lore"));


        return headBuilder.displayName(this.msgApi.getTranslation(language, key + ".title"));
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
        Bukkit.getPluginManager().callEvent(new PlayerChangeLanguageEvent(networkPlayer, language, networkPlayer.getLanguage()));
    }
}
