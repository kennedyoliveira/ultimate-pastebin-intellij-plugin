package com.github.kennedyoliveira.ultimatepastebin.ui;

import com.github.kennedyoliveira.ultimatepastebin.service.ToolWindowService;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author kennedy
 */
public class UltimatePasteBinToolWindow implements ToolWindowFactory {

  private static final Logger logger = UltimatePasteBinUtils.logger;

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    logger.info("Initializing Pastes ToolWindow");
    toolWindow.setStripeTitle("Ultimate PasteBin");
    toolWindow.setTitle("Ultimate PasteBin");

    ContentManager contentManager = toolWindow.getContentManager();

    Content content = contentManager.getFactory().createContent(toolWindow.getComponent(), null, false);

    ToolWindowService service = ServiceManager.getService(ToolWindowService.class);

    // Panel with toolbar
    SimpleToolWindowPanel simpleToolWindowPanel = new SimpleToolWindowPanel(true);

    // Scrolable panel
    JBScrollPane jbScrollPane = new JBScrollPane(service.getTree());
    simpleToolWindowPanel.add(jbScrollPane);
    simpleToolWindowPanel.setToolbar(createToolbar());

    content.setComponent(simpleToolWindowPanel);

    contentManager.addContent(content);
  }

  public JComponent createToolbar() {
    ActionGroup actionGroup = (ActionGroup) ActionManager.getInstance().getAction("ultimatepastebin.ToolwindowToolbar");
    return ActionManager.getInstance().createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, actionGroup, true).getComponent();
  }
}
