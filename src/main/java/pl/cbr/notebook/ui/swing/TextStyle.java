package pl.cbr.notebook.ui.swing;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class TextStyle {

    public static SimpleAttributeSet getWithForeground(Color color) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        return style;
    }

    public static SimpleAttributeSet getWithBackground(Color color) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setBackground(style, color);
        return style;
    }

    public static SimpleAttributeSet getWithForeground(Color color, boolean bold) {
        SimpleAttributeSet style = TextStyle.getWithForeground(color);
        StyleConstants.setBold(style, bold);
        return style;
    }

    public static SimpleAttributeSet urlStyle() {
        return TextStyle.getWithForeground(Color.BLUE);
    }

    public static SimpleAttributeSet quoteStyle() {
        SimpleAttributeSet style = getWithBackground(Color.LIGHT_GRAY);
        return style;
    }

    public static SimpleAttributeSet paragraph2Style() {
        SimpleAttributeSet style = TextStyle.getWithForeground(Color.BLACK);
        StyleConstants.setFontSize(style, 22);
        return style;
    }

    public static SimpleAttributeSet paragraph3Style() {
        SimpleAttributeSet style = TextStyle.getWithForeground(Color.BLACK);
        StyleConstants.setFontSize(style, 19);
        return style;
    }

    public static SimpleAttributeSet paragraph4Style() {
        SimpleAttributeSet style = TextStyle.getWithForeground(Color.BLACK);
        StyleConstants.setFontSize(style, 16);
        return style;
    }


    public static SimpleAttributeSet italicStyle() {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setItalic(style, true);
        return style;
    }

    public static SimpleAttributeSet boldStyle() {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setBold(style, true);
        return style;
    }


    public static SimpleAttributeSet boldItalicStyle() {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setBold(style, true);
        StyleConstants.setItalic(style, true);
        return style;
    }

    public static SimpleAttributeSet urlUnderlineStyle() {
        SimpleAttributeSet style = urlStyle();
        StyleConstants.setUnderline(style, true);
        return style;
    }

    public static SimpleAttributeSet titleStyle() {
        SimpleAttributeSet style = TextStyle.getWithForeground(Color.BLACK);
        StyleConstants.setFontSize(style, 22);
        return style;
    }

    public static SimpleAttributeSet searchStyle() {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setBackground(style, Color.YELLOW);
        return style;
    }
}
