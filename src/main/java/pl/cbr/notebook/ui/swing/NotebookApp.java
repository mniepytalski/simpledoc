package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.SingleInstance;

public class NotebookApp {

    public static void main(String[] args) {
        if (SingleInstance.lockInstance()) {
            NotebookUI.start();
        } else {
            System.out.println("Application already started on this machine");
        }
    }
}
