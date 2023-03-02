package com.laudynetwork.networkutils.api.resourcePackAPI;

public enum Rankbadges {
    ADMIN('\uE100'),
    DEVELOPER('\uE101'),
    STAFF('\uE102'),
    CONTENT('\uE103'),
    BUILDER('\uE104'),
    CREATOR('\uE105'),
    VIP('\uE106'),
    SPARK('\uE107'),
    PRIME('\uE108'),
    PREMIUM('\uE109'),
    PLAYER('\uE10A'),
    VANISH('\uE10B');

    private final Character IconChar;

    Rankbadges(Character IconChar) {
        this.IconChar = IconChar;
    }
}
