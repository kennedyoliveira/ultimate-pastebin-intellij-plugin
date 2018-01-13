package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.utils.ClipboardUtils;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Action to copy the content of a paste to CLipboard.
 */
public class CopyPasteContentAction extends AbstractPasteSelectedAction {

  private static final Logger logger = UltimatePasteBinUtils.logger;

  @Override
  public void actionPerformed(AnActionEvent e) {
    ToolWindowService service = ServiceManager.getService(ToolWindowService.class);

    service.getSelectedPaste().ifPresent(this::downloadAndCopyToClipBoard);
  }

  /**
   * Fetch the paste content and copy it to clipboard.
   *
   * @param paste Paste to be fetched
   */
  private void downloadAndCopyToClipBoard(Paste paste) {
    new Task.Backgroundable(null, getMessage("ultimatepastebin.paste.content.fetching"), false) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        try {
          final PasteBinService service = ServiceManager.getService(PasteBinService.class);

          logger.info("Getting paste content: " + paste);
          final String pasteContent = service.getPasteBin().getPasteContent(paste);

          logger.info("Copying paste content to clipboard");
          ClipboardUtils.copyToClipboard(pasteContent);

          Notifications.Bus.notify(new Notification("Paste contents copied to clipboard",
                                                    "Ultimate PasteBin",
                                                    getMessage("ultimatepastebin.actions.copypasteclipboard.ok.notification.message"),
                                                    NotificationType.INFORMATION));
        } catch (Exception e) {
          logger.error("Failed to copy paste content to clipboard", e);
          Notifications.Bus.notify(new Notification("Error fetching paste contents",
                                                    "Ultimate PasteBin",
                                                    getMessage("ultimatepastebin.actions.copypastecontent.genericerror.notification.message", e.getMessage()),
                                                    NotificationType.ERROR));
        }
      }
    }.queue();
  }

  @Override
  public void update(AnActionEvent e) {
    super.update(e);

    e.getPresentation().setText(getMessage("ultimatepastebin.actions.copypasteclipboard.text"));
    e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.copypasteclipboard.description"));
  }
}
