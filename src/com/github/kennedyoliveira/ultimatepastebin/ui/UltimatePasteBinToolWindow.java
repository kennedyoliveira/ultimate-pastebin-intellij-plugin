package com.github.kennedyoliveira.ultimatepastebin.ui;

import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurableSettings;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * @author kennedy
 */
public class UltimatePasteBinToolWindow implements ToolWindowFactory {

  private static final Logger logger = UltimatePasteBinUtils.logger;

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    logger.info("Initializing Pastes ToolWindow");
    toolWindow.setStripeTitle("Ultimate PasteBin");
    toolWindow.setTitle("Ultimate PasteBin");

    ContentManager contentManager = toolWindow.getContentManager();

    Content content = contentManager.getFactory().createContent(toolWindow.getComponent(), null, false);

    ToolWindowService service = ServiceManager.getService(ToolWindowService.class);

    // Panel with toolbar
    SimpleToolWindowPanel simpleToolWindowPanel = new SimpleToolWindowPanel(true);

    // Scrolable panel
    JBScrollPane jbScrollPane = new JBScrollPane(service.getTree());
    simpleToolWindowPanel.add(jbScrollPane);
    simpleToolWindowPanel.setToolbar(createToolbar());

    content.setComponent(simpleToolWindowPanel);

    contentManager.addContent(content);

    toolWindow.activate(this::firstTimeOpen);
  }

  private void firstTimeOpen() {
    if (ServiceManager.getService(PasteBinConfigurationService.class).isAuthInfoPresent()) {
      validateSavedConfiguration();
    } else {
      noConfigAvailable();
    }
  }

  public JComponent createToolbar() {
    ActionGroup actionGroup = (ActionGroup) ActionManager.getInstance().getAction("ultimatepastebin.ToolwindowToolbar");
    return ActionManager.getInstance().createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, actionGroup, true).getComponent();
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
      logger.error("Validating stored credentials", e);
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
}
