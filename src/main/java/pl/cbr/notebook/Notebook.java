package pl.cbr.notebook;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Notebook {

    private final List<Note> notes;
    @Setter
    private Note actualNote;
    static int counter;

    public Notebook() {
        FileStorage.createDir(StorageConfig.CURRENT_DIR);
        FileStorage.createDir(StorageConfig.CURRENT_DIR + StorageConfig.BACKUP_DIR);
        notes = NotebookStorage.getAll();
        counter = notes.size() + 1;
        actualNote = Note.of("", null);
    }

    public static String getNewFileName() {
        return "filename" + counter++ + StorageConfig.FILE_POSTFIX;
    }

    public List<Note> searchText(String text) {
        return notes.stream().filter(note -> note.getData().contains(text)).collect(Collectors.toList());
    }

    public List<Note> searchTag(String tag) {
        return notes.stream().filter(note -> note.getTags().contains(tag)).collect(Collectors.toList());
    }

    public List<String> getAllTags() {
        return notes.stream().flatMap(note -> note.getTags().stream()).distinct().collect(Collectors.toList());
    }

    public void delete(Note note) {
        NotebookStorage.copyToBackup(note);
        NotebookStorage.delete(note);
        actualNote = Note.empty();
    }

    public Note save(Note note) {
        Note newNote = notes.stream().filter(n -> n.getFileName().equals(note.getFileName()))
                .findAny()
                .map(n -> {
                    n.setData(note.getData());
                    return n;
                }).orElseGet(() -> {
                    if (note.getFileName() == null) {
                        note.setFileName(Notebook.getNewFileName());
                        notes.add(note);
                    }
                    return note;
                });
        NotebookStorage.save(newNote);
        return newNote;
    }

    public Note loadByTextId(String textId) {
        Optional<Note> note = notes.stream().filter(n -> n.getTextId().equalsIgnoreCase(textId)).findFirst();
        actualNote = note.orElseGet(Note::empty);
        return actualNote;
    }

    public String getNoteTitle(String data) {
        return getNoteTitleFromData(data);
    }

    private String getNoteTitleFromData(String data) {
        String[] lines = data.split("\\n");
        if (lines.length >= 1) {
            return lines[0].trim();
        } else {
            return "";
        }
    }
}
