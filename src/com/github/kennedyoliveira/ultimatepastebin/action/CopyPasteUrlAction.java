package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.utils.ClipboardUtils;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;

import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * <p>Copy the URL of a paste to clipboard and show a notification to the user.</p>
 */
public class CopyPasteUrlAction extends AbstractPasteSelectedAction {

  private static final Logger logger = UltimatePasteBinUtils.logger;

  @Override
  public void actionPerformed(AnActionEvent e) {
    ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

    Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

    selectedPaste.ifPresent(this::copyToClipboardAndNotify);
  }

  /**
   * Copy the paste URL to the clipboard and show a notification to the user.
   *
   * @param paste Paste to copy the URL.
   */
  private void copyToClipboardAndNotify(Paste paste) {
    logger.info("Copying Paste URL to clipboard: " + paste);
    String url = paste.getUrl();

    ClipboardUtils.copyToClipboard(url);

    Notifications.Bus.notify(new Notification("Paste URL Copied to Clipboard",
                                              "Ultimate PasteBin",
                                              getMessage("ultimatepastebin.actions.copypasteurl.ok.notification.message", url),
                                              NotificationType.INFORMATION,
                                              NotificationListener.URL_OPENING_LISTENER));
  }

  @Override
  public void update(AnActionEvent e) {
    super.update(e);

    e.getPresentation().setText(getMessage("ultimatepastebin.actions.copypasteurl.text"));
    e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.copypasteurl.description"));
  }
}
