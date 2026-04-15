package pl.cbr.notebook.ui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindPanel extends JPanel {

    private final JFrame frame;
    private final NotebookUIModel model;

    JTextField findField;

    JButton nextBtn;
    JButton prevBtn;

    JToggleButton caseBtn;
    JToggleButton regexBtn;

    JButton closeBtn;

    public FindPanel(JFrame frame, NotebookUIModel model) {
        this.frame = frame;
        this.model = model;

        setLayout(new BorderLayout());
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 3));

        findField = new JTextField(20);

        nextBtn = ButtonUtil.getIconButton("arrow1", "arrow1-hoover");
        ButtonUtil.setSize(nextBtn);
        prevBtn = ButtonUtil.getIconButton("arrow2", "arrow2-hoover");
        ButtonUtil.setSize(prevBtn);

        caseBtn = ButtonUtil.getToggleButton("lower-upper", "lower-upper-hoover");
        ButtonUtil.setSize(caseBtn);
        regexBtn = ButtonUtil.getToggleButton("regexp", "regexp-hoover");
        ButtonUtil.setSize(regexBtn);
        closeBtn = ButtonUtil.getIconButton("close", "close-hoover");
        ButtonUtil.setSize(closeBtn);

        findField.setToolTipText("Szukaj tekstu");
        nextBtn.setToolTipText("Następne (Enter)");
        prevBtn.setToolTipText("Poprzednie (Shift+Enter)");
        caseBtn.setToolTipText("Uwzględnij wielkość liter");
        regexBtn.setToolTipText("Wyrażenia regularne");
        closeBtn.setToolTipText("Zamknij (ESC)");

        add(new JLabel("Znajdź:"));
        add(findField);
        add(prevBtn);
        add(nextBtn);
        add(caseBtn);
        add(regexBtn);
        add(closeBtn);

        initBehavior();

        model.setFindPanel(this);
        this.setVisible(false);
    }

    public void onStart() {
        if (isVisible()) {
            findField.requestFocusInWindow();
            findField.selectAll();
        }
    }

    private void initBehavior() {

        findField.addActionListener(e -> nextBtn.doClick());

        findField.getInputMap().put(
                KeyStroke.getKeyStroke("shift ENTER"), "prev");

        findField.getActionMap().put("prev", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                prevBtn.doClick();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close");

        getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.findDialog();
                frame.revalidate();
            }
        });

        JTextPane textPane = model.getTextArea();

        onNextButton(textPane);
        onPreviousButton(textPane);

        closeBtn.addActionListener(e -> setVisible(false));
    }

    private void onNextButton(JTextPane textPane) {
        nextBtn.addActionListener(e -> {

            String originalText = textPane.getText();
            String text = originalText;
            String search = findField.getText();

            if (search.isEmpty()) return;

            int pos = textPane.getSelectionEnd(); // 🔥 ważniejsze niż caret

            try {
                // 🔁 TRYB REGEX
                if (regexBtn.isSelected()) {

                    int flags = caseBtn.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
                    Pattern pattern = Pattern.compile(search, flags);
                    Matcher matcher = pattern.matcher(originalText);

                    if (matcher.find(pos)) {
                        textPane.requestFocus();
                        textPane.select(matcher.start(), matcher.end());
                    }

                } else {
                    // 🔁 TRYB ZWYKŁY

                    if (!caseBtn.isSelected()) {
                        text = text.toLowerCase();
                        search = search.toLowerCase();
                    }

                    int found = text.indexOf(search, pos);

                    if (found != -1) {
                        textPane.requestFocus();
                        textPane.select(found, found + search.length());
                    }
                }

            } catch (Exception ex) {
                // np. błędny regex "("
                System.out.println("Błędny regex: " + search);
            }
        });
    }

    private void onPreviousButton(JTextPane textPane) {
        prevBtn.addActionListener(e -> {

            String originalText = textPane.getText();
            String text = originalText;
            String search = findField.getText();

            if (search.isEmpty()) return;

            int pos = textPane.getSelectionStart() - 1; // 🔥 kluczowe

            try {
                // 🔁 TRYB REGEX
                if (regexBtn.isSelected()) {

                    int flags = 0;

                    if (!caseBtn.isSelected()) {
                        flags |= Pattern.CASE_INSENSITIVE;
                        flags |= Pattern.UNICODE_CASE;
                    }

                    Pattern pattern = Pattern.compile(search, flags);
                    Matcher matcher = pattern.matcher(originalText);

                    int lastStart = -1;
                    int lastEnd = -1;

                    while (matcher.find()) {
                        if (matcher.start() > pos) break;

                        lastStart = matcher.start();
                        lastEnd = matcher.end();
                    }

                    if (lastStart != -1) {
                        textPane.requestFocus();
                        textPane.select(lastStart, lastEnd);
                    }

                } else {
                    // 🔁 TRYB ZWYKŁY

                    if (!caseBtn.isSelected()) {
                        text = text.toLowerCase();
                        search = search.toLowerCase();
                    }

                    int found = text.lastIndexOf(search, pos);

                    if (found != -1) {
                        textPane.requestFocus();
                        textPane.select(found, found + search.length());
                    }
                }

            } catch (Exception ex) {
                System.out.println("Błędny regex: " + search);
            }
        });
    }
}
