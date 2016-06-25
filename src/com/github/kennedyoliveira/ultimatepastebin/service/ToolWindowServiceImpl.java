package com.github.kennedyoliveira.ultimatepastebin.service;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.ultimatepastebin.settings.PasteBinConfigurationService;
import com.github.kennedyoliveira.ultimatepastebin.ui.*;
import com.github.kennedyoliveira.ultimatepastebin.utils.UltimatePasteBinUtils;
import com.intellij.ide.ui.customization.CustomizationUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.Optional;

import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;
import static com.intellij.util.ui.UIUtil.invokeLaterIfNeeded;
import static java.util.Comparator.comparing;

/**
 * Service for interacting with Ultimate Pastebin tool window
 */
public class ToolWindowServiceImpl implements ToolWindowService {

  private final static Logger log = UltimatePasteBinUtils.LOG;

  /**
   * Node representing the User Information
   */
  private DefaultMutableTreeNode userNode;

  /**
   * Node representing the trends paste
   */
  private DefaultMutableTreeNode trendsNode;

  /**
   * Tree used to display the pastes for the user.
   */
  private Tree tree;

  /**
   * Service for interacting with paste bin
   */
  private PasteBinService pasteBinService;

  /**
   * Service for interacting with the configuration
   */
  private PasteBinConfigurationService pasteBinConfigurationService;

  public ToolWindowServiceImpl() {
    pasteBinService = ServiceManager.getService(PasteBinService.class);
    pasteBinConfigurationService = ServiceManager.getService(PasteBinConfigurationService.class);

    DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    this.userNode = new DefaultMutableTreeNode(new UserNode(null));
    this.trendsNode = new DefaultMutableTreeNode(new TrendPasteNode());

    // This nodes always will be present
    root.add(userNode);
    root.add(trendsNode);

    // Initialize the tree
    this.tree = new Tree(new ListTreeTableModel(root, null));

    // Disable root node
    // and show the root children
    this.tree.setRootVisible(false);
    this.tree.setShowsRootHandles(true);

    // Enable speed search for tree of paste notes
    new TreeSpeedSearch(tree);

    // Tree render for putting icons
    this.tree.setCellRenderer(new PasteTreeRenderer());

    // Sets the right click context menu
    CustomizationUtil.installPopupHandler(tree, "ultimatepastebin.ToolwindowPopupMenu", ActionPlaces.TFS_TREE_POPUP);
  }

  @Override
  public Tree getTree() {
    return tree;
  }

  @Override
  public void fetchPastes() {
    new Task.Backgroundable(null, "Fetching Data from PasteBin", false) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        try {
          indicator.setText(getMessage("ultimatepastebin.settings.login.validation.title"));

          if (pasteBinService.isCredentialsValid()) {
            ((UserNode) userNode.getUserObject()).setUserInformation(pasteBinService.getPasteBin().getUserInformation().orElse(null));

            indicator.setIndeterminate(false);
            indicator.setText(getMessage("ultimatepastebin.tasks.fetching.trendspastes"));
            indicator.setFraction(0.01D);

            fetchTrendPastesAndUpdateUI();

            // update progress
            indicator.setFraction(0.5D);
            indicator.setText(getMessage("ultimatepastebin.tasks.fetching.userpaste"));

            fetchUserPastesAndUpdateUI();

            // update progress
            indicator.setFraction(1.0D);
            indicator.setIndeterminate(true);
            indicator.setText(getMessage("ultimatepastebin.finished"));
          }
        } catch (Exception e) {
          handleFetchingErrors(e);
        }
      }
    }.queue();
  }

  /**
   * Fetch user pastes and update the tree with new added pastes.
   */
  private void fetchUserPastesAndUpdateUI() {
    // Get the user pastes
    List<Paste> userPastes = pasteBinService.getPasteBin().listUserPastes(pasteBinConfigurationService.getTotalPastesToFetch());

    // removes all the previous pastes
    userNode.removeAllChildren();
    invokeLaterIfNeeded(tree::updateUI);

    // Create a paste node for each user paste and add to the userNode
    userPastes.stream().map(PasteNodeUtil::createNodeForPaste).forEach(userNode::add);
  }

  /**
   * Fetch Trend Pastes and update the tree with new added pastes.
   */
  private void fetchTrendPastesAndUpdateUI() {
    // Get the trending pastes
    List<Paste> pastes = pasteBinService.getPasteBin().listTrendingPastes();

    // removes all the previous pastes
    trendsNode.removeAllChildren();
    invokeLaterIfNeeded(tree::updateUI);

    // Create a paste node for each trending pastes and add to the trend node
    pastes.stream().sorted(comparing(Paste::getHits).reversed()).map(PasteNodeUtil::createNodeForPaste).forEach(trendsNode::add);
  }

  @Override
  public void fetchUserPastes() {
    new Task.Backgroundable(null, getMessage("ultimatepastebin.data.fetching"), false) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        try {
          indicator.setText(getMessage("ultimatepastebin.settings.login.validation.title"));

          if (pasteBinService.isCredentialsValid()) {
            ((UserNode) userNode.getUserObject()).setUserInformation(pasteBinService.getPasteBin().getUserInformation().orElse(null));

            indicator.setText(getMessage("ultimatepastebin.tasks.fetching.userpaste"));

            fetchUserPastes();
          }
        } catch (Exception e) {
          handleFetchingErrors(e);
        }
      }
    }.queue();
  }

  @Override
  public void fetchTrendingPastes() {
    new Task.Backgroundable(null, getMessage("ultimatepastebin.data.fetching"), false) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        try {
          indicator.setText(getMessage("ultimatepastebin.settings.login.validation.title"));

          if (pasteBinService.isCredentialsValid()) {
            ((UserNode) userNode.getUserObject()).setUserInformation(pasteBinService.getPasteBin().getUserInformation().orElse(null));

            indicator.setText(getMessage("ultimatepastebin.tasks.fetching.trendspastes"));

            fetchTrendingPastes();
          }
        } catch (Exception e) {
          handleFetchingErrors(e);
        }
      }
    }.queue();
  }

  /**
   * Handles errors ocurred while fetching pastes.
   *
   * @param e Exception ocurred.
   */
  private void handleFetchingErrors(Exception e) {
    log.info("Error while fetching pastes", e);
    ((UserNode) userNode.getUserObject()).setUserInformation(null);
    Notifications.Bus.notify(new Notification("Error fetching pastes",
                                              "Ultimate PasteBin",
                                              getMessage("ultiamtepastebin.data.fetching.error", e.getMessage() == null ? "Unknow error" : e.getMessage()),
                                              NotificationType.ERROR));
  }

  @Override
  public Optional<Paste> getSelectedPaste() {
    TreePath selectionPath = tree.getSelectionModel().getSelectionPath();

    if (selectionPath == null)
      return Optional.empty();

    Object selectedObject = selectionPath.getLastPathComponent();

    if (selectedObject != null && selectedObject instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedObject;

      if (selectedNode.getUserObject() instanceof PasteNode) {
        return Optional.of(((PasteNode) selectedNode.getUserObject()).getPaste());
      } else if (selectedNode.getUserObject() instanceof PasteInfoNode) {
        Object parentUserObject = ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();

        if (parentUserObject instanceof PasteNode) {
          return Optional.of(((PasteNode) parentUserObject).getPaste());
        }
      }
    }

    return Optional.empty();
  }
}
