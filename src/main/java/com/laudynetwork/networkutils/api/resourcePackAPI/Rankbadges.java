package com.laudynetwork.networkutils.api.resourcePackAPI;

// Character.toString((char) Integer.parseInt(prefix.split(" ")[0].substring(2), 16))

public class Rankbadges extends ResourcePackImpl {
    Rankbadges() {
        items.put("ADMIN", '\uE100');
        items.put("DEVELOPER", '\uE101');
        items.put("STAFF", '\uE102');
        items.put("CONTENT", '\uE103');
        items.put("BUILDER", '\uE104');
        items.put("CREATOR", '\uE105');
        items.put("VIP", '\uE106');
        items.put("SPARK", '\uE107');
        items.put("PRIME", '\uE108');
        items.put("PREMIUM", '\uE109');
        items.put("PLAYER", '\uE10A');
        items.put("VANISH", '\uE10B');
        items.put("BETA", '\uE10C');
    }
}
