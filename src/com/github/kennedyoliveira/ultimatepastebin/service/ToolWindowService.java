package com.github.kennedyoliveira.ultimatepastebin.service;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.intellij.ui.treeStructure.Tree;

import java.util.Optional;

public interface ToolWindowService {

  /**
   * @return The tree with the pastes and user information
   */
  Tree getTree();

  /**
   * Initiate the actions for fetching user and trending pastes and update the toolwindow with results
   */
  void fetchPastes();

  /**
   * Initiate the action for fetching user pastes and update the toolwindow with results
   */
  void fetchUserPastes();

  /**
   * Initiate the action for fetching trend pastes and update the toolwindow with results
   */
  void fetchTrendPastes();

  /**
   * @return A selected paste in the {@link #getTree()}
   */
  Optional<Paste> getSelectedPaste();

  /**
   * Checks of the {@code paste} is a Trend paste.
   *
   * @param paste Paste to be checked.
   * @return {@code true} if trend, {@code false} otherwise.
   */
  boolean isTrendPast(Paste paste);
}
