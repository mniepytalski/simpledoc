package pl.cbr.notebook.ui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ReplacePanel extends JPanel {

    private final JFrame frame;
    private final NotebookUIModel model;

    JTextField findField = new JTextField(10);
    JTextField replaceField = new JTextField(10);

    JButton nextBtn = ButtonUtil.getIconButton("arrow1", "arrow1-hoover");
    JButton prevBtn = ButtonUtil.getIconButton("arrow2", "arrow2-hoover");

    JButton replaceBtn = new JButton("Zamień");
    JButton replaceAllBtn = new JButton("Wszystko");

    JButton closeBtn = new JButton("✖");

    JCheckBox caseCheck = new JCheckBox("Aa");

    public ReplacePanel(JFrame frame, NotebookUIModel model) {
        this.frame = frame;
        this.model = model;

        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 3));

        add(findField);

        add(new JLabel("→"));
        add(replaceField);

        add(prevBtn);
        add(nextBtn);

        add(replaceBtn);
        add(replaceAllBtn);

        add(caseCheck);
        add(closeBtn);

        initBehavior();

        model.setReplacePanel(this);
        this.setVisible(false);
    }

    public void onStart() {
        if (isVisible()) {
            findField.requestFocusInWindow();
            findField.selectAll();
        }
    }

    private void initBehavior() {

        // Enter → następny
        findField.addActionListener(e -> nextBtn.doClick());

        // Shift+Enter → poprzedni
        findField.getInputMap().put(
                KeyStroke.getKeyStroke("shift ENTER"), "prev");

        findField.getActionMap().put("prev", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                prevBtn.doClick();
            }
        });

        // ESC → zamknij
        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close");

        getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        // Enter w replace → zamień
        replaceField.addActionListener(e -> replaceBtn.doClick());

        getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.findDialog();
                frame.revalidate();
            }
        });

        JTextPane textArea = model.getTextArea();

        onNextButton(textArea);
        onPreviousButton(textArea);
        onReplaceButton(textArea);
        onReplaceAllButton(textArea);
        onStartPanel();
    }

    private void onStartPanel() {
        findField.requestFocusInWindow();
        findField.selectAll();
    }

    private void onReplaceAllButton(JTextPane textArea) {
        replaceAllBtn.addActionListener(e -> {
            String text = textArea.getText();
            String search = findField.getText();
            String replace = replaceField.getText();

            if (search.isEmpty()) return;

            if (caseCheck.isSelected()) {
                textArea.setText(text.replace(search, replace));
            } else {
                String lowerText = text.toLowerCase();
                String lowerSearch = search.toLowerCase();

                StringBuilder result = new StringBuilder();
                int index = 0;

                while (true) {
                    int found = lowerText.indexOf(lowerSearch, index);
                    if (found == -1) {
                        result.append(text.substring(index));
                        break;
                    }

                    result.append(text, index, found);
                    result.append(replace);

                    index = found + search.length();
                }

                textArea.setText(result.toString());
            }
        });
    }

    private void onReplaceButton(JTextPane textArea) {
        replaceBtn.addActionListener(e -> {
            String search = findField.getText();
            String replace = replaceField.getText();

            int start = textArea.getSelectionStart();

            String selected = textArea.getSelectedText();

            if (selected != null && selected.equals(search)) {

                textArea.replaceSelection(replace);

                // 🔥 KLUCZOWE
                textArea.setCaretPosition(start + replace.length());
            }

            nextBtn.doClick();
        });
    }

    private void onPreviousButton(JTextPane textArea) {
        prevBtn.addActionListener(e -> {
            String text = textArea.getText();
            String search = findField.getText();

            if (search.isEmpty()) return;

            if (!caseCheck.isSelected()) {
                text = text.toLowerCase();
                search = search.toLowerCase();
            }

            // 🔥 ZMIANA
            int pos = textArea.getSelectionStart() - 1;

            int found = text.lastIndexOf(search, pos);

            if (found != -1) {
                textArea.requestFocus();
                textArea.select(found, found + search.length());
            }
        });
    }

    private void onNextButton(JTextPane textArea) {
        nextBtn.addActionListener(e -> {
            String text = textArea.getText();
            String search = findField.getText();

            if (search.isEmpty()) return;

            if (!caseCheck.isSelected()) {
                text = text.toLowerCase();
                search = search.toLowerCase();
            }

            // 🔥 ZMIANA
            int pos = textArea.getSelectionEnd();

            int found = text.indexOf(search, pos);

            if (found != -1) {
                textArea.requestFocus();
                textArea.select(found, found + search.length());
            }
        });
    }
}
