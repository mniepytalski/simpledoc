package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Diary;
import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.utils.DateUtil;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class NotebookUI {
    private final NotebookUIModel model;

    private static final int APP_WIDTH = 700;
    private static final int APP_HEIGHT = 500;

    private final NoteLog log;

    private NotebookUI() {
        model = new NotebookUIModel();
        log = new NoteLog();
    }

    public static void start() {
        NotebookUI notebookUI = new NotebookUI();
        notebookUI.createUi();
    }

    private void createUi() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Notebook v1.8");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(APP_WIDTH, APP_HEIGHT);

            JPanel leftPanel = new JPanel(new BorderLayout());
            JList<Note> listOfTitles = getListOfTitles();
            JScrollPane leftScrollPane = new JScrollPane(listOfTitles);
            leftPanel.add(leftScrollPane,  BorderLayout.CENTER);

            JPanel rightPanel = new JPanel(new BorderLayout());

            JPanel topLeftPanel = new JPanel();
            topLeftPanel.setLayout(new BoxLayout(topLeftPanel, BoxLayout.X_AXIS));
            JButton newButton = new JButton("New");
            newButton.addActionListener(e -> model.newNote());
            topLeftPanel.add(newButton);

            JTextField searchField = new JTextField();
            model.setSearchField(searchField);
            searchField.addActionListener(e -> {
                log.info("search: {}", e.getActionCommand());
                List<Note> notes = model.searchNote(e.getActionCommand());
                notes.forEach(note -> log.info("filename: {}, title: {}", note.getFileName(), note.getTitle()));
            });
            topLeftPanel.add(searchField);
            rightPanel.add(topLeftPanel, BorderLayout.NORTH);

            JTextPane textArea = new JTextPane();
            model.setTextArea(textArea);

            Diary diary = new Diary();
            textArea.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                   diary.event(e.getKeyChar(),textArea.getCaretPosition());
//                    System.out.println("key:" + e.getKeyChar() + "," + textArea.getCaretPosition());
//                    if (textArea.getCaretPosition() == 10 ) {
//                        textArea.getStyledDocument().setCharacterAttributes(textArea.getCaretPosition() - 2 , 2, Keyword.TAG.getStyle(), true);
//                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
            textArea.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    int start = textArea.getSelectionStart();
                    int end = textArea.getSelectionEnd();
                    String selected = textArea.getSelectedText();

                    if (selected != null && !selected.isEmpty()) {
                        System.out.println("Zaznaczono od " + start + " do " + end +
                                " → \"" + selected + "\"");
                    }
                }
            });
            JScrollPane scrollPane = new JScrollPane(textArea);
            rightPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> model.saveNote());
            JButton deleteButton = new JButton("Delete");

            buttonPanel.add(deleteButton);
            buttonPanel.add(Box.createHorizontalGlue()); // elastyczna przestrzeń
            buttonPanel.add(saveButton);
            deleteButton.addActionListener(e -> model.deleteNote());

            rightPanel.add(buttonPanel, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    leftPanel,
                    rightPanel
            );
            splitPane.setDividerLocation(150);
            splitPane.setResizeWeight(0.0);

            frame.add(splitPane, BorderLayout.CENTER);

//            NotebookUIMenu.createMenu(frame);

            frame.setVisible(true);
            addShortcuts();
            model.cleanNoteWindow();
        });
    }

    private void addShortcuts() {
        defineShortCut(KeyEvent.VK_S, NotebookUIModel::saveNote);
        defineShortCut(KeyEvent.VK_N, NotebookUIModel::newNote);
        defineShortCut(KeyEvent.VK_D, NotebookUIModel::deleteNote);
    }

    private void defineShortCut(int keyEvent, Consumer<NotebookUIModel> action) {
        int menuShortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        InputMap inputMap = model.getTextArea().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = model.getTextArea().getActionMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, menuShortcutKey);
        String actionName = "" + keyStroke;
        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.accept(model);
            }
        });
    }

    private JList<Note> getListOfTitles() {
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

                roleLabel.setText(DateUtil.formatDate(value.getFile().getModificationDate()) + " " + value.getFileName());

                nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
                roleLabel.setFont(new Font("Arial", Font.PLAIN, 11));

                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                    panel.setForeground(list.getSelectionForeground());
                    nameLabel.setForeground(list.getSelectionForeground());
                    roleLabel.setForeground(list.getSelectionForeground());
                } else {
                    panel.setBackground(list.getBackground());
                    panel.setForeground(list.getForeground());
                    nameLabel.setForeground(list.getForeground());
                    roleLabel.setForeground(Color.DARK_GRAY);
                }
                return panel;
            }
        });

        titleList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 1) {
                    JList<Note> target = (JList<Note>)me.getSource();
                    int index = target.locationToIndex(me.getPoint());
                    if (index >= 0) {
                        Note note = model.getTitleModel().get(index);
                        model.setNoteFromTextId(note.getTextId());
                    }
                }
            }
        });
        model.setTitleModel(titleModel);
        return titleList;
    }
}
