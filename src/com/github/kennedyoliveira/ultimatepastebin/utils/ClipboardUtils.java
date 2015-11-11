package com.github.kennedyoliveira.ultimatepastebin.utils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * Created by kennedy on 11/7/15.
 */
public class ClipboardUtils {

    private ClipboardUtils() {
    }

    public static void copyToClipboard(String content) {
        try {
            // Copy the link to the clipboard
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content), null);
        } catch (HeadlessException ignored) {
        }
    }
}
