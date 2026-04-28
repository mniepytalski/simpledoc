package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Note;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

class NoteTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if (node.getUserObject() instanceof Note) {
            Note note = (Note) node.getUserObject();
            if (node.getParent().toString().matches("^\\d{6}$")) {
                setText("<html><font color='gray'>" + note.getModificationDateHour() + "</font> " + note.getLabel() + "</html>");
            } else {
                setText("<html><font color='gray'>" + note.getModificationDate() + "</font> " + note.getLabel() + "</html>");
            }
        } else {
            try {
                CategoryElement.valueOf(node.toString());
            } catch (IllegalArgumentException e) {
                setForeground(Color.GRAY);
            }
        }
        return this;
    }
}
