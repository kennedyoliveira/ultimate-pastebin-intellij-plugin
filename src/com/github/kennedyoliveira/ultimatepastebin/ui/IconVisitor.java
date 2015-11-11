package com.github.kennedyoliveira.ultimatepastebin.ui;

import com.github.kennedyoliveira.ultimatepastebin.UltimatePasteBinIcons;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteVisibility;

import javax.swing.*;

/**
 * <p>Visitor that visit {@link IconVisitable} to return a {@link Icon}</p>
 *
 * @author kennedy
 */
public class IconVisitor {

    public Icon visit(UserNode userNode) {
        if (userNode.getUserInformation() != null) {
            return UltimatePasteBinIcons.USER_LOGGED_IN_ICON;
        } else {
            return UltimatePasteBinIcons.USER_LOGGED_OFF_ICON;
        }
    }

    public Icon visit(PasteNode pasteNode) {
        Paste paste = pasteNode.getPaste();

        // Custom icons representing the visibility
        if (paste.getVisibility() == PasteVisibility.PUBLIC) {
            return UltimatePasteBinIcons.PUBLIC_PASTE_ICON;
        } else if (paste.getVisibility() == PasteVisibility.UNLISTED) {
            return UltimatePasteBinIcons.UNLISTED_PASTE_ICON;
        } else {
            return UltimatePasteBinIcons.PRIVATE_PASTE_ICON;
        }
    }

    public Icon visit(PasteInfoNode pasteInfoNode) {
        return UltimatePasteBinIcons.PASTE_INFO_ICON;
    }

    public Icon visit(TrendPasteNode trendPasteNode) {
        return UltimatePasteBinIcons.TRENDING_PASTES_ICON;
    }
}
