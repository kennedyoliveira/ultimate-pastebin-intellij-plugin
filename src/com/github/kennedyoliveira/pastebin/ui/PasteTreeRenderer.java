package com.github.kennedyoliveira.pastebin.ui;

import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.WrappingIconPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.awt.*;

/**
 * <p>Custom renderer for the Paste tree</p>
 *
 * @author kennedy
 */
public class PasteTreeRenderer extends DefaultTreeRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (component instanceof WrappingIconPanel && value instanceof MutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            // When is the root, i return, because there is no need
            // to do anything, the root isn't being shown anyway
            if (node.isRoot())
                return component;

            Object userObject = node.getUserObject();

            if (userObject instanceof IconVisitable) {
                Icon icon = ((IconVisitable) userObject).visitNode(new IconVisitor());
                ((WrappingIconPanel) component).setIcon(icon);
            }
        }

        return component;
    }
}
