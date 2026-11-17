package pl.cbr.notebook.ui.swing;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacePanel extends JPanel {

    private final JFrame frame;
    private final NotebookUIModel model;

    JTextField findField = new JTextField(10);
    JTextField replaceField = new JTextField(10);

    JButton nextBtn = ButtonUtil.getIconButton("arrow1", "arrow1-hoover");
    JButton prevBtn = ButtonUtil.getIconButton("arrow2", "arrow2-hoover");

    JToggleButton regexBtn = ButtonUtil.getToggleButton("regexp", "regexp-hoover");
    JToggleButton caseBtn = ButtonUtil.getToggleButton("lower-upper", "lower-upper-hoover");

    JButton replaceBtn = new JButton("Zamień");
    JButton replaceAllBtn = new JButton("All");

    JButton closeBtn = ButtonUtil.getIconButton("close", "close-hoover");

    public ReplacePanel(JFrame frame, NotebookUIModel model) {
        this.frame = frame;
        this.model = model;

        ButtonUtil.setSize(nextBtn);
        ButtonUtil.setSize(prevBtn);
        ButtonUtil.setSize(caseBtn);
        ButtonUtil.setSize(regexBtn);
        ButtonUtil.setSize(closeBtn);

        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 3));

        add(findField);

        add(new JLabel("→"));
        add(replaceField);

        add(prevBtn);
        add(nextBtn);

        add(caseBtn);
        add(regexBtn);

        add(replaceBtn);
        add(replaceAllBtn);

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

    public void setSearchText(String text) {
        findField.setText(text);
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
        onClosePanel();
    }

    private void onClosePanel() {
        closeBtn.addActionListener(e -> setVisible(false));
    }

    private void onStartPanel() {
        findField.requestFocusInWindow();
        findField.selectAll();
    }

    private void onReplaceAllButton(JTextPane textArea) {
        replaceAllBtn.addActionListener(e -> {
            String text = getText(textArea);
            String search = findField.getText();
            String replace = replaceField.getText();

            if (search.isEmpty()) return;

            if (regexBtn.isSelected()) {
                Pattern pattern = Pattern.compile(search, Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(text);
                textArea.setText(matcher.replaceAll(replace
                        .replace("\\n","\n")
                        .replace("\\r","\r")
                        .replace("\\t","\t")
                ));
                return;
            }

            if (caseBtn.isSelected()) {
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
            String text = getText(textArea);
            String search = findField.getText();

            if (search.isEmpty()) return;

            if (!caseBtn.isSelected()) {
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
            String text = getText(textArea);
            String search = findField.getText();

            if (search.isEmpty()) return;

            if (!caseBtn.isSelected()) {
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

    private static String getText(JTextPane textPane) {
        String originalText = null;
        try {
            originalText = textPane.getStyledDocument().getText(0, textPane.getStyledDocument().getLength());
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
        return originalText;
    }
}
