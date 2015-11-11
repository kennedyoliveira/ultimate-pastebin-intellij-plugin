package com.github.kennedyoliveira.ultimatepastebin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Icons for Ultimate PasteBin
 *
 * @author kennedy
 */
public class UltimatePasteBinIcons {

    /**
     * @deprecated Constants only class
     */
    private UltimatePasteBinIcons() {
    }

    public static final Icon USER_LOGGED_IN_ICON = IconLoader.getIcon("/icons/user_loggedin.png");

    public static final Icon USER_LOGGED_OFF_ICON = IconLoader.getIcon("/icons/user_loggedoff.png");

    public static final Icon NEW_PASTE_ICON = IconLoader.getIcon("/icons/new_paste.png");

    public static final Icon PUBLIC_PASTE_ICON = IconLoader.getIcon("/icons/public_note.png");

    public static final Icon PRIVATE_PASTE_ICON = IconLoader.getIcon("/icons/private_note.png");

    public static final Icon UNLISTED_PASTE_ICON = IconLoader.getIcon("/icons/unlisted_note.png");

    public static final Icon PASTE_INFO_ICON = IconLoader.getIcon("/icons/note_info.png");

    public static final Icon TRENDING_PASTES_ICON = IconLoader.getIcon("/icons/trending.png");

    public static final Icon WARNING_ICON = IconLoader.getIcon("/icons/warning.png");
}
