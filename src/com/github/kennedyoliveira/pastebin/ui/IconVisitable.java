package com.github.kennedyoliveira.pastebin.ui;

import javax.swing.*;

/**
 * <p>Interface to be visitable by a {@link IconVisitor}</p>
 *
 * @author kennedy
 */
public interface IconVisitable {

    Icon visitNode(IconVisitor iconVisitor);
}
