package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Config;
import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.utils.DateUtil;
import pl.cbr.notebook.utils.SelectedTextUtil;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class NotebookUI {
    private final NotebookUIModel model;

    private static final int APP_WIDTH = 700;
    private static final int APP_HEIGHT = 500;

    private static boolean panelVisible =  true;
    private static int rightPanelWidth = 0;
    private static int rightPanelHeight = 0;
    private static int frameWidth = 0;

    private final AppConfig appConfig;

    private NotebookUI() {
        model = new NotebookUIModel();
        appConfig = new AppConfig();
    }

    public static void start() {
        NotebookUI notebookUI = new NotebookUI();
        notebookUI.createUi();
    }

    private void createUi() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(appConfig.getTitle());
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(APP_WIDTH, APP_HEIGHT);

            JPanel leftPanel = new JPanel(new BorderLayout());
            JList<Note> listOfTitles = getListOfTitles();
            JScrollPane leftScrollPane = new JScrollPane(listOfTitles);
            leftPanel.add(leftScrollPane, BorderLayout.CENTER);

            JPanel rightPanel = new JPanel(new BorderLayout());

            JPanel topLeftPanel = new JPanel();
            topLeftPanel.setLayout(new BoxLayout(topLeftPanel, BoxLayout.X_AXIS));

            JButton detailsButton = getDetailsButton();
            topLeftPanel.add(detailsButton, BorderLayout.EAST);

            JButton newButton = new JButton(appConfig.getButtonNew());
            newButton.addActionListener(e -> model.newNote());
            topLeftPanel.add(newButton);

            JTextField searchField = new JTextField();
            model.setSearchField(searchField);
            searchField.addActionListener(e -> {
                NoteLog.info("search: {}", e.getActionCommand());
                List<Note> notes = model.searchNote(e.getActionCommand());
                notes.forEach(note -> NoteLog.info("filename: {}, title: {}", note.getFileName(), note.getTitle()));
            });
            topLeftPanel.add(searchField);
            rightPanel.add(topLeftPanel, BorderLayout.NORTH);

            JTextPane textArea = new JTextPane();
            model.setTextArea(textArea);

            int selectionStart = textArea.getSelectionStart();
            textArea.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (SelectedTextUtil.checkIsCursor(e)) {
                        model.calculateDiffWithUpdate();
                    }
                    if (e.isMetaDown() || e.isControlDown()) {
                        return;
                    }

                    String sel = textArea.getSelectedText();
                    if (sel != null && !sel.isEmpty()) {
                        if (SelectedTextUtil.check(e)) {
                            String oldText = model.getTextArea().getText();
                            String newText = SelectedTextUtil.modifyText(e, model.getTextArea());
                            model.calculateDiff(oldText, newText);
                        }
                    } else {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            model.calculateDiffWithUpdate();
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });
            Label positionLabel = new Label(getPositionText(textArea));
            textArea.addCaretListener(e -> {
                positionLabel.setText(getPositionText(textArea));
                int start = selectionStart;
                int end = textArea.getSelectionEnd();
                String selected = textArea.getSelectedText();

                if (selected != null && !selected.isEmpty()) {
                    MarkedDocument markedDocument = model.getNotebook().getMarkedDocument();
                    markedDocument.set(start, end, selected);
                }
            });

            textArea.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    model.calculateDiffWithUpdate();
                    int pos = textArea.viewToModel2D(e.getPoint());
                    String url = model.getNotebook().getActualNote().findUrl(pos);
                    if (url != null) {
                        try {
                            Desktop.getDesktop().browse(new URI(url));
                        } catch (Exception ex) {
                            NoteLog.info(ex.getMessage());
                        }
                    }
                }
            });

            textArea.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int pos = textArea.viewToModel2D(e.getPoint());
                    String url = model.getNotebook().getActualNote().findUrl(pos);
                    if (url != null) {
                        model.getNotebook().getActualNote().getUrls().stream()
                                .filter(noteUrl -> noteUrl.getUrl().equals(url)).findFirst()
                                .ifPresent(noteUrl -> {
                                    textArea.getStyledDocument().setCharacterAttributes(noteUrl.getStart(), noteUrl.getLength(), TextStyle.urlUnderlineStyle(), true);
                                    textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                    model.setUrlStyleChanged(true);
                                });
                    } else {
                        if (model.isUrlStyleChanged()) {
                            model.getNotebook().getActualNote().getUrls()
                                    .forEach(noteUrl -> textArea.getStyledDocument().setCharacterAttributes(noteUrl.getStart(), noteUrl.getLength(), TextStyle.urlStyle(), true));
                            textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
                            model.setUrlStyleChanged(false);
                        }
                    }
                }
            });

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    model.saveNote();
                    Config.save();
                    frame.dispose();
                    NoteLog.info("closed");
                }
            });

            DocumentArea.dynamicDocumentStyle(textArea, searchField, model.getNotebook());

            JScrollPane scrollPane = new JScrollPane(textArea);
            rightPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            JButton saveButton = new JButton(appConfig.getButtonSave());
            saveButton.addActionListener(e -> model.saveNote());
            JButton deleteButton = new JButton(appConfig.getButtonDelete());
            buttonPanel.add(deleteButton);
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(saveButton);
            buttonPanel.add(positionLabel);
            deleteButton.addActionListener(e -> model.deleteNote());

            rightPanel.add(buttonPanel, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    leftPanel,
                    rightPanel
            );
            splitPane.setDividerLocation(150);
            splitPane.setResizeWeight(0.0);
            textArea.setComponentPopupMenu(createContextMenu(frame));

            frame.add(splitPane, BorderLayout.CENTER);


            detailsButton.addActionListener(e -> {
                panelVisible = !panelVisible;
                leftPanel.setVisible(panelVisible);
                splitPane.setDividerLocation(150);
                splitPane.setResizeWeight(0.0);
                splitPane.revalidate();
            });


            frame.setVisible(true);
            addShortcuts();

            // Custom COPY
            KeyStroke keyStrokeC = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            textArea.getInputMap().put(keyStrokeC, "copy");
            textArea.getActionMap().put("copy", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    model.copyTextToClipboard(e);
                }
            });

            // Custom PASTE
            KeyStroke keyStrokeV = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            textArea.getInputMap().put(keyStrokeV, "paste");
            textArea.getActionMap().put("paste", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    model.insertFromClipboard(e);
                }
            });
            model.cleanNoteWindow();
        });
    }

    private JButton getDetailsButton() {
        Icon squareIcon = new Icon() {
            private final int size = 20;

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                int yMargin = 3;
                g.setColor(Color.BLACK);
                g.drawRect(x, y + yMargin, size, size - yMargin * 2);
                g.drawRect(x, y + yMargin, size / 3, size - yMargin * 2);
                IntStream.range(2, 6).forEach(i ->
                    g.drawLine(x, y + yMargin * i, x + size / 3, y + yMargin * i)
                );
            }
        };

        JButton detailsButton = new JButton(squareIcon);
        detailsButton.setPreferredSize(new Dimension(28, 28)); // kwadratowy przycisk
        detailsButton.setFocusPainted(false);
        detailsButton.setBorder(BorderFactory.createEmptyBorder());
        detailsButton.setContentAreaFilled(false);
        return detailsButton;
    }

    private String getPositionText(JTextPane textArea) {
        Document doc = textArea.getDocument();
        Element root = doc.getDefaultRootElement();
        int caretPos = textArea.getCaretPosition();
        int line = textArea.getDocument().getDefaultRootElement().getElementIndex(caretPos) + 1;
        Element lineElement = root.getElement(root.getElementIndex(caretPos));
        int column = caretPos - lineElement.getStartOffset() + 1;
        return "Ln:" + line + "  Col:" + column + "  Pos:" + caretPos;
    }

    private void addShortcuts() {
        defineShortCut(KeyEvent.VK_S, NotebookUIModel::saveNote);
        defineShortCut(KeyEvent.VK_N, NotebookUIModel::newNote);
        defineShortCut(KeyEvent.VK_D, NotebookUIModel::deleteNote);
        defineShortCut(KeyEvent.VK_L, NotebookUIModel::formatNote);
        defineShortCut(KeyEvent.VK_Z, NotebookUIModel::rollbackNote);
        defineShortCut(KeyEvent.VK_P, NotebookUIModel::dumpChanges);
    }

    private JPopupMenu createContextMenu(JFrame frame) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem authorItem = new JMenuItem(appConfig.getDialogTitleAuthor());
        JMenuItem helpItem = new JMenuItem(appConfig.getDialogTitleHelp());

        authorItem.addActionListener(e ->
                JOptionPane.showMessageDialog(frame,
                        appConfig.getMessageAuthor(),
                        appConfig.getDialogTitleAuthor(),
                        JOptionPane.INFORMATION_MESSAGE));
        helpItem.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                appConfig.getMessageHelp(),
                appConfig.getDialogTitleHelp(),
                JOptionPane.INFORMATION_MESSAGE));
        popupMenu.add(authorItem);
        popupMenu.add(helpItem);
        return popupMenu;
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
                    JList<Note> target = (JList<Note>) me.getSource();
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
