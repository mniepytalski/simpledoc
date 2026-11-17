package pl.cbr.notebook.ui.swing;

import lombok.extern.slf4j.Slf4j;
import pl.cbr.notebook.SingleInstance;

@Slf4j
public class NotebookApp {

    public static void main(String[] args) {
        if (SingleInstance.lockInstance()) {
            NotebookUI.start();
        } else {
            log.error("Application already started on this machine");
        }
    }
}
