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

    public static SimpleAttributeSet titleStyle() {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, Color.BLACK);
        StyleConstants.setBold(style, true);
        StyleConstants.setFontSize(style, 14);
        return style;
    }
}
