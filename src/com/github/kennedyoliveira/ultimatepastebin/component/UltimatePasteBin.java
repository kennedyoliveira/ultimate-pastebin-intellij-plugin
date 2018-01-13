package com.github.kennedyoliveira.ultimatepastebin.component;

import com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurableSettings;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.ultimatepastebin.utils.StreamUtils;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants.*;
import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Principal component of the Plugin, it will load everything
 */
public class UltimatePasteBin implements ApplicationComponent {

  private static final Logger log = UltimatePasteBinUtils.logger;
  private static final String ULTIMATE_PASTE_BIN = "Ultimate PasteBin";

  @Override
  public void initComponent() {
    log.info("Initializing UltimatePasteBin v" + VERSION + "...");
    final PasteBinConfigurationService configurationService = ServiceManager.getService(PasteBinConfigurationService.class);

    // Sets the current used language for translations
    log.info("Checking language configuration...");
    if (configurationService.getCurrentLanguage() != null) {
      final Locale language = MessageBundle.getLanguageLocale();
      log.info("Setting language to "  + language);
      Locale.setDefault(language);
    }

    showChangeLogIfNeeded();

    // First time
    // Show welcome message
    if (!configurationService.isShowedWelcomeMessage()) {
      showWelcomeMessage();
    } else {
      if (configurationService.isAuthInfoPresent()) {
        validateSavedConfiguration();
      } else {
        // If there's no configuration
        noConfigAvailable();
      }
    }
  }

  /**
   * Show the welcome message for the User.
   */
  private void showWelcomeMessage() {
    PasteBinConfigurationService configurationService = ServiceManager.getService(PasteBinConfigurationService.class);

    // Gets the welcome message
    String message = getMessage("ultimatepastebin.welcomemessage", PROJECT_URL, DONATION_URL);

    Notifications.Bus.notify(new Notification("Ultimate PasteBin Welcome Message", ULTIMATE_PASTE_BIN, message, NotificationType.INFORMATION, (notification, event) -> {
      if ("#settings".equals(event.getDescription())) {
        ShowSettingsUtil.getInstance().showSettingsDialog(null, PasteBinConfigurableSettings.class);
      } else {
        NotificationListener.URL_OPENING_LISTENER.hyperlinkUpdate(notification, event);
      }
    }));

    configurationService.setShowedWelcomeMessage(true);
    configurationService.setVersion(VERSION);
  }

  /**
   * Validates the saved configuration for the plugin.
   */
  private void validateSavedConfiguration() {
    boolean validCredentials = false;

    try {
      PasteBinService pasteBinService = ServiceManager.getService(PasteBinService.class);

      validCredentials = pasteBinService.isCredentialsValid();
    } catch (Exception e) {
      log.error("Validating stored credentials", e);
    }

    if (!validCredentials) {
      Notifications.Bus.notify(new Notification("Invalid configuration for Ultimate PasteBin",
                                                "Ultimate PasteBin",
                                                getMessage("ultimatepastebin.invalid.credentials"),
                                                NotificationType.ERROR,
                                                (notification, event) -> {
                                                  ShowSettingsUtil.getInstance().showSettingsDialog(null, PasteBinConfigurableSettings.class);
                                                  notification.expire();
                                                }));
    } else {
      ServiceManager.getService(ToolWindowService.class).fetchPastes();
    }
  }

  /**
   * If there's no config available take some action to notify the user.
   */
  private void noConfigAvailable() {
    Notifications.Bus.notify(new Notification("No Configuration Found for Ultimate PasteBin",
                                              "Ultimate PasteBin",
                                              getMessage("ultimatepastebin.missing.configuration"),
                                              NotificationType.WARNING,
                                              (notification, event) -> {
                                                ShowSettingsUtil.getInstance().showSettingsDialog(null, PasteBinConfigurableSettings.class);
                                              }));
  }

  /**
   * <p>Checks if needs to show the change logger message and update the saved version {@link PasteBinConfigurationService#setVersion(String)}.</p>
   * <p>if the configuration version is not null (mean there was a version before this one) and the version is different
   * from the actual, i show the change logger.</p>
   */
  private void showChangeLogIfNeeded() {
    log.debug("Checking if is a new version launched for the first time");
    PasteBinConfigurationService configurationService = ServiceManager.getService(PasteBinConfigurationService.class);

    // last version the plugin was executed
    String lastVersion = configurationService.getVersion();

    if (StringUtil.equals(lastVersion, VERSION)) {
      log.debug("New version detected");
      try {
        log.debug("Reading changes");
        String changes = StreamUtils.readAllLines(getClass().getResourceAsStream("/last-change.txt"));

        log.debug("Sending notification");
        Notifications.Bus.notify(new Notification("ultimatepastebin.changelogmessage",
                                                  "Ultimate PasteBin Changes",
                                                  getMessage("ultimatepastebin.changelogmessage", DONATION_URL, changes),
                                                  NotificationType.INFORMATION,
                                                  NotificationListener.URL_OPENING_LISTENER));

        // Update the version
        configurationService.setVersion(VERSION);
      } catch (Exception e) {
        log.error("Failed to check for new version changes", e);
      }
    }
  }

  @Override
  public void disposeComponent() {
    log.info("Disposing UltimatePasteBin component...");
  }

  @NotNull
  @Override
  public String getComponentName() {
    return "Ultimate PasteBin";
  }
}
