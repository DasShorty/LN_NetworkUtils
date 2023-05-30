package com.laudynetwork.networkutils.api.messanger.backend;

import com.laudynetwork.networkutils.api.messanger.api.MessageAPI;
import com.laudynetwork.networkutils.api.messanger.api.TranslationInjection;
import lombok.val;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Class made by DasShorty ~Anthony
 */
public class MessageAnnotationInjector {

    private final MessageCache cache;

    public MessageAnnotationInjector(MessageCache cache) {
        this.cache = cache;
    }

    private Set<Field> scanFiles() {
        val reflections = new Reflections("com.laudynetwork");
        return reflections.getFieldsAnnotatedWith(TranslationInjection.class);
    }

    public void initVariables() {
        scanFiles().forEach(field -> {
            field.setAccessible(true);
            try {
                field.set(field.getClass(), new MessageAPI(cache, field.getAnnotation(TranslationInjection.class).prefix()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
