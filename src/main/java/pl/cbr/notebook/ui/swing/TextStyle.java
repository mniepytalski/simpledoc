package pl.cbr.notebook.ui.swing;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class TextStyle {

    public static SimpleAttributeSet getStyle(Color color) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        return style;
    }

    public static SimpleAttributeSet getStyle(Color color, boolean bold) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        return style;
    }

    public static SimpleAttributeSet urlStyle() {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, Color.BLUE);
        return style;
    }

    public static SimpleAttributeSet urlUnderlineStyle() {
        SimpleAttributeSet style = urlStyle();
        StyleConstants.setUnderline(style, true);
        return style;
    }

    public static SimpleAttributeSet titleStyle() {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, Color.BLACK);
        StyleConstants.setFontSize(style, 22);
        return style;
    }

    public static SimpleAttributeSet searchStyle() {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setBackground(style, Color.YELLOW);
        return style;
    }
}
