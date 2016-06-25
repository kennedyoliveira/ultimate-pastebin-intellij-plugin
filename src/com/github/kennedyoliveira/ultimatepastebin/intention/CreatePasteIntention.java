package com.github.kennedyoliveira.ultimatepastebin.intention;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteHighLight;
import com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinIcons;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.ui.forms.CreatePasteForm;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Intention showed in the editor to create paste for a file or selected code.
 */
public class CreatePasteIntention implements IntentionAction, Iconable {

  @Nls
  @NotNull
  @Override
  public String getText() {
    return getMessage("ultimatepastebin.intentions.createpaste.text");
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return getMessage("ultimatepastebin.intentions.createpaste.family");
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

    Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

    return selectedPaste.isPresent();
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    Paste paste = new Paste();

    if (editor.getSelectionModel().getSelectedText() != null) {
      paste.setContent(editor.getSelectionModel().getSelectedText());
    } else {
      paste.setContent(editor.getDocument().getText());
    }

    FileType fileType = file.getFileType();
    Optional<PasteHighLight> highLight = UltimatePasteBinUtils.getHighlighFromVirtualFile(file.getVirtualFile());
    paste.setHighLight(highLight.orElse(PasteHighLight.TEXT));

    CreatePasteForm.createAndShowForm(paste, project, fileType);
  }

  @Override
  public boolean startInWriteAction() {
    return false;
  }

  @Override
  public Icon getIcon(@IconFlags int flags) {
    return UltimatePasteBinIcons.NEW_PASTE_ICON;
  }
}
