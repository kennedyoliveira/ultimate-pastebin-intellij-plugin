package com.github.kennedyoliveira.ultimatepastebin.settings;

import com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Configuration service for interacting with plugin settings
 */
public interface PasteBinConfigurationService {

  /**
   * <p>Get the stored password.</p>
   * <p>If there is a problem getting the password this method returns an empty String {@code ""}.</p>
   *
   * @return The stored password or empty String in case of a problem.
   */
  String getPassword();

  /**
   * <p>Set the password to authenticate with PasteBin.</p>
   *
   * @param password The new password.
   */
  void setPassword(String password);

  /**
   * Validates if the auth information is filled.
   *
   * @return {@link Boolean#TRUE} if all the necessary settings are filled, {@link Boolean#FALSE} otherwise.
   */
  @Contract(pure = true, value = "null -> false;")
  boolean isAuthInfoPresent();

  /**
   * @return If already showed the welcome message to the user
   */
  boolean isShowedWelcomeMessage();

  /**
   * Set that already showed the welcome message to the user
   *
   * @param showedWelcomeMessage showed or not
   */
  void setShowedWelcomeMessage(boolean showedWelcomeMessage);

  /**
   * @return Version that this settings was saved
   */
  String getVersion();

  /**
   * Sets the version that this configuration is to be saved
   *
   * @param version the new version
   */
  void setVersion(String version);

  /**
   * @return If the credentials from the configuration is valid or not
   */
  boolean isValidCredentials();

  /**
   * Sets if the credentials is valid or not
   *
   * @param validCredentials {@code True} for valid credentials, {@code false} otherwise
   */
  void setValidCredentials(boolean validCredentials);

  /**
   * @return The total of users pastes that needs to be fetched.
   */
  int getTotalPastesToFetch();

  /**
   * Sets the new {@code totalPastesToFetch}, if the value is higher than 1000, sets to 1000, if lower than 1 sets to 1
   *
   * @param totalPastesToFetch The new total
   */
  void setTotalPastesToFetch(int totalPastesToFetch);

  /**
   * @return The current language being used by the plugin.
   */
  @Nullable
  String getCurrentLanguage();

  /**
   * <p>Sets the current language to be used by the plugin.</p>
   * <p>The language must be one of the {@link MessageBundle#getAvailableLanguages()}, if the language especified is
   * {@code null} or not exists in the {@link MessageBundle#getAvailableLanguages()} then the {@link Locale#getDefault()}
   * will be used instead</p>
   *
   * @param currentLanguage one of the {@link MessageBundle#getAvailableLanguages()} or null to use the system default
   */
  void setCurrentLanguage(@Nullable String currentLanguage);

  /**
   * @return The username for authentication.
   */
  @NotNull
  String getUsername();

  /**
   * Sets the new username for authentication.
   *
   * @param username THe new username.
   */
  void setUsername(String username);

  /**
   * @return The devkey for authentication.
   */
  @NotNull
  String getDevkey();

  /**
   * Sets the new devkey for authentication.
   *
   * @param devkey The new devkey.
   */
  void setDevkey(String devkey);
}
