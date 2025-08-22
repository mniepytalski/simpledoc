package pl.cbr.notebook;

import org.junit.jupiter.api.Test;

import java.util.List;

class NotebookTest {

    @Test
    void getAllTags() {
        Notebook notebook = new Notebook();
        List<String> allTags = notebook.getAllTags();

        System.out.println(allTags.size());
    }

    @Test
    void searchTag() {
        Notebook notebook = new Notebook();
        List<Note> notes = notebook.searchTag("#notatka");

        System.out.println(notes.size());
    }

    @Test
    void searchText() {
        Notebook notebook = new Notebook();
        List<Note> notes = notebook.searchText("nastepna");

        System.out.println(notes.size());
    }
}