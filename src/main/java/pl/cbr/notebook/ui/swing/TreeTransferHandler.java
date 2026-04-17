package pl.cbr.notebook.ui.swing;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;

public class TreeTransferHandler extends TransferHandler {

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

        return new StringSelection(node.toString());
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDrop();
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) return false;

        JTree tree = (JTree) support.getComponent();
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();

        TreePath dest = dl.getPath();
        DefaultMutableTreeNode parent =
                (DefaultMutableTreeNode) dest.getLastPathComponent();

        try {
            String data = (String) support.getTransferable()
                    .getTransferData(DataFlavor.stringFlavor);

            DefaultMutableTreeNode newNode =
                    new DefaultMutableTreeNode(data);

            parent.add(newNode);

            ((DefaultTreeModel) tree.getModel()).reload(parent);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
