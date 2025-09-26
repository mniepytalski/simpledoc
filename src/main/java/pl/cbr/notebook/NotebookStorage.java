package pl.cbr.notebook;

import pl.cbr.notebook.utils.DateUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NotebookStorage {

    public static List<Note> getAll() {
        return NotebookStorage.listFiles().stream()
                .map(fileName -> Note.of(FileStorage.load(fileName)))
                .collect(Collectors.toList());
    }

    public static void save(Note note) {
        note.getDiary().toLogger();
        System.out.println("=========================================");
        System.out.println("Saving note:\n" + note.getData());
        System.out.println("=========================================");
        FileStorage.save(note.getFileName(), note.getData().replaceAll("\r+\n", "\r\n"));
    }

    public static void copyToBackup(Note note, String operation) {
        String prefix = DateUtil.format(new Date(System.currentTimeMillis()), "yyyyMMdd_HHmmss_");
        FileStorage.copyToBackup(note.getFileName(), StorageConfig.BACKUP_DIR + prefix + note.getFileName());
    }

    public static void delete(Note note) {
        FileStorage.delete(note.getFileName());
    }

    public static Set<String> listFiles() {
        return Stream.of(Objects.requireNonNull(new File(StorageConfig.CURRENT_DIR).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .filter(name -> name.endsWith(StorageConfig.FILE_POSTFIX))
                .collect(Collectors.toSet());
    }
}
