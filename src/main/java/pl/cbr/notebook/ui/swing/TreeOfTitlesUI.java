package pl.cbr.notebook.ui.swing;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.cbr.notebook.Note;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class TreeOfTitlesUI implements ListOfNotes {

    private final Frame frame;
    private final NotebookUIModel model;
    private final JScrollPane scrollPane;
    private final JTree tree;
    private final List<Note> elements;

    public TreeOfTitlesUI(Frame frame, NotebookUIModel model) {
        this.frame = frame;
        this.model = model;
        this.elements = model.getNotebook().getNotes();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Notatki");
        tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setCellRenderer(new NoteTreeRenderer());
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path == null) return;
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Object obj = node.getUserObject();
                    if (obj instanceof Note) {
                        clickMouseLeftButton((Note)obj);
                    }
                }
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path == null) return;
                    tree.setSelectionPath(path);
                    Object lastPathComponent = path.getLastPathComponent();
                    if (lastPathComponent instanceof DefaultMutableTreeNode) {
                        Object userObject = ((DefaultMutableTreeNode) lastPathComponent).getUserObject();
                        if (userObject instanceof Note) {
                            clickMouseRightButton((Note) userObject);
                            JPopupMenu menu = createPopupMenu((Note) userObject);
                            menu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        });

        reCreate(elements);

        scrollPane = new JScrollPane(tree);
    }

    @Override
    public void remove(Note note) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode foundNode = findNode(getRoot(), note.toString());
        if (foundNode != null && foundNode.getParent() != null && foundNode.getUserObject() instanceof Note) {
            log.info("Node to remove: {}", foundNode);
            model.removeNodeFromParent(foundNode);
        }
    }

    private DefaultMutableTreeNode getRoot() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        return (DefaultMutableTreeNode) model.getRoot();
    }

    public void clickMouseLeftButton(Note note) {
        model.setNoteFromTextId(note.getTextId());
    }

    public void clickMouseRightButton(Note note) {
        model.setNoteFromTextId(note.getTextId());
    }

    @Override
    public int setSelectedIndex(Note note) {
        log.info("setSelectedIndex: {}", note.getTextId());
        DefaultMutableTreeNode node = findNode(getRoot(), note.toString());
        if (node == null) return -1;
        if (node.getUserObject() instanceof Note) {
            log.info("Node to set: {}", node.getUserObject());
            TreePath path = new TreePath(node.getPath());
            tree.setSelectionPath(path);
            return 1;
        }
        return -1;
    }

    @Override
    public void reCreate(List<Note> notes) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();

        List<Note> sortedNotes = notes.stream()
                .sorted(Comparator.comparing(Note::getModificationDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
        addTreeStructure(sortedNotes, root);
        model.reload(root);
        expandAllElements();
    }

    @Override
    public void saveNote(Note note, Note actualNote) {

    }

    private void addTreeStructure(List<Note> elements, DefaultMutableTreeNode root) {
        List<CategoryElement> categories = Arrays.asList(CategoryElement.values());
        List<DefaultMutableTreeNode> treeNodes = categories.stream().map(CategoryElement::name)
                .map(DefaultMutableTreeNode::new).collect(Collectors.toList());

        elements.forEach(element -> treeNodes.stream()
                .filter(e -> e.toString().equals(element.getCategory()))
                .findFirst()
                .ifPresentOrElse(node -> addNode(node, element),
                        () -> treeNodes.add(new DefaultMutableTreeNode(element))));
        treeNodes.forEach(root::add);
    }

    private void addNode(DefaultMutableTreeNode node, Note element) {
        String modificationDay = element.getModificationDay();
        for (int i=0; i<node.getChildCount(); i++) {
            DefaultMutableTreeNode childAt = (DefaultMutableTreeNode)node.getChildAt(i);
            if (childAt.getUserObject() instanceof Note) {
                if (((Note) childAt.getUserObject()).getModificationDay().equals(modificationDay)) {
                    node.remove(childAt);
                    DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(modificationDay);
                    defaultMutableTreeNode.add(new DefaultMutableTreeNode(childAt.getUserObject()));
                    defaultMutableTreeNode.add(new DefaultMutableTreeNode(element));
                    node.add(defaultMutableTreeNode);
                    return;
                }
            }
            if (childAt.toString().equals(modificationDay)) {
                childAt.add(new DefaultMutableTreeNode(element));
                return;
            }
        }
        node.add(new DefaultMutableTreeNode(element));
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode node, String value) {
        if (value.equals(node.getUserObject().toString())) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode result = findNode(child, value);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private void expandAllElements() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
        });
    }

    private JPopupMenu createPopupMenu(Note note) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItemInfo = new JMenuItem("Informacje");
        popup.add(menuItemInfo);
        menuItemInfo.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                    "Title: " + note.getTitle() + "\nFile name: " + note.getFileName(),
                    note.getTitle() ,
                    JOptionPane.INFORMATION_MESSAGE));
        return popup;
    }
}
