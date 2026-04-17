package pl.cbr.notebook.ui.swing;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class NotesTreeExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Lista notatek (drzewo)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        // Root (korzeń)
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Notatki");

        // Kategorie
        DefaultMutableTreeNode praca = new DefaultMutableTreeNode("TODO");
        DefaultMutableTreeNode szkola = new DefaultMutableTreeNode("FIX");
        DefaultMutableTreeNode bug = new DefaultMutableTreeNode("BUG");
        DefaultMutableTreeNode task = new DefaultMutableTreeNode("TASK");

        // Notatki
        praca.add(new DefaultMutableTreeNode("Spotkanie projektowe"));
        praca.add(new DefaultMutableTreeNode("TODO backend"));

        szkola.add(new DefaultMutableTreeNode("Matematyka"));
        szkola.add(new DefaultMutableTreeNode("Fizyka"));

        // Dodanie do root
        root.add(praca);
        root.add(szkola);
        root.add(bug);
        root.add(task);

        // JTree
        JTree tree = new JTree(root);
        tree.setRootVisible(false);
//        tree.setDragEnabled(true);
//        tree.setDropMode(DropMode.ON_OR_INSERT);
//        tree.setTransferHandler(new TreeTransferHandler());

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        // Scroll (ważne!)
        JScrollPane scrollPane = new JScrollPane(tree);

        frame.add(scrollPane);
        frame.setVisible(true);
    }
}
