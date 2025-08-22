package pl.cbr.notebook;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
public class Note {
    @Setter
    private String data;

    @Setter
    private String fileName;

    @Setter
    private String title;
    private final Set<String> tags;
    private final boolean newNote;

    private static final int counter = 0;

    private Note(String data, String fileName, String title, Set<String> tags, boolean newNote) {
        this.data = data;
        this.fileName = fileName;
        this.title = title;
        this.tags = tags;
        this.newNote = newNote;
    }

    public String getTextId() {
        return title + "-" + fileName;
    }

    public static Note of(FileData file) {
        return Note.of(file.getData(), file.getFileName());
    }

    public static Note of(String data, String fileName) {
        return new Note(data, fileName, Note.getNoteTitle(data), TagUtil.search(data), false);
    }

    public static Note empty()  {
        return new Note("", null, "", Set.of(), true);
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
