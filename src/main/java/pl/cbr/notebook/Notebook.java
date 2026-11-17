package pl.cbr.notebook;

import lombok.Getter;
import lombok.Setter;
import pl.cbr.notebook.ui.swing.MarkedDocument;
import pl.cbr.notebook.utils.StringUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Notebook {

    private final List<Note> notes;
    @Setter
    private Note actualNote;
    private final MarkedDocument markedDocument;

    public Notebook() {
        FileStorage.createDir(StorageConfig.CURRENT_DIR);
        FileStorage.createDir(StorageConfig.CURRENT_DIR + StorageConfig.BACKUP_DIR);

        FileStorage.copyFromResource("/Info.txt", FileStorage.getFileDataLocation("Info.txt"));
        notes = NotebookStorage.getAll().stream()
                .sorted((o1, o2) -> o2.getFile().getModificationDate()
                        .compareTo(o1.getFile().getModificationDate()))
                .collect(Collectors.toList());
        actualNote = Note.of(FileData.empty());
        markedDocument = new MarkedDocument();
    }

    public List<Note> sortedNotes() {
        return notes.stream()
                .sorted((o1, o2) -> o2.getFile().getModificationDate()
                        .compareTo(o1.getFile().getModificationDate()))
                .collect(Collectors.toList());
    }

    public List<Note> searchText(String text) {
        return notes.stream().filter(note -> note.getData().contains(text))
                .sorted((o1, o2) -> o2.getFile().getModificationDate()
                        .compareTo(o1.getFile().getModificationDate()))
                .collect(Collectors.toList());
    }

    public List<Note> searchTag(String tag) {
        return notes.stream().filter(note -> note.getTags().contains(tag)).collect(Collectors.toList());
    }

    public List<String> getAllTags() {
        return notes.stream().flatMap(note -> note.getTags().stream()).distinct().collect(Collectors.toList());
    }

    public void delete(Note note) {
        notes.remove(note);
        NotebookStorage.copyToBackup(note, "delete");
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
                        note.setFileName(FileName.get());
                        notes.add(note);
                    }
                    return note;
                });
        NotebookStorage.save(newNote);
        NotebookStorage.copyToBackup(note, "save");
        return newNote;
    }

    public Note loadByTextId(String textId) {
        Optional<Note> note = notes.stream().filter(n -> n.getTextId().equalsIgnoreCase(textId)).findFirst();
        actualNote = note.orElseGet(Note::empty);
        return actualNote;
    }

    public Note loadByTitle(String title) {
        Optional<Note> note = notes.stream().filter(n -> n.getTitle().equalsIgnoreCase(title)).findFirst();
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
            return StringUtil.EMPTY;
        }
    }
}
