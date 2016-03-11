package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.ultimatepastebin.ui.forms.CreatePasteForm;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;
import static com.github.kennedyoliveira.ultimatepastebin.utils.SyntaxHighlighUtils.getHighlighByFileExtension;

/**
 * Created by kennedy on 11/6/15.
 */
public class CreatePasteAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Gets the selected text
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        final Paste paste = new Paste();

        // If there are something selected
        if (editor != null && editor.getSelectionModel().getSelectedText() != null) {
            paste.setContent(editor.getSelectionModel().getSelectedText());
        }

        // Gets all the selected files
        VirtualFile[] selectedFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

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
