package com.github.kennedyoliveira.ultimatepastebin.i18n;

import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;
import com.intellij.CommonBundle;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Message bundle for internationalization.
 */
public class MessageBundle {

  private static final Map<String, Locale> availableTranslations;
  private static final PasteBinConfigurationService pasteBinConfigurationService;

  static {
    pasteBinConfigurationService = ServiceManager.getService(PasteBinConfigurationService.class);

    // Defines the available languages in the format of key = Name of the language, Value the locale used
    availableTranslations = new HashMap<>();

    availableTranslations.put("German", new Locale.Builder().setLanguage("de").build());
    availableTranslations.put("Português Brasileiro", new Locale.Builder().setLanguage("pt").setRegion("BR").build());
    availableTranslations.put("English", new Locale.Builder().setLanguage("en").build());
    availableTranslations.put("French", new Locale.Builder().setLanguage("fr").build());
    availableTranslations.put("Norwegian", new Locale.Builder().setLanguage("no").build());
    availableTranslations.put("Nederlands", new Locale.Builder().setLanguage("nl").build());
  }

  private MessageBundle() {}

  /**
   * Get the message acconding
   * @param key
   * @param params
   * @return
   */
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
