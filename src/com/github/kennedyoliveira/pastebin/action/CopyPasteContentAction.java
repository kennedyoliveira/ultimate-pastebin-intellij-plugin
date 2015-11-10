package com.github.kennedyoliveira.pastebin.action;

import com.github.kennedyoliveira.pastebin.service.PasteBinService;
import com.github.kennedyoliveira.pastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin.utils.ClipboardUtils;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteVisibility;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/7/15.
 */
public class CopyPasteContentAction extends AbstractPasteSelectedAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ToolWindowService service = ServiceManager.getService(ToolWindowService.class);

        service.getSelectedPaste().ifPresent(this::downloadAndCopyToClipBoard);
    }

    private void downloadAndCopyToClipBoard(Paste paste) {
        if (paste.getVisibility() == PasteVisibility.PRIVATE) {
            Notifications.Bus.notify(new Notification("Can't fetch private paste contents",
                                                      "Ultimate PasteBin",
                                                      getMessage("ultimatepastebin.actions.copypastecontent.error.notification.message"),
                                                      NotificationType.ERROR));
            return;
        }

        new Task.Backgroundable(null, getMessage("ultimatepastebin.paste.content.fetching"), false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    PasteBinService service = ServiceManager.getService(PasteBinService.class);

                    String pasteContent = service.getPasteBin().getPasteContent(paste);

                    ClipboardUtils.copyToClipboard(pasteContent);

                    Notifications.Bus.notify(new Notification("Paste contents copied to clipboard",
                                                              "Ultimate PasteBin",
                                                              getMessage("ultimatepastebin.actions.copypasteclipboard.ok.notification.message"),
                                                              NotificationType.INFORMATION));
                } catch (Exception e) {
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
