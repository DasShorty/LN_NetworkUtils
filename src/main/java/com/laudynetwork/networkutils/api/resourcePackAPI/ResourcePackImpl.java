package com.laudynetwork.networkutils.api.resourcePackAPI;

public class ResourcePackImpl implements ResourcePackInterface {
    @Override
    public Character get(String name) {
        return items.get(name.toUpperCase());
    }

    @Override
    public boolean contains(Character character) {
        return items.containsValue(character);
    }

    @Override
    public boolean contains(String string) {
        return items.containsKey(string.toUpperCase());
    }

    @Override
    public String get(Character iconChar) {
        for (String elm : items.keySet()) {
            if (items.get(elm).equals(iconChar)) return elm;
        }
        return null;
    }
}
