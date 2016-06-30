package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.ultimatepastebin.ui.forms.CreatePasteForm;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.vfs.VirtualFile;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Action to create a new paste.
 */
public class CreatePasteAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    // Gets the selected text
    Editor editor = e.getData(DataKeys.EDITOR);

    // Default plain text
    FileType fileType = PlainTextFileType.INSTANCE;

    final Paste paste = new Paste();

    // If there are something selected
    if (editor != null && editor.getSelectionModel().getSelectedText() != null) {
      paste.setContent(editor.getSelectionModel().getSelectedText());
    } else {
      // Gets all the selected files
      VirtualFile[] selectedFiles = e.getData(DataKeys.VIRTUAL_FILE_ARRAY);

      // When there is multiple files selected, i do nothing
      if (selectedFiles != null && selectedFiles.length == 1 && !selectedFiles[0].isDirectory()) {
        fileType = selectedFiles[0].getFileType();

        // Try to get the Highlight based on the file, if not found use the Text
        paste.setHighLight(UltimatePasteBinUtils.getHighlighFromVirtualFile(selectedFiles[0]).orElse(PasteHighLight.TEXT));

        // Set the content of the selected file to paste
        UltimatePasteBinUtils.getFileContent(selectedFiles[0]).ifPresent(paste::setContent);
      }
    }

    CreatePasteForm.createAndShowForm(paste, e.getProject(), fileType);
  }

  @Override
  public void update(AnActionEvent e) {
    super.update(e);

    PasteBinConfigurationService configurationService = ServiceManager.getService(PasteBinConfigurationService.class);

    // Gets the selected text
    Editor editor = e.getData(DataKeys.EDITOR);

    // Gets all the selected files
    VirtualFile[] selectedFiles = e.getData(DataKeys.VIRTUAL_FILE_ARRAY);

    final boolean hasTextSelectedInEditor = editor != null && editor.getSelectionModel().getSelectedText() != null;
    final boolean hasFileSelectedInProjectWindow = selectedFiles != null && selectedFiles.length == 1 && !selectedFiles[0].isDirectory();

    final boolean shouldEnableCreatePasteAction = configurationService.isValidCredentials() && (hasTextSelectedInEditor || hasFileSelectedInProjectWindow);

    e.getPresentation().setEnabled(shouldEnableCreatePasteAction);

    e.getPresentation().setText(getMessage("ultimatepastebin.actions.createpaste.text"));
    e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.createpaste.description"));
  }
}
