package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Config;
import pl.cbr.notebook.Note;
import pl.cbr.notebook.NoteUrl;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.utils.DateUtil;
import pl.cbr.notebook.utils.SelectedTextUtil;

import javax.swing.*;
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
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class NotebookUI {
    private final NotebookUIModel model;
    JFrame frame;

    private static boolean panelVisible = true;

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
            frame = new JFrame(appConfig.getTitle());
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(appConfig.getAppWidth(), appConfig.getAppHeight());

            JPanel leftPanel = new JPanel(new BorderLayout());
            model.setListOfTitles(getListOfTitles());
            JScrollPane leftScrollPane = new JScrollPane(model.getListOfTitles());
            leftPanel.add(leftScrollPane, BorderLayout.CENTER);

            JPanel rightPanel = new JPanel(new BorderLayout());

            JPanel topLeftPanel = new JPanel();
            topLeftPanel.setLayout(new BoxLayout(topLeftPanel, BoxLayout.X_AXIS));

            JButton detailsButton = getIconButton("details", "details_hoover");
            detailsButton.setToolTipText("Schowaj/Wysun lewy panel");
            topLeftPanel.add(detailsButton, BorderLayout.EAST);

            JButton menuButton = getIconButton("menu", "menu_hoover");
            menuButton.setToolTipText("Menu");
            topLeftPanel.add(menuButton);

            JPopupMenu menu = createMenu(menuButton);
            menuButton.addActionListener(e ->
                    menu.show(menuButton, 0, menuButton.getHeight())
            );

            JButton newButton = getIconButton("new", "new_hoover");
            newButton.setToolTipText("Okno nowej notatki");
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
                        String oldText = model.getTextArea().getText();
                        String newText = SelectedTextUtil.modifyText(e, model.getTextArea());
                        model.calculateDiff(oldText, newText);
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
                int end = textArea.getSelectionEnd();
                String selected = textArea.getSelectedText();
                if (selected != null && !selected.isEmpty()) {
                    MarkedDocument markedDocument = model.getNotebook().getMarkedDocument();
                    markedDocument.set(selectionStart, end, selected);
                }
            });

            textArea.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    model.calculateDiffWithUpdate();
                    int pos = textArea.viewToModel2D(e.getPoint());
                    NoteUrl noteUrl = model.getNotebook().getActualNote().findUrl(pos);
                    if (noteUrl != null) {
                        try {
                            switch(noteUrl.getType()) {
                                case EXTERNAL:
                                    Desktop.getDesktop().browse(new URI(noteUrl.getUrl()));
                                    break;
                                case INTERNAL:
                                    model.setNoteFromInternalLink(noteUrl.getUrl());
                                    break;
                            }
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
                    NoteUrl url = model.getNotebook().getActualNote().findUrl(pos);
                    if (url != null) {
                        model.getNotebook().getActualNote().getUrls().stream()
                                .filter(noteUrl -> noteUrl.getUrl().equals(url.getUrl())).findFirst()
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
            KeyStroke keyStrokeC = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
            textArea.getInputMap().put(keyStrokeC, "copy");
            textArea.getActionMap().put("copy", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    model.copyTextToClipboard(e);
                }
            });

            // Custom PASTE
            KeyStroke keyStrokeV = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
            textArea.getInputMap().put(keyStrokeV, "paste");
            textArea.getActionMap().put("paste", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    model.insertFromClipboard(e);
                }
            });
            model.cleanNoteWindow();
        });
    }

    private JButton getIconButton(String icon, String hoover) {
        JButton detailsButton = new JButton(new ImageIcon(getResource("icons/" + icon + ".png")));
        detailsButton.setRolloverIcon(new ImageIcon(getResource("icons/" + hoover + ".png")));
        return detailsButton;
    }

    private static URL getResource(String name) {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResource(name);
    }

    private String getPositionText(JTextPane textArea) {
        Element root = textArea.getDocument().getDefaultRootElement();
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

    private JPopupMenu createMenu(JButton jButton) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem authorItem = new JMenuItem(appConfig.getDialogTitleAuthor());
        JMenuItem infoItem = new JMenuItem("Instrukcja");
        JMenuItem helpItem = new JMenuItem(appConfig.getDialogTitleHelp());

        authorItem.addActionListener(e ->
                JOptionPane.showMessageDialog(jButton,
                        appConfig.getMessageAuthor(),
                        appConfig.getDialogTitleAuthor(),
                        JOptionPane.INFORMATION_MESSAGE));
        infoItem.addActionListener(e ->
                model.setNoteFromInternalLink("Info"));
        helpItem.addActionListener(e ->
                JOptionPane.showMessageDialog(jButton,
                appConfig.getMessageHelp(),
                appConfig.getDialogTitleHelp(),
                JOptionPane.INFORMATION_MESSAGE));
        popupMenu.add(infoItem);
        popupMenu.add(helpItem);
        popupMenu.add(authorItem);
        return popupMenu;
    }

    private void defineShortCut(int keyEvent, Consumer<NotebookUIModel> action) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        model.getTextArea().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "" + keyStroke);
        model.getTextArea().getActionMap().put("" + keyStroke, new AbstractAction() {
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
                        menuItemInfo.addActionListener(e -> {
                        JOptionPane.showMessageDialog(frame,
                                "Title: " + note.getTitle() + "\nFile name: " + note.getFileName(),
                                note.getTitle() ,
                                JOptionPane.INFORMATION_MESSAGE);
                        });
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
