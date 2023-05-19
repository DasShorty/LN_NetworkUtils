package com.laudynetwork.networkutils.api.resourcePackAPI;

import com.laudynetwork.networkutils.api.resourcePackAPI.text.Overlay;
import com.laudynetwork.networkutils.api.resourcePackAPI.text.Rankbadges;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourcePackAPI {
    final static Map<String, Class<? extends ResourcePackInterface>> enums = Map.of(
            "rankBadges", Rankbadges.class,
            "overlay", Overlay.class
    );
    private static final Pattern pattern = Pattern.compile("\\{(.*?):(.*?)}");

    /*public static Map<String, ResourcePackInterface> parse(String input) {
        Map<String, ResourcePackInterface> data = new HashMap<>();

        for (Class<? extends ResourcePackInterface> temp : enums){
            ResourcePackInterface packEnum = resourcePackEnum(temp);
            int index = input.indexOf(packEnum.toFind());
            while (index != -1){
                int startIndex = input.indexOf(":", index);
                int endIndex = input.indexOf("}", startIndex);

                String name = input.substring(startIndex + 1, endIndex);
                String replace = packEnum.toFind() + name + "}";

                data.put(replace, packEnum);

                input = input.replace(replace, name);
                index = input.indexOf(packEnum.toFind());
            }
        }
        return data;
    }*/

    public static String convert(String input) {
        Map<String, String> data = parse(input);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            ResourcePackInterface res = resourcePackEnum(enums.get(entry.getKey()));
            if (res.contains(entry.getValue()))
                input = input.replaceAll("\\{" + entry.getKey() + ":" + entry.getValue() + "}", String.valueOf(res.get(entry.getValue())));
        }
        return input;
    }

    private static Map<String, String> parse(String input) {
        Map<String, String> result = new HashMap<>();
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            if (!result.containsKey(key) && (enums.containsKey(key)))
                result.put(key, value);
        }
        return result;
    }

    @SneakyThrows
    private static ResourcePackInterface resourcePackEnum(Class<? extends ResourcePackInterface> aClass) {
        return aClass.getDeclaredConstructor().newInstance();
    }
}
