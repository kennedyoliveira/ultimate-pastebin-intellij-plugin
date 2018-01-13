package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;

import java.util.Optional;

/**
 * <p>This class is a base for actions that needs that a paste is selected to be marked as enabled</p>
 */
public abstract class AbstractPasteSelectedAction extends AnAction {

  @Override
  public void update(AnActionEvent e) {
    super.update(e);

    e.getPresentation().setEnabled(getSelectedPaste().isPresent());
  }

  protected Optional<Paste> getSelectedPaste() {
    final ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

    return toolWindowService.getSelectedPaste();
  }
}
