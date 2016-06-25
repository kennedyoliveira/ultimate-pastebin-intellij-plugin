package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinConstants;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Action to open the URL for donation.
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
