package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.utils.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class ListOfTitlesUI {

    private final Frame frame;
    private final NotebookUIModel model;

    public ListOfTitlesUI(Frame frame, NotebookUIModel model) {
        this.frame = frame;
        this.model = model;
    }

    public JList<Note> create() {
        DefaultListModel<Note> titleModel = new DefaultListModel<>();
        Notebook notebook = model.getNotebook();
        titleModel.addAll(notebook.getNotes());
        JList<Note> titleList = new JList<>(titleModel);
        titleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        titleList.setCellRenderer(new ListCellRenderer<>() {
            private final JPanel panel = new JPanel(new BorderLayout());
            private final JLabel nameLabel = new JLabel();
            private final JLabel roleLabel = new JLabel();
            {
                panel.setLayout(new GridLayout(2, 1));
                panel.add(nameLabel);
                panel.add(roleLabel);
                panel.setOpaque(true); // Important for background coloring
            }

            @Override
            public Component getListCellRendererComponent(
                    JList<? extends Note> list, Note value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                nameLabel.setText(value.getTitle());
                roleLabel.setText(DateUtil.formatDate(value.getFile().getModificationDate()));
                nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
                roleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                if (isSelected) {
                    setListElementColors(list.getSelectionBackground(), list.getSelectionForeground());
                } else {
                    setListElementColors(list.getBackground(), list.getForeground());
                }
                return panel;
            }

            private void setListElementColors(Color selectionBackground, Color selectionForeground) {
                panel.setBackground(selectionBackground);
                panel.setForeground(selectionForeground);
                nameLabel.setForeground(selectionForeground);
                roleLabel.setForeground(selectionForeground);
            }
        });

        titleList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                getSelectedNote(me).ifPresent(note -> {
                    if (MouseEvent.BUTTON3 == me.getButton()) {
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem menuItemInfo = new JMenuItem("Informacje");
                        popup.add(menuItemInfo);
                        popup.show(me.getComponent(), me.getX(), me.getY());
                        menuItemInfo.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                                "Title: " + note.getTitle() + "\nFile name: " + note.getFileName(),
                                note.getTitle() ,
                                JOptionPane.INFORMATION_MESSAGE));
                    }
                    if (me.getClickCount() == 1) {
                        model.setNoteFromTextId(note.getTextId());
                    }
                });
            }

            private Optional<Note> getSelectedNote(MouseEvent me) {
                JList<Note> target = (JList<Note>) me.getSource();
                int index = target.locationToIndex(me.getPoint());
                if (index >= 0) {
                    return Optional.of(model.getTitleModel().get(index));
                } else  {
                    return Optional.empty();
                }
            }
        });

        model.setTitleModel(titleModel);
        return titleList;
    }
}
