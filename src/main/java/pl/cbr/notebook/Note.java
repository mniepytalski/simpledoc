package pl.cbr.notebook;

import lombok.Getter;
import lombok.Setter;
import pl.cbr.notebook.diary.Diary;
import pl.cbr.notebook.ui.swing.Keyword;
import pl.cbr.notebook.ui.swing.CategoryElement;
import pl.cbr.notebook.utils.DateUtil;
import pl.cbr.notebook.utils.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
public class Note {
    @Setter
    private FileData file;

    @Setter
    private String title;
    private String category;
    private Set<String> tags;
    private final boolean newNote;
    private final List<NoteUrl> urls;
    private final Diary diary;

    private Note(FileData file, String title, Set<String> tags, boolean newNote, String category) {
        this.file = file;
        this.title = title;
        this.tags = tags;
        this.newNote = newNote;
        this.category = category;
        this.urls = new ArrayList<>();
        this.diary = new Diary();
    }

    public String getTextId() {
        return title + "-" + file.getFileName();
    }

    public String getData() {
        return file.getData();
    }

    public String getModificationDate() {
        return DateUtil.formatDate(getFile().getModificationDate());
    }

    public String getModificationDateHour() {
        return DateUtil.formatShortDate(getFile().getModificationDate());
    }

    public String getModificationDay() {
        return DateUtil.formatDayDate(getFile().getModificationDate());
    }

    public String getLabel() {
        if (category.isEmpty()) {
            return title;
        } else {
            return title.substring(category.length() + 1);
        }
    }

    public void setData(String data) {
        if (!file.getData().equals(data)) {
            file.setData(data);
            title = Note.getNoteTitle(file.getData());
            category = calculateCategory(title);
            tags = Keyword.TAG.search(data);
            file.setModificationDate(new Date(System.currentTimeMillis()));
        }
    }

    public NoteUrl findUrl(int position) {
        return urls.stream()
                .filter(url -> url.getStart() <= position && url.getEnd() > position)
                .findFirst()
                .orElse(null);
    }

    public String getFileName() {
        return file.getFileName();
    }

    public void setFileName(String fileName) {
        file.setFileName(fileName);
    }

    public static Note of(FileData file) {
        String noteTitle = Note.getNoteTitle(file.getData());
        return new Note(file, noteTitle, Keyword.TAG.search(file.getData()), false, calculateCategory(noteTitle));
    }

    public static Note empty()  {
        return new Note(FileData.empty(), StringUtil.EMPTY, Set.of(), true, StringUtil.EMPTY);
    }

    private static String getNoteTitle(String data) {
        String[] lines = data.split("\\n");
        if (lines.length >= 1) {
            return lines[0].trim();
        } else {
            return StringUtil.EMPTY;
        }
    }

    private static String calculateCategory(String title) {
        int position = title.indexOf(".");
        if (position == -1) return "";
        String category = title.substring(0, position).toUpperCase().trim();
        try {
            CategoryElement.valueOf(category);
            return category;
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}
