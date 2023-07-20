package com.laudynetwork.networkutils.api.resourcePackAPI.text;

// Character.toString((char) Integer.parseInt(prefix.split(" ")[0].substring(2), 16))

import com.laudynetwork.networkutils.api.resourcePackAPI.ResourcePackImpl;

public class GameModeBadges extends ResourcePackImpl {
    GameModeBadges() {
        items.put("MAN", '\uE120');
        items.put("HUNTER", '\uE121');
    }
}
