package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;

import java.util.Optional;

/**
 * <p>This class is a base for actions that needs that a paste is selected to be marked as enabled</p>
 *
 * @author kennedy
 */
public abstract class AbstractPasteSelectedAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

        Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

        e.getPresentation().setEnabled(selectedPaste.isPresent());
    }
}
