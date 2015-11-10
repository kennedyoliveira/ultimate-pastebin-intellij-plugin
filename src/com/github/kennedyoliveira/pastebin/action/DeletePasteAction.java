package com.github.kennedyoliveira.pastebin.action;

import com.github.kennedyoliveira.pastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.pastebin.service.PasteBinService;
import com.github.kennedyoliveira.pastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/7/15.
 */
public class DeletePasteAction extends AbstractPasteSelectedAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

        Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

        if (selectedPaste.isPresent()) {
            Paste paste = selectedPaste.get();

            int resp = Messages.showOkCancelDialog(getMessage("ultimatepastebin.actions.deletepaste.confirmation.message", paste.getTitle()),
                                                   getMessage("ultimatepastebin.actions.deletepaste.confirmation.title"),
                                                   PlatformIcons.WARNING_INTRODUCTION_ICON);

            if (resp == Messages.OK) {
                new Task.Backgroundable(e.getProject(), getMessage("ultimatepastebin.actions.deletepaste.task.title", paste.getTitle()), false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        PasteBinService pasteBinService = ServiceManager.getService(PasteBinService.class);

                        if (pasteBinService.getPasteBin().deletePaste(paste)) {
                            Notifications.Bus.notify(new Notification("Paste deleted!",
                                                                      "Ultimate PasteBin",
                                                                      getMessage("ultimatepastebin.actions.deletepaste.ok.message", paste.getTitle()),
                                                                      NotificationType.INFORMATION));

                            ApplicationManager.getApplication().invokeLater(toolWindowService::fetchUserPastes);
                        } else {
                            Notifications.Bus.notify(new Notification("Error deleting a paste",
                                                                      "Ultimate PasteBin",
                                                                      getMessage("ultimatepastebin.actions.deletepaste.error.message", paste.getTitle()),
                                                                      NotificationType.ERROR));
                        }
                    }
                }.queue();
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.deletepaste.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.deletepaste.description"));
    }
}
