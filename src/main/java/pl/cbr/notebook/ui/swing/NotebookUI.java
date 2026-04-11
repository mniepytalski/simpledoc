package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Config;
import pl.cbr.notebook.Note;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.Consumer;

public class NotebookUI {
    private final NotebookUIModel model;
    JFrame frame;

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
            frame = createFrame();

            model.setLeftPanel(new JPanel(new BorderLayout()));
            ListOfTitlesUI listOfTitlesUI = new ListOfTitlesUI(frame, model);
            model.setListOfTitles(listOfTitlesUI.create());
            JScrollPane leftScrollPane = new JScrollPane(model.getListOfTitles());
            model.getLeftPanel().add(leftScrollPane, BorderLayout.CENTER);

            model.setRightPanel(new JPanel(new BorderLayout()));

            JPanel northContainer = new JPanel();
            northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));

            northContainer.add(createTopRightPanel());      // Twój pasek

            Label positionLabel = new Label();
            TextPanel textArea = new TextPanel(model, positionLabel);
            northContainer.add(new FindPanel(frame, model));
            northContainer.add(new ReplacePanel(frame, model));

            model.getRightPanel().add(northContainer, BorderLayout.NORTH);
            model.getRightPanel().add(new JScrollPane(textArea), BorderLayout.CENTER);
            model.getRightPanel().add(createButtonPanel(positionLabel), BorderLayout.SOUTH);

            model.setSplitPane(new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    model.getLeftPanel(),
                    model.getRightPanel()
            ));
            model.getSplitPane().setDividerLocation(150);
            model.getSplitPane().setResizeWeight(0.0);

            frame.add(model.getSplitPane(), BorderLayout.CENTER);

            frame.setVisible(true);
            addShortcuts();

            model.cleanNoteWindow();
        });
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame(appConfig.getTitle());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(appConfig.getAppWidth(), appConfig.getAppHeight());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.saveNote();
                Config.save();
                frame.dispose();
                NoteLog.info("closed");
            }
        });
        return frame;
    }

    private JPanel createTopRightPanel() {
        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new BoxLayout(topRightPanel, BoxLayout.X_AXIS));
        topRightPanel.add(createDetailsButton(), BorderLayout.EAST);
        topRightPanel.add(createMenuButton());
        topRightPanel.add(createNewButton());
        topRightPanel.add(createSearchField());
        return topRightPanel;
    }

    private JPanel createButtonPanel(Label positionLabel) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(createDeleteButton());
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(positionLabel);
        buttonPanel.add(createSaveButton());
        return buttonPanel;
    }

    private JButton createDeleteButton() {
        JButton deleteButton = new JButton(appConfig.getButtonDelete());
        deleteButton.addActionListener(e -> model.deleteNote());
        return deleteButton;
    }

    private JButton createSaveButton() {
        JButton saveButton = new JButton(appConfig.getButtonSave());
        saveButton.addActionListener(e -> model.saveNote());
        return saveButton;
    }

    private JTextField createSearchField() {
        JTextField searchField = new JTextField();
        model.setSearchField(searchField);
        searchField.addActionListener(e -> {
            NoteLog.info("search: {}", e.getActionCommand());
            List<Note> notes = model.searchNote(e.getActionCommand());
            notes.forEach(note -> NoteLog.info("filename: {}, title: {}", note.getFileName(), note.getTitle()));
        });
        return searchField;
    }

    private JButton createNewButton() {
        JButton newButton = ButtonUtil.getIconButton("new", "new_hoover");
        newButton.setToolTipText("Okno nowej notatki");
        newButton.addActionListener(e -> model.newNote());
        return newButton;
    }

    private JButton createMenuButton() {
        JButton menuButton = ButtonUtil.getIconButton("menu", "menu_hoover");
        menuButton.setToolTipText("Menu");
        PopupMenu menu = new PopupMenu(model, appConfig, menuButton);
        menuButton.addActionListener(e ->
                menu.show(menuButton, 0, menuButton.getHeight())
        );
        return menuButton;
    }

    private JButton createDetailsButton() {
        JButton detailsButton = ButtonUtil.getIconButton("details", "details_hoover");
        detailsButton.setToolTipText("Schowaj/Wysun lewy panel");
        detailsButton.addActionListener(e -> model.leftSideOnOff());
        return detailsButton;
    }

    private void addShortcuts() {
        defineShortCut(KeyEvent.VK_S, NotebookUIModel::saveNote);
        defineShortCut(KeyEvent.VK_N, NotebookUIModel::newNote);
        defineShortCut(KeyEvent.VK_D, NotebookUIModel::deleteNote);
        defineShortCut(KeyEvent.VK_L, NotebookUIModel::formatNote);
        defineShortCut(KeyEvent.VK_Z, NotebookUIModel::rollbackNote);
        defineShortCut(KeyEvent.VK_P, NotebookUIModel::dumpChanges);
        defineShortCut(KeyEvent.VK_1, NotebookUIModel::leftSideOnOff);
        defineShortCut(KeyEvent.VK_F, NotebookUIModel::findDialog);
        defineShortCut(KeyEvent.VK_R, NotebookUIModel::replaceDialog);
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
}
