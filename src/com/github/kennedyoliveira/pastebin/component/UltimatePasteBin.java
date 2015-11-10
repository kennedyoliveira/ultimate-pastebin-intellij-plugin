package com.github.kennedyoliveira.pastebin.component;

import com.github.kennedyoliveira.pastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.pastebin.service.PasteBinService;
import com.github.kennedyoliveira.pastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin.settings.PasteBinConfigurableSettings;
import com.github.kennedyoliveira.pastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.pastebin.settings.PasteBinSettings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/8/15.
 */
public class UltimatePasteBin implements ApplicationComponent {

    private final static String VERSION = "1.0.0";
    private final static Logger log = Logger.getInstance(UltimatePasteBin.class);

    @Override
    public void initComponent() {
        PasteBinConfigurationService configurationService = ServiceManager.getService(PasteBinConfigurationService.class);

        // Sets the current used language for translations
        if(configurationService.getCurrentLanguage() != null) {
            Locale.setDefault(MessageBundle.getLanguageLocale());
        }

        // First time in this version
        // Show welcome message
        if (!configurationService.isShowedWelcomeMessage() || (!configurationService.getVersion().equals(VERSION))) {
            String message = getMessage("ultimatepastebin.welcomemessage");
            Notifications.Bus.notify(new Notification("Ultimate PasteBin Welcome Message", "Ultimate PasteBin", message, NotificationType.INFORMATION, (notification, event) -> {
                if ("#settings".equals(event.getDescription())) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(null, PasteBinConfigurableSettings.class);
                } else {
                    NotificationListener.URL_OPENING_LISTENER.hyperlinkUpdate(notification, event);
                }
            }));

            configurationService.setShowedWelcomeMessage(true);
            configurationService.setVersion(VERSION);
        } else {
            PasteBinSettings pasteBinSettings = configurationService.getPasteBinSettings();

            // If there's no configuration
            if (pasteBinSettings == null || pasteBinSettings.getPasteBinAccountCredentials() == null ||
                    !pasteBinSettings.getPasteBinAccountCredentials().getUserName().isPresent() ||
                    !pasteBinSettings.getPasteBinAccountCredentials().getPassword().isPresent() ||
                    pasteBinSettings.getPasteBinAccountCredentials().getDevKey() == null ||
                    pasteBinSettings.getPasteBinAccountCredentials().getDevKey().isEmpty()) {

                Notifications.Bus.notify(new Notification("No Configuration Found for Ultimate PasteBin",
                                                          "Ultimate PasteBin",
                                                          getMessage("ultimatepastebin.missing.configuration"),
                                                          NotificationType.WARNING,
                                                          (notification, event) -> {
                                                              ShowSettingsUtil.getInstance().showSettingsDialog(null, PasteBinConfigurableSettings.class);
                                                          }));
            } else {
                boolean validCredentials = false;

                try {
                    PasteBinService pasteBinService = ServiceManager.getService(PasteBinService.class);

                    validCredentials = pasteBinService.isCredentialsValid();
                } catch (Exception e) {
                    log.error(e);
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
        }
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Ultimate PasteBin";
    }
}
