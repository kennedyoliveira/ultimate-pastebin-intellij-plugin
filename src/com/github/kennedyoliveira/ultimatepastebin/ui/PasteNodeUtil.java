package com.github.kennedyoliveira.ultimatepastebin.ui;

import com.github.kennedyoliveira.pastebin4j.Paste;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.github.kennedyoliveira.pastebin4j.PasteExpiration.NEVER;
import static com.github.kennedyoliveira.ultimatepastebin.i18n.MessageBundle.getMessage;

/**
 * Created by kennedy on 11/5/15.
 */
public class PasteNodeUtil {

  private PasteNodeUtil() {}

  private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy kk:mm");

  /**
   * <p>Creates a {@link MutableTreeNode} representing a {@link Paste} and its infos, wrapping the data in {@link PasteNode} and {@link PasteInfoNode}</p>
   *
   * @param paste The {@link Paste} used to create the nodes.
   * @return a {@link MutableTreeNode} with the info.
   */
  public static MutableTreeNode createNodeForPaste(Paste paste) {
    Objects.requireNonNull(paste);

    final DefaultMutableTreeNode pasteNode = new DefaultMutableTreeNode(new PasteNode(paste));

    pasteNode.add(new DefaultMutableTreeNode(new PasteInfoNode(getMessage("ultimatepastebin.paste.key", paste.getKey()))));
    pasteNode.add(new DefaultMutableTreeNode(new PasteInfoNode(getMessage("ultimatepastebin.paste.url", paste.getUrl()))));
    pasteNode.add(new DefaultMutableTreeNode(new PasteInfoNode(getMessage("ultimatepastebin.paste.visibility", paste.getVisibility().name()))));
    pasteNode.add(new DefaultMutableTreeNode(new PasteInfoNode(getMessage("ultimatepastebin.paste.createddate", formatDateTime(paste.getLocalPasteDate())))));
    pasteNode.add(new DefaultMutableTreeNode(new PasteInfoNode(getMessage("ultimatepastebin.paste.expiration", paste.getExpiration().name()))));
    pasteNode.add(new DefaultMutableTreeNode(new PasteInfoNode(getMessage("ultimatepastebin.paste.expirationdate", paste.getExpiration() == NEVER ? getMessage("ultimatepastebin.paste.expiration.never") : formatDateTime(paste.getLocalExpirationDate())))));
    pasteNode.add(new DefaultMutableTreeNode(new PasteInfoNode(getMessage("ultimatepastebin.paste.views", paste.getHits()))));
    pasteNode.add(new DefaultMutableTreeNode(new PasteInfoNode(getMessage("ultimatepastebin.paste.highlight", paste.getHighLight().name()))));

    return pasteNode;
  }

  private static String formatDateTime(ZonedDateTime zonedDateTime) {
    return DEFAULT_DATETIME_FORMATTER.format(zonedDateTime);
  }
}
