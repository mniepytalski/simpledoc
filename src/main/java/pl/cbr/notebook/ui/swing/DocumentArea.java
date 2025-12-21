package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Note;
import pl.cbr.notebook.NoteUrl;
import pl.cbr.notebook.NoteUrlType;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.utils.StringUtil;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentArea {

    public static void print(Note note, JTextPane textArea, JTextField searchField) {
        StyledDocument doc = textArea.getStyledDocument();
        textArea.setText(StringUtil.EMPTY);
        String searchText = searchField.getText().trim();
        try {
            if (searchText.isEmpty()) {
                note.getUrls().clear();
                textArea.setText(note.getData());
            } else {
                SimpleAttributeSet highlight = TextStyle.getWithForeground(Color.BLUE, true);
                String[] strings = note.getData().split(getRegexp(searchText));
                for (String string : strings) {
                    if (string.equalsIgnoreCase(searchText)) {
                        doc.insertString(doc.getLength(), string, highlight);
                    } else {
                        doc.insertString(doc.getLength(), string, TextStyle.getWithForeground(Color.BLACK));
                    }
                }
            }
        } catch (BadLocationException e) {
            NoteLog.info(e.getMessage());
        }
    }

    static boolean isHighlighting = false;

    public static void dynamicDocumentStyle(JTextPane textArea, JTextField searchField, Notebook notebook) {
        StyledDocument doc = textArea.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet defaultStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
        ((AbstractDocument) doc).setDocumentFilter(new DocumentFilter() {
            private void applyHighlight() {
                if (isHighlighting) return;
                isHighlighting = true;
                SwingUtilities.invokeLater(() -> {
                    try {
                        String text = doc.getText(0, doc.getLength());
                        doc.setCharacterAttributes(0, text.length(), defaultStyle, true);

                        Note note = notebook.getActualNote();
                        note.getUrls().clear();
                        EnumSet.allOf(Keyword.class).forEach(keyword -> {
                            Pattern pattern = Pattern.compile(keyword.getRegexp());
                            Matcher m1 = pattern.matcher(text);
                            while (m1.find()) {
                                doc.setCharacterAttributes(m1.start(), m1.end() - m1.start(), keyword.getStyle(), keyword.isReplace());
                                if (keyword == Keyword.URL || keyword == Keyword.INTERNAL_URL) {
                                    addUrlToList(m1, note, doc, keyword);
                                }
                                if (keyword == Keyword.CODE) {
                                    CodeKeyword.codeAnalyzer(m1, doc, text);
                                }
                            }
                        });
                        if (searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
                            Pattern p1 = Pattern.compile(searchField.getText());
                            Matcher m1 = p1.matcher(text);
                            while (m1.find()) {
                                doc.setCharacterAttributes(m1.start(), m1.end() - m1.start(), TextStyle.searchStyle(), false);
                            }
                        }
                    } catch (BadLocationException e) {
                        NoteLog.info(e.getMessage());
                    } finally {
                        isHighlighting = false;
                    }
                });
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                super.insertString(fb, offset, string, attr);
                applyHighlight();
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length)
                    throws BadLocationException {
                super.remove(fb, offset, length);
                applyHighlight();
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                super.replace(fb, offset, length, text, attrs);
                applyHighlight();
            }
        });
    }

    private static void addUrlToList(Matcher m1, Note note, StyledDocument doc, Keyword keyword) {
        NoteUrlType urlType = keyword ==  Keyword.INTERNAL_URL ? NoteUrlType.INTERNAL : NoteUrlType.EXTERNAL;
        try {
            String url = doc.getText(m1.start(), m1.end() - m1.start());
            if (keyword == Keyword.INTERNAL_URL) {
                if (url.length() > 4) {
                    url = url.substring(2, url.length() - 2);
                }
            }
            note.getUrls().add(NoteUrl.of(m1.start(), m1.end(), url, urlType));
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getRegexp(String text) {
        return "(?i)(?=" + text + ")|(?<=" + text + ")";
    }
}
