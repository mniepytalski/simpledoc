package pl.cbr.notebook.ui.swing;

import javax.swing.*;

public class PopupMenu extends JPopupMenu {
    private final NotebookUIModel model;
    private final AppConfig appConfig;

    public PopupMenu(NotebookUIModel model, AppConfig appConfig, JButton jButton) {
        this.model = model;
        this.appConfig = appConfig;
        createUI(jButton);
    }

    private void createUI(JButton jButton) {
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
        add(infoItem);
        add(helpItem);
        add(authorItem);
    }
}
