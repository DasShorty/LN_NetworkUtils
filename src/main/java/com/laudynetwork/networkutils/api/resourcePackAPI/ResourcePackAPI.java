package com.laudynetwork.networkutils.api.resourcePackAPI;

import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcePackAPI {
    final static List<Class<? extends ResourcePackInterface>> enums = Arrays.asList(
            Rankbadges.class,
            GUI.class
    );

    public static Map<String, ResourcePackInterface> parse(String input) {
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
    }

    @SneakyThrows
    private static ResourcePackInterface resourcePackEnum(Class<? extends ResourcePackInterface> aClass) {
        return aClass.getDeclaredConstructor().newInstance();
    }
}
