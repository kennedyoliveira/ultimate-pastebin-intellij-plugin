package com.github.kennedyoliveira.ultimatepastebin.component;

import com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurableSettings;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinSettings;
import com.github.kennedyoliveira.ultimatepastebin.utils.StreamUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.RuntimeInterruptedException;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants.DONATION_URL;
import static com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants.PROJECT_URL;
import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;
import static java.util.stream.Collectors.joining;

/**
 * Created by kennedy on 11/8/15.
 */
public class UltimatePasteBin implements ApplicationComponent {

    private final static String VERSION = "1.3.1.1";
    private final static Logger log = Logger.getInstance(UltimatePasteBin.class);

    @Override
    public void initComponent() {
        PasteBinConfigurationService configurationService = ServiceManager.getService(PasteBinConfigurationService.class);

        // Sets the current used language for translations
        if (configurationService.getCurrentLanguage() != null) {
            Locale.setDefault(MessageBundle.getLanguageLocale());
        }

        // if the configuration version is not null (mean there was a version before this one) and the version is different
        // from the actual, i show the change log
        if (configurationService.getVersion() != null && !configurationService.getVersion().equals(VERSION)) {
            try {
                String changes = StreamUtils.readAllLines(getClass().getResourceAsStream("/last-change.txt"));

                Notifications.Bus.notify(new Notification("ultimatepastebin.changelogmessage",
                        "Ultimate PasteBin Changes",
                        getMessage("ultimatepastebin.changelogmessage", DONATION_URL, changes),
                        NotificationType.INFORMATION,
                        NotificationListener.URL_OPENING_LISTENER));

                // Update the version
                configurationService.setVersion(VERSION);
            } catch (Exception e) {
                log.error(e);
            }
        }

        // First time
        // Show welcome message
        if (!configurationService.isShowedWelcomeMessage()) {
            // Gets the welcome message
            String message = getMessage("ultimatepastebin.welcomemessage", PROJECT_URL, DONATION_URL);

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
