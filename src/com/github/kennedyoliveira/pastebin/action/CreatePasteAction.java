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
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;
import static com.github.kennedyoliveira.pastebin.utils.SyntaxHighlighUtils.getHighlighByFileExtension;

/**
 * Created by kennedy on 11/6/15.
 */
public class CreatePasteAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Gets the selected text
        Editor editor = e.getData(DataKeys.EDITOR);

        final Paste paste = new Paste();

        // If there are something selected
        if (editor != null && editor.getSelectionModel().getSelectedText() != null) {
            paste.setContent(editor.getSelectionModel().getSelectedText());
        }

        // Gets all the selected files
        VirtualFile[] selectedFiles = e.getData(DataKeys.VIRTUAL_FILE_ARRAY);

        // Default plain text
        FileType fileType = PlainTextFileType.INSTANCE;

        // When there is multiple files selected, i do nothing
        if (selectedFiles != null && selectedFiles.length == 1 && !selectedFiles[0].isDirectory()) {
            String extension = selectedFiles[0].getExtension();
            fileType = selectedFiles[0].getFileType();
            String defaultFileExtension = fileType.getDefaultExtension();

            // Try to get by the default file extension, if not found, try to get by the file extension, if not found too,
            // then go with text
            paste.setHighLight(getHighlighByFileExtension(defaultFileExtension).orElse(getHighlighByFileExtension(extension).orElse(PasteHighLight.TEXT)));

            // If there's no content selected, then i set the content of the selected file
            if (paste.getContent() == null) {
                try {
                    paste.setContent(new String(selectedFiles[0].contentsToByteArray(), selectedFiles[0].getCharset()));
                } catch (IOException ignored) {
                }
            }
        }

        CreatePasteForm.createAndShowForm(paste, e.getProject(), fileType);
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
