package com.github.kennedyoliveira.pastebin.action;

import com.github.kennedyoliveira.pastebin.UltimatePasteBinConstants;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * @author kennedy
 */
public class DonateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        BrowserUtil.browse(UltimatePasteBinConstants.DONATION_URL);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        e.getPresentation().setText(getMessage("ultimatepastebin.actions.donate.text"));
        e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.donate.description"));
    }
}
