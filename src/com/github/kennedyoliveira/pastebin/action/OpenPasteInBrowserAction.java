package com.github.kennedyoliveira.pastebin.action;

import com.github.kennedyoliveira.pastebin.service.ToolWindowService;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;

import java.util.Optional;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/9/15.
 */
public class OpenPasteInBrowserAction extends AbstractPasteSelectedAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ToolWindowService toolWindowService = ServiceManager.getService(ToolWindowService.class);

        Optional<Paste> selectedPaste = toolWindowService.getSelectedPaste();

        selectedPaste.map(Paste::getUrl).ifPresent(BrowserUtil::browse);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.openpasteinbrowser.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.openpasteinbrowser.description"));
    }
}
