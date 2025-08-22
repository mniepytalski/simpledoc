package pl.cbr.notebook;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class NotebookStorageTest {

    @Test
    void getAllNotes() {
        List<Note> notes = NotebookStorage.getAll();
        System.out.println(notes.size());
    }

    @Test
    void listFiles() {
        Set<String> files = NotebookStorage.listFiles();
        System.out.println(files);
    }
}
