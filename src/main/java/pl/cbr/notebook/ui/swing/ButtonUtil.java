package pl.cbr.notebook.ui.swing;

import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@UtilityClass
public class ButtonUtil {

    private final static int BUTTON_SIZE = 25;

    public void setSize(JToggleButton button) {
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
    }

    public void setButtonSize(JButton button) {
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
    }

    public JButton getIconButton(String icon, String hoover) {
        JButton detailsButton = new JButton(new ImageIcon(getResource("icons/" + icon + ".png")));
        detailsButton.setRolloverIcon(new ImageIcon(getResource("icons/" + hoover + ".png")));
        return detailsButton;
    }

    private URL getResource(String name) {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResource(name);
    }
}
