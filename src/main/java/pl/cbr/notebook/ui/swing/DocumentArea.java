package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Note;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.List;

public class DocumentArea {

    public static void print(Note note, JTextPane textArea, JTextField searchField) {
        SimpleAttributeSet normal = TextStyle.getStyle(Color.BLACK);
        SimpleAttributeSet titleStyle = TextStyle.titleStyle();
        StyledDocument doc = textArea.getStyledDocument();
        textArea.setText("");
        String searchText = searchField.getText().trim();
        try {
            if (searchText.isEmpty()) {
                printDocument(note, doc, titleStyle, normal);
            } else {
                SimpleAttributeSet highlight = TextStyle.getStyle(Color.BLUE, true);
                String[] strings = note.getData().split(getRegexp(searchText));
                for (String string : strings) {
                    if (string.equalsIgnoreCase(searchText)) {
                        doc.insertString(doc.getLength(), string, highlight);
                    } else {
                        doc.insertString(doc.getLength(), string, normal);
                    }
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static void printDocument(Note note, StyledDocument doc, SimpleAttributeSet titleStyle, SimpleAttributeSet normal) throws BadLocationException {
        int titleEnd = note.getData().indexOf("\n");
        if (titleEnd > 0) {
            doc.insertString(doc.getLength(), note.getData().substring(0, titleEnd), titleStyle);
            List<DocumentPart> documentParts = Keyword.analise(note.getData().substring(titleEnd));
            for (DocumentPart documentPart : documentParts) {
                doc.insertString(doc.getLength(), documentPart.getText(), documentPart.getKeyword().getStyle());
            }
        } else {
            doc.insertString(doc.getLength(), note.getData(), normal);
        }
    }

    private static String getRegexp(String text) {
        return "(?i)(?=" + text + ")|(?<=" + text + ")";
    }
}
