package de.shortexception.networkutils.api.messanger;

import de.shortexception.networkutils.api.sql.SQLConnection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection"})
public class SQLTextHandler {

    private final SQLConnection connection;

    public SQLTextHandler(Plugin plugin) {
        connection = new SQLConnection(plugin.getConfig().getString("language.jdbc"), plugin.getConfig().getString("language.user"), plugin.getConfig().getString("language.pwd"));
    }

    public String getKey(String key, Language language) {
        var translatedString = "&ckey is invalid! " + key;
        try {
            var prepareStatement = connection.getConnection().prepareStatement("SELECT * FROM `translations` WHERE 'languageKey' = " + key);
            var resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                translatedString = resultSet.getString(language.name().toLowerCase());
            }

            prepareStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return translatedString;
    }

    public Component translateColors(String raw) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }

    public String translateReplacements(String raw, Replacement... replacements) {
        StringBuilder rawBuilder = new StringBuilder(raw);
        for (Replacement replacement : replacements) {
            rawBuilder.append(rawBuilder.toString().replaceAll(replacement.placeholder(), replacement.replaced().toString()));
        }
        return rawBuilder.toString();
    }

    record Replacement(String placeholder, Object replaced){}

    public enum Language {
        GERMAN,
        ENGLISH,
        RUSSIAN,
        JAPANESE,
        FRENCH,
        CHINESE,
        SPANISH
    }
}
