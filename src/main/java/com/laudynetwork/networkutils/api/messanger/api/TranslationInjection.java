package com.laudynetwork.networkutils.api.messanger.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class made by DasShorty ~Anthony
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TranslationInjection {
    MessageAPI.PrefixType prefix();
}
