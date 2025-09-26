package pl.cbr.notebook.utils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Set;

public class SelectedTextUtil {
    static Set<Integer> vkSelectModification = Set.of(KeyEvent.VK_ENTER, KeyEvent.VK_SPACE, KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE, KeyEvent.VK_TAB);
    static Set<Integer> vkCursors = Set.of(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);

    public static boolean checkIsCursor(KeyEvent e) {
        return vkCursors.contains(e.getKeyCode());
    }

    public static boolean check(KeyEvent e) {
        return vkSelectModification.contains(e.getKeyCode());
    }

    public static String modifyText(KeyEvent e, JTextPane textArea) {
        String firstPart = textArea.getText().substring(0, textArea.getSelectionStart());
        String secondPart = textArea.getText().substring(textArea.getSelectionEnd());
        if (KeyEvent.VK_DELETE == e.getKeyCode() || KeyEvent.VK_BACK_SPACE == e.getKeyCode()) {
            return firstPart + secondPart;
        } else {
            return firstPart + e.getKeyChar() + secondPart;
        }
    }
}
