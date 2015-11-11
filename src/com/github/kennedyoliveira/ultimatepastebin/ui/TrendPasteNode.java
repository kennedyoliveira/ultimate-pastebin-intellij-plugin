package com.github.kennedyoliveira.ultimatepastebin.ui;

import javax.swing.*;

/**
 * Created by kennedy on 11/5/15.
 */
public class TrendPasteNode implements IconVisitable {

    @Override
    public String toString() {
        return "Trend Paste";
    }

    @Override
    public Icon visitNode(IconVisitor iconVisitor) {
        return iconVisitor.visit(this);
    }
}
