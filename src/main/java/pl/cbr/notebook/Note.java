package pl.cbr.notebook;

import lombok.Getter;
import lombok.Setter;
import pl.cbr.notebook.ui.swing.Keyword;

import java.util.Date;
import java.util.Set;

@Getter
public class Note {
    @Setter
    private FileData file;

    @Setter
    private String title;
    private Set<String> tags;
    private final boolean newNote;

    private static final int counter = 0;

    private Note(FileData file, String title, Set<String> tags, boolean newNote) {
        this.file = file;
        this.title = title;
        this.tags = tags;
        this.newNote = newNote;
    }

    public String getTextId() {
        return title + "-" + file.getFileName();
    }

    public String getData() {
        return file.getData();
    }

    public void setData(String data) {
        if (!file.getData().equals(data)) {
            file.setData(data);
            tags = Keyword.TAG.search(data);
            file.setModificationDate(new Date(System.currentTimeMillis()));
        }
    }

    public String getFileName() {
        return file.getFileName();
    }

    public void setFileName(String fileName) {
        file.setFileName(fileName);
    }

    public static Note of(FileData file) {
        return new Note(file, Note.getNoteTitle(file.getData()), Keyword.TAG.search(file.getData()), false);
    }

    public static Note empty()  {
        return new Note(FileData.empty(), "", Set.of(), true);
    }

    private static String getNoteTitle(String data) {
        String[] lines = data.split("\\n");
        if (lines.length >= 1) {
            return lines[0].trim();
        } else {
            return "";
        }
    }
}
