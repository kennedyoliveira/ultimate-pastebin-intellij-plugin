package com.github.kennedyoliveira.pastebin.ui;

import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteVisibility;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * <p>Visitor that visit {@link IconVisitable} to return a {@link Icon}</p>
 *
 * @author kennedy
 */
public class IconVisitor {

    public Icon visit(UserNode userNode) {
        if (userNode.getUserInformation() != null) {
            return IconLoader.getIcon("/icons/user_loggedin.png");
        } else {
            return IconLoader.getIcon("/icons/user_loggedoff.png");
        }
    }

    public Icon visit(PasteNode pasteNode) {
        Paste paste = pasteNode.getPaste();

        // Custom icons representing the visibility
        if (paste.getVisibility() == PasteVisibility.PUBLIC) {
            return IconLoader.getIcon("/icons/public_note.png");
        } else if (paste.getVisibility() == PasteVisibility.UNLISTED) {
            return IconLoader.getIcon("/icons/unlisted_note.png");
        } else {
            return IconLoader.getIcon("/icons/private_note.png");
        }
    }

    public Icon visit(PasteInfoNode pasteInfoNode) {
        return IconLoader.getIcon("/icons/note_info.png");
    }

    public Icon visit(TrendPasteNode trendPasteNode) {
        return IconLoader.getIcon("/icons/trending.png");
    }
}
