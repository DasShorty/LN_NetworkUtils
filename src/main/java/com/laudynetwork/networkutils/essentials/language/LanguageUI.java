package com.laudynetwork.networkutils.essentials.language;

import com.laudynetwork.networkutils.NetworkUtils;
import com.laudynetwork.networkutils.api.MongoDatabase;
import com.laudynetwork.networkutils.api.gui.GUI;
import com.laudynetwork.networkutils.api.gui.GUIItem;
import com.laudynetwork.networkutils.api.item.itembuilder.HeadBuilder;
import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
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
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class LanguageUI extends GUI {

    private final MessageAPI msgApi = new MessageAPI(NetworkUtils.getINSTANCE().getMessageCache(), MessageAPI.PrefixType.SYSTEM);
    private final String language;
    private final NetworkPlayer networkPlayer;
    private final Plugin plugin;

    public LanguageUI(Player player, Component displayName, MongoDatabase database, Plugin plugin) {
        super(player, displayName, 27);
        this.plugin = plugin;
        this.networkPlayer = new NetworkPlayer(database, player.getUniqueId());
        this.language = this.networkPlayer.getLanguage();
    }

    @Override
    public void generateGUI(Player player) {

        set(11, getItem(LanguageHead.GERMAN.getHeadTexture(), "networkutils.language.ui.german", language), (clicker, clickedItem, clickType) -> {
            changeLanguage("de", clicker);
            return GUIItem.GUIAction.CLOSE;
        });
        set(15, getItem(LanguageHead.ENGLISH.getHeadTexture(), "networkutils.language.ui.english", language), (clicker, clickedItem, clickType) -> {
            changeLanguage("en", clicker);
            return GUIItem.GUIAction.CLOSE;
        });

        setBackground(Material.GRAY_STAINED_GLASS_PANE);
    }

    private void changeLanguage(String language, Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            networkPlayer.setLanguage(language);

            val languageName = this.msgApi.getTranslation(language, "networkutils.language." + language);
            player.sendMessage(this.msgApi.getMessage(networkPlayer.getLanguage(), "networkutils.language.changed", Placeholder.component("language", languageName)));
        });
    }

    private HeadBuilder getItem(String headTexture, String key, String language) {
        var headBuilder = new HeadBuilder().skullOwner(headTexture);

        if (this.msgApi.existTranslation(key + ".lore"))
            headBuilder = headBuilder.lore(getLore(language, key + ".lore"));


        return headBuilder.displayName(this.msgApi.getTranslation(language, key + ".title"));
    }

    private ArrayList<Component> getLore(String language, String key) {

        val translation = this.msgApi.getRaw(language, key);
        val split = translation.rawTranslation().split(";");

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
