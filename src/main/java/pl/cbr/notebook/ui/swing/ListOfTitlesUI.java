package pl.cbr.notebook.ui.swing;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.utils.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ListOfTitlesUI implements ListOfNotes {

    private final Frame frame;
    private final NotebookUIModel model;
    private JList<Note> elements;
    private DefaultListModel<Note> titleModel;
    @Getter
    private JScrollPane scrollPane;

    public ListOfTitlesUI(Frame frame, NotebookUIModel model) {
        this.frame = frame;
        this.model = model;
        scrollPane = create();
    }

    public JScrollPane create() {
        titleModel = new DefaultListModel<>();
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
                        createMenu(note, me);
                        clickMouseRightButton(note);
                    }
                    if (me.getClickCount() == 1) {
                        clickMouseLeftButton(note);
                    }
                });
            }

            private Optional<Note> getSelectedNote(MouseEvent me) {
                JList<Note> target = (JList<Note>) me.getSource();
                int index = target.locationToIndex(me.getPoint());
                if (index >= 0) {
                    return Optional.of(titleModel.get(index));
                } else  {
                    return Optional.empty();
                }
            }
        });

        elements = titleList;
        return new JScrollPane(titleList);
    }

    @Override
    public void clickMouseLeftButton(Note note) {
        log.info("clickMouseLeftButton");
        model.setNoteFromTextId(note.getTextId());
    }

    @Override
    public void clickMouseRightButton(Note note) {
        log.info("clickMouseRightButton");
    }

    @Override
    public void remove(Note note) {
        for (int i = 0; i < titleModel.size(); i++) {
            Note s = titleModel.get(i);
            if (s.getTextId().equals(note.getTextId())) {
                titleModel.removeElementAt(i);
                break;
            }
        }
    }

    @Override
    public int setSelectedIndex(Note note) {
        for (int i = 0; i < elements.getModel().getSize(); i++ ) {
            if (note.getTextId().equals(elements.getModel().getElementAt(i).getTextId()) ) {
                elements.setSelectedIndex(i);
                return i;
            }
        }
        return -1;
    }

    @Override
    public void reCreate(List<Note> notes) {
        titleModel.removeAllElements();
        notes.forEach(note -> titleModel.addElement(note));
    }

    @Override
    public void saveNote(Note note, Note actualNote) {
        if (note.isNewNote()) {
            titleModel.addElement(note);
        } else {
            for (int i = 0; i < titleModel.size(); i++) {
                if (actualNote.getTextId().equals(titleModel.get(i).getTextId())) {
                    actualNote.setData(titleModel.get(i).getData());
                    titleModel.set(i, actualNote);
                }
            }
        }
    }

    private void createMenu(Note note, MouseEvent me) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItemInfo = new JMenuItem("Informacje");
        popup.add(menuItemInfo);
        popup.show(me.getComponent(), me.getX(), me.getY());
        menuItemInfo.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Title: " + note.getTitle() + "\nFile name: " + note.getFileName(),
                note.getTitle() ,
                JOptionPane.INFORMATION_MESSAGE));
    }
}
