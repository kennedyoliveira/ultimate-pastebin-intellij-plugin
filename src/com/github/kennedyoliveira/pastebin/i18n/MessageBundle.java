package com.github.kennedyoliveira.pastebin.i18n;

import com.github.kennedyoliveira.pastebin.settings.PasteBinConfigurationService;
import com.intellij.CommonBundle;
import com.intellij.openapi.components.ServiceManager;
import com.sun.istack.internal.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author kennedy
 */
public class MessageBundle {

    private final static Map<String, Locale> availableTranslations;
    private final static PasteBinConfigurationService pasteBinConfigurationService;

    static {
        pasteBinConfigurationService = ServiceManager.getService(PasteBinConfigurationService.class);

        // Defines the available languages in the format of key = Name of the language, Value the locale used
        availableTranslations = new HashMap<>();

        availableTranslations.put("Portugues Brasileiro", new Locale.Builder().setLanguage("pt").setRegion("BR").build());
        availableTranslations.put("English", new Locale.Builder().setLanguage("en").build());
    }

    @Nullable
    public static String getMessage(String key, Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    @NotNull
    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("messages", getLanguageLocale());
    }

    /**
     * @return The locale based on the configuration for language, or the {@link Locale#getDefault()}
     * if the configuration is missing or isn't found
     */
    @NotNull
    public static Locale getLanguageLocale() {
        return availableTranslations.getOrDefault(pasteBinConfigurationService.getCurrentLanguage(), Locale.getDefault());
    }

    /**
     * @return The available languages.
     */
    @NotNull
    public static Set<String> getAvailableLanguages() {
        return Collections.unmodifiableSet(availableTranslations.keySet());
    }
}
