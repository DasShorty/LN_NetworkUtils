package com.laudynetwork.networkutils.api.player;

import lombok.Getter;
@Getter
public enum ProtocolVersion {
    /**
     * 1.19.1 also represents 1.19.2
     * */
    V_1_19_1(760, "1.19.1/2"),
    V_1_19_3(761, "1.19.3");


    private final int protocolVersion;
    private final String versionName;

    ProtocolVersion(int protocolVersion, String versionName) {
        this.protocolVersion = protocolVersion;
        this.versionName = versionName;
    }

    public static ProtocolVersion getByProtocolVersion(int protocolVersion) {

        for (ProtocolVersion value : ProtocolVersion.values()) {
            if (value.protocolVersion == protocolVersion)
                return value;
        }

        return null;
    }
}
