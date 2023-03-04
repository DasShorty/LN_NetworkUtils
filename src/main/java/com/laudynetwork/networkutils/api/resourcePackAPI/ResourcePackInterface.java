package com.laudynetwork.networkutils.api.resourcePackAPI;

import java.util.HashMap;
import java.util.Map;

public interface ResourcePackInterface {
    Map<String, Character> items = new HashMap<>();

    String get(Character iconChar);

    Character get(String name);

    boolean contains(Character character);

    boolean contains(String string);
}
