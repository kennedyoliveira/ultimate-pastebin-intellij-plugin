package com.github.kennedyoliveira.pastebin.ui;

import com.github.kennedyoliveira.pastebin4j.Paste;

import javax.swing.*;

import static com.github.kennedyoliveira.pastebin.i18n.MessageBundle.getMessage;

/**
 * <p>>Wraps a {@link com.github.kennedyoliveira.pastebin4j.Paste} to make easy identifying
 * paste nodes.</p
 *
 * @author kennedy
 */
public class PasteNode implements IconVisitable {

    private Paste paste;

    public PasteNode(Paste paste) {
        this.paste = paste;
    }

    public Paste getPaste() {
        return paste;
    }

    @Override
    public String toString() {
        return paste.getTitle() == null || paste.getTitle().isEmpty() ? getMessage("ultimatepastebin.paste.untitled") : paste.getTitle();
    }

    @Override
    public Icon visitNode(IconVisitor iconVisitor) {
        return iconVisitor.visit(this);
    }
}
