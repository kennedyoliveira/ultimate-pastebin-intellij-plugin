package com.github.kennedyoliveira.ultimatepastebin.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * Utility method to interact with Clipboard.
 */
public class ClipboardUtils {

  private final static Logger log = UltimatePasteBinUtils.LOG;

  private ClipboardUtils() {
  }

  /**
   * <p>Copy the {@code content} to clipboard.</p>
   * <p>If the content is null, doesn't do nothing.</p>
   *
   * @param content Content to be copied
   */
  public static void copyToClipboard(String content) {
    if (StringUtil.isNotEmpty(content)) {
      try {
        // Copy the link to the clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content), null);
      } catch (HeadlessException e) {
        log.info("Error while coping content to clipboard", e);
      }
    }
  }
}
