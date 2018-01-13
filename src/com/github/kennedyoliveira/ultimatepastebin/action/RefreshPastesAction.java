package com.github.kennedyoliveira.ultimatepastebin.action;

import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Action the fetch all the pastes and updates the UI
 */
public class RefreshPastesAction extends AnAction {

  private static final Logger logger = UltimatePasteBinUtils.logger;

  @Override
  public void actionPerformed(AnActionEvent e) {
    logger.info("Refreshing pastes from RefreshPasteAction");
    final ToolWindowService service = ServiceManager.getService(ToolWindowService.class);

    service.fetchPastes();
  }

  @Override
  public void update(AnActionEvent e) {
    super.update(e);

    e.getPresentation().setText(getMessage("ultimatepastebin.actions.refreshpastes.text"));
    e.getPresentation().setDescription(getMessage("ultimatepastebin.actions.refreshpastes.description"));
  }
}
