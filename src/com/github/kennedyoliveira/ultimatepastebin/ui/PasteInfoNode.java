package com.github.kennedyoliveira.ultimatepastebin.ui;

import javax.swing.*;

/**
 * <p>Wrapper for a {@link com.github.kennedyoliveira.pastebin4j.Paste} information.</p>
 *
 * @author kennedy
 */
public class PasteInfoNode implements IconVisitable {

    private String info;

    public PasteInfoNode(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return info;
    }

    @Override
    public Icon visitNode(IconVisitor iconVisitor) {
        return iconVisitor.visit(this);
    }
}
