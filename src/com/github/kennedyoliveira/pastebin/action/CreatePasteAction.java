package com.github.kennedyoliveira.pastebin.action;

import com.github.kennedyoliveira.pastebin.i18n.MessageBundle;
import com.github.kennedyoliveira.pastebin.service.PasteBinService;
import com.github.kennedyoliveira.pastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.pastebin.ui.forms.CreatePasteForm;
import com.github.kennedyoliveira.pastebin.utils.ClipboardUtils;
import com.github.kennedyoliveira.pastebin.utils.SyntaxHighlighUtils;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/6/15.
 */
public class CreatePasteAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Paste paste = new Paste();

        // Gets the selected text
        Editor editor = e.getData(DataKeys.EDITOR);

        // If there are something selected
        if (editor != null && editor.getSelectionModel().getSelectedText() != null) {
            paste.setContent(editor.getSelectionModel().getSelectedText());
        }

        // Gets all the selected files
        VirtualFile[] selectedFiles = e.getData(DataKeys.VIRTUAL_FILE_ARRAY);

        // When there is multiple files selected, i do nothing
        if (selectedFiles != null && selectedFiles.length == 1 && !selectedFiles[0].isDirectory()) {
            String extension = selectedFiles[0].getExtension();

            paste.setHighLight(SyntaxHighlighUtils.getHighlighByFileExtension(extension).orElse(PasteHighLight.TEXT));

            if (paste.getContent() == null) {
                try {
                    paste.setContent(new String(selectedFiles[0].contentsToByteArray(), selectedFiles[0].getCharset()));
                } catch (IOException e1) {
                }
            }
        }

        AsyncResult<Boolean> booleanAsyncResult = new CreatePasteForm(e.getProject(), paste).showAndGetOk();

        booleanAsyncResult.doWhenDone((Consumer<Boolean>) result -> {
            if (result) {
                new Task.Backgroundable(e.getProject(), getMessage("ultimatepastebin.actions.createpaste.task.title"), false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            PasteBinService service = ServiceManager.getService(PasteBinService.class);
                            String url = service.getPasteBin().createPaste(paste);

                            String message = getMessage("ultimatepastebin.actions.createpaste.ok.notification.message", url);

                            ClipboardUtils.copyToClipboard(url);

                            Notifications.Bus.notify(new Notification("Paste created",
                                                                      "Ultimate PasteBin",
                                                                      message,
                                                                      NotificationType.INFORMATION,
                                                                      NotificationListener.URL_OPENING_LISTENER), e.getProject());

                            // Updates the pastes...
                            ApplicationManager.getApplication().invokeLater(() -> {
                                ServiceManager.getService(ToolWindowService.class).fetchUserPastes();
                            });
                        } catch (Exception e1) {
                            Notifications.Bus.notify(new Notification("Error creating a paste",
                                                                      "Ultimate PasteBin",
                                                                      getMessage("ultimatepastebin.actions.createpaste.error.notification.message", e1.getMessage()),
                                                                      NotificationType.ERROR));
                        }
                    }
                }.queue();
            }
        });
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        PasteBinConfigurationService configurationService = ServiceManager.getService(PasteBinConfigurationService.class);

        e.getPresentation().setEnabled(configurationService.isValidCredentials());

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.createpaste.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.createpaste.description"));
    }
}
