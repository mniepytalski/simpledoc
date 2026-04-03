package pl.cbr.notebook.ui.swing;

import lombok.Getter;

@Getter
public class AppConfig {

    private final int appWidth = 700;
    private final int appHeight = 500;

    private final String title = "Simpledoc v2.3.4";

    private final String messageAuthor = "Notebook @2025\nAuthor: Mariusz Niepytalski\nEmail: mniepytalski@icloud.com";

    private final String messageHelp = "Help\nCTRL+S - save\nCTRL+D - delete\nCTRL+N - new\nCTRL+L - format selected text, only json"
            + "\nCTRL+C - copy selected text\nCTRL+V - paste selected text\nCTRL+X - copy and delete selected text"
            + "\nCTRL+Z - rollback last changes";

    private final String dialogTitleHelp = "Help";

    private final String dialogTitleAuthor = "Author";

    private final String buttonSave = "Save";

    private final String buttonDelete =  "Delete";
}
