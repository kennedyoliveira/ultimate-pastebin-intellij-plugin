package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.ultimatepastebin.service.PasteBinService;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Action to delete a selected paste.
 */
public class DeletePasteAction extends AbstractPasteSelectedAction {

  private static final Logger logger = UltimatePasteBinUtils.logger;
  private ToolWindowService toolWindowService;

  protected DeletePasteAction() {
    this.toolWindowService = ServiceManager.getService(ToolWindowService.class);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

    if (selectedPaste.isPresent()) {
      Paste paste = selectedPaste.get();

      int resp = Messages.showOkCancelDialog(e.getProject(),
                                             getMessage("ultimatepastebin.actions.deletepaste.confirmation.message",
                                                        paste.getTitle()),
                                             getMessage("ultimatepastebin.actions.deletepaste.confirmation.title"),
                                             getMessage("ultimatepastebin.ok"),
                                             getMessage("ultimatepastebin.cancel"),
                                             Messages.getWarningIcon());

      if (resp == Messages.OK) {
        new Task.Backgroundable(e.getProject(),
                                getMessage("ultimatepastebin.actions.deletepaste.task.title", paste.getTitle()),
                                false) {
          @Override
          public void run(@NotNull ProgressIndicator indicator) {
            logger.info("Deleting paste: " + paste);
            final PasteBinService pasteBinService = ServiceManager.getService(PasteBinService.class);

            try {
              pasteBinService.getPasteBin().deletePaste(paste);
              logger.info("Paste deleted successfully");

              Notifications.Bus.notify(new Notification("Paste deleted!",
                                                        "Ultimate PasteBin",
                                                        getMessage("ultimatepastebin.actions.deletepaste.ok.message",
                                                                   paste.getTitle()),
                                                        NotificationType.INFORMATION));

              ApplicationManager.getApplication().invokeLater(toolWindowService::fetchUserPastes);
            } catch (Exception e1) {
              logger.error("Failed to delete paste", e1);
              Notifications.Bus.notify(new Notification("Error deleting a paste",
                                                        "Ultimate PasteBin",
                                                        getMessage("ultimatepastebin.actions.deletepaste.error.message",
                                                                   paste.getTitle(),
                                                                   e1.getMessage()),
                                                        NotificationType.ERROR));
            }
          }
        }.queue();
      }
    }
  }

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setText(getMessage("ultimatepastebin.actions.deletepaste.text"));
    e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.deletepaste.description"));

    e.getPresentation().setEnabled(isDeletable());
  }

  /**
   * @return If the selected paste is deletable.
   */
  private boolean isDeletable() {
    return getSelectedPaste().filter(p -> !this.toolWindowService.isTrendPast(p))
                             .isPresent();
  }
}
