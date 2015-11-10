package com.github.kennedyoliveira.pastebin.action;

import com.github.kennedyoliveira.pastebin.service.ToolWindowService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/6/15.
 */
public class RefreshPastesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ToolWindowService service = ServiceManager.getService(ToolWindowService.class);

        service.fetchPastes();
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.refreshpastes.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.refreshpastes.description"));
    }
}
