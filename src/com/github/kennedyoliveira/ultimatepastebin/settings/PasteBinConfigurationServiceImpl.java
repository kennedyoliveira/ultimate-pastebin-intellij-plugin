package com.github.kennedyoliveira.ultimatepastebin.settings;

import com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants.MAX_PASTES_TO_FETCH;

/**
 * <p>Configuration service for saving and loading plugin settings.</p>
 *
 * @author kennedy
 */
@State(name = "ultimatepastebin", storages = @Storage(id = "main", file = "ultimatepastebin_settings.xml"))
public class PasteBinConfigurationServiceImpl implements PersistentStateComponent<Element>, PasteBinConfigurationService {

  private static final Logger log = UltimatePasteBinUtils.log;
  private static final String ULTIMATE_PASTEBIN_PASSWORD_KEY = "ULTIMATE_PASTEBIN_KEY";

  /**
   * User name to authenticate at Pastebin.
   */
  private String username;

  /**
   * Devkey to authenticate at Pastebin.
   */
  private String devkey;

  /**
   * Flag setted after showing the welcome message, to preent reshowing it.
   */
  private boolean showedWelcomeMessage;

  /**
   * Flag setted after a valid credential is set
   */
  private boolean validCredentials;

  /**
   * Version that the configuration was saved
   */
  private String version;

  /**
   * Configuration for the total of pastes to fetch, default is 50 if not especified.
   */
  private int totalPastesToFetch = UltimatePasteBinConstants.DEFAULT_TOTAL_PASTES_TO_FETCH;

  /**
   * The current plugin language
   */
  private String currentLanguage;

  public static PasteBinConfigurationService getInstance() {
    return ServiceManager.getService(PasteBinConfigurationService.class);
  }

  @Nullable
  @Override
  public Element getState() {
    final Element element = new Element("UltimatePasteBinSettings");

    element.setAttribute("username", getUsername());
    element.setAttribute("devkey", getDevkey());
    element.setAttribute("showedWelcomeMessage", String.valueOf(showedWelcomeMessage));
    element.setAttribute("version", version);
    element.setAttribute("totalPastesToFetch", String.valueOf(totalPastesToFetch));

    if (currentLanguage != null)
      element.setAttribute("currentLanguage", currentLanguage);

    return element;
  }

  @Override
  public void loadState(Element state) {
    this.devkey = state.getAttributeValue("devkey", "");
    this.username = state.getAttributeValue("username", "");

    this.showedWelcomeMessage = Boolean.valueOf(state.getAttributeValue("showedWelcomeMessage", "false"));
    this.version = state.getAttributeValue("version");

    try {
      this.totalPastesToFetch = Integer.parseInt(state.getAttributeValue("totalPastesToFetch"));
    } catch (NumberFormatException e) {
      // If fails to recover the value, sets the default
      this.totalPastesToFetch = UltimatePasteBinConstants.DEFAULT_TOTAL_PASTES_TO_FETCH;
    }

    Optional.ofNullable(state.getAttributeValue("currentLanguage"))
            .ifPresent(this::setCurrentLanguage);
  }

  @Override
  public String getPassword() {
    String password = null;

    try {
      password = PasswordSafe.getInstance().getPassword(null, PasteBinConfigurationServiceImpl.class, ULTIMATE_PASTEBIN_PASSWORD_KEY);
    } catch (PasswordSafeException e) {
      log.info("Error getting the password for the key: " + ULTIMATE_PASTEBIN_PASSWORD_KEY, e);
    }

    return StringUtil.notNullize(password);
  }

  @Override
  public void setPassword(String password) {
    try {
      PasswordSafe.getInstance().storePassword(null, PasteBinConfigurationServiceImpl.class, ULTIMATE_PASTEBIN_PASSWORD_KEY, password);
    } catch (PasswordSafeException e) {
      log.info("Error while storing the password for the key: " + ULTIMATE_PASTEBIN_PASSWORD_KEY, e);
    }
  }

  @Override
  @Contract(pure = true)
  public boolean isAuthInfoPresent() {
    return StringUtil.isNotEmpty(username)
      && StringUtil.isNotEmpty(devkey)
      && StringUtil.isNotEmpty(getPassword());
  }

  @Override
  public boolean isShowedWelcomeMessage() {
    return showedWelcomeMessage;
  }

  @Override
  public void setShowedWelcomeMessage(boolean showedWelcomeMessage) {
    this.showedWelcomeMessage = showedWelcomeMessage;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean isValidCredentials() {
    return validCredentials;
  }

  @Override
  public void setValidCredentials(boolean validCredentials) {
    this.validCredentials = validCredentials;
  }

  @Override
  public int getTotalPastesToFetch() {
    return totalPastesToFetch;
  }

  @Override
  public void setTotalPastesToFetch(int totalPastesToFetch) {
    this.totalPastesToFetch = Math.min(totalPastesToFetch, MAX_PASTES_TO_FETCH);
  }

  @Override
  @Nullable
  public String getCurrentLanguage() {
    return currentLanguage;
  }

  @Override
  public void setCurrentLanguage(@Nullable String currentLanguage) {
    this.currentLanguage = currentLanguage;
  }

  @Override
  @NotNull
  public String getUsername() {
    return StringUtil.notNullize(username);
  }

  @Override
  public void setUsername(String username) {
    this.username = StringUtil.notNullize(username);
  }

  @Override
  @NotNull
  public String getDevkey() {
    return StringUtil.notNullize(devkey);
  }

  @Override
  public void setDevkey(String devkey) {
    this.devkey = StringUtil.notNullize(devkey);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PasteBinConfigurationServiceImpl that = (PasteBinConfigurationServiceImpl) o;
    return showedWelcomeMessage == that.showedWelcomeMessage &&
      validCredentials == that.validCredentials &&
      totalPastesToFetch == that.totalPastesToFetch &&
      Objects.equals(username, that.username) &&
      Objects.equals(devkey, that.devkey) &&
      Objects.equals(currentLanguage, that.currentLanguage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, devkey, showedWelcomeMessage, validCredentials, totalPastesToFetch, currentLanguage);
  }
}
