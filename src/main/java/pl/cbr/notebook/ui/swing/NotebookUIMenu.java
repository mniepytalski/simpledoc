package pl.cbr.notebook.ui.swing;

import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@UtilityClass
public class NotebookUIMenu {

    public void createMenu(JFrame frame) {
        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        // Create menu items for File
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Add items to File menu
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Create "About" item for Help menu
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        // Add menu bar to frame
        frame.setJMenuBar(menuBar);

        // === Actions ===

        // Exit action
        exitItem.addActionListener(e -> System.exit(0));

        // About action
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                frame,
                "Author: Mariusz Niepytalski\nVersion: 1.0\n(c) 2025",
                "About",
                JOptionPane.INFORMATION_MESSAGE
        ));
    }
}
