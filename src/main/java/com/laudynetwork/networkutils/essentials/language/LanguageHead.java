package com.laudynetwork.networkutils.essentials.language;

import lombok.Getter;

public enum LanguageHead {

    GERMAN("5e7899b4806858697e283f084d9173fe487886453774626b24bd8cfecc77b3f"),
    JAPANESE("8043ae9bbfa8b8bbb5c964bbce45fbe79a3ad742be07b56607c68c8e11164"),
    RUSSIAN("16eafef980d6117dabe8982ac4b4509887e2c4621f6a8fe5c9b735a83d775ad"),
    ENGLISH("cd91456877f54bf1ace251e4cee40dba597d2cc40362cb8f4ed711e50b0be5b3");

    @Getter
    private final String headTexture;

    LanguageHead(String headTexture) {
        this.headTexture = headTexture;
    }
}
