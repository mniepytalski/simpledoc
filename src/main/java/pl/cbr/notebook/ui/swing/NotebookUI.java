package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

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
            frame.setVisible(true);

            addShortcuts(saveButton, newButton, deleteButton);

            model.cleanNoteWindow();
        });
    }

    private static void addShortcuts(JButton saveButton, JButton newButton, JButton deleteButton) {
        defineShortCut("saveAction", KeyEvent.VK_S, saveButton);
        defineShortCut("newAction", KeyEvent.VK_N, newButton);
        defineShortCut("deleteAction", KeyEvent.VK_D, deleteButton);
    }

    private static void defineShortCut(String actionName, int keyEvent, JButton button) {
        int menuShortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        InputMap inputMap = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = button.getActionMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, menuShortcutKey);
        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.doClick();
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
                roleLabel.setText("File: " + value.getFileName());

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
