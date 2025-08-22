package pl.cbr.notebook.ui.swing;

public class NoteLog {

    public void info(String message) {
        System.out.println(message);
    }

    public void info(String message, String ... params) {
        String replaced = message.replaceAll("\\{}", "%s");
        System.out.println(String.format(replaced, params));
    }
}
