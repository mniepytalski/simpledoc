package pl.cbr.notebook.ui.swing;

import lombok.Getter;
import lombok.Setter;
import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;


public class NotebookUIModel {

    @Getter
    private final Notebook notebook;

    @Setter
    private JTextPane textArea;

    @Getter
    @Setter
    private DefaultListModel<Note> titleModel;

    @Getter
    @Setter
    JTextField searchField;

    private final NoteLog log;

    public NotebookUIModel() {
        this.notebook = new Notebook();
        log = new NoteLog();
    }

    public void newNote() {
        log.info("new");
        cleanNoteWindow();
    }

    public void deleteNote() {
        Note actualNote = notebook.getActualNote();
        log.info("delete: {}", actualNote.getFileName());
        if (actualNote.getFileName() != null) {
            notebook.delete(actualNote);
        }
        for (int i = 0; i < titleModel.size(); i++) {
            Note s = titleModel.get(i);
            if (s.getTextId().equals(actualNote.getTextId())) {
                titleModel.removeElementAt(i);
                break;
            }
        }
        cleanNoteWindow();
    }

    public void saveNote() {
        notebook.getActualNote().setData(textArea.getText());
        Note note = notebook.save(notebook.getActualNote());
        note.setTitle(notebook.getNoteTitle(note.getData()));
        if (note.isNewNote()) {
            titleModel.addElement(note);
        } else {
            for (int i = 0; i < titleModel.size(); i++) {
                if (notebook.getActualNote().getTextId().equals(titleModel.get(i).getTextId())) {
                    notebook.getActualNote().setData(titleModel.get(i).getData());
                    titleModel.set(i, notebook.getActualNote());
                }
            }
        }
        notebook.getActualNote().setTitle(notebook.getNoteTitle(textArea.getText()));
        log.info("save: {}", note.getFileName());
    }

    public List<Note> searchNote(String searchText) {
        List<Note> notes = searchNoteByText(searchText);
        titleModel.removeAllElements();
        notes.forEach(note -> titleModel.addElement(note));
        return notes;
    }

    public List<Note> searchNoteByText(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return notebook.getNotes();
        }
        return notebook.getNotes().stream().filter(note -> note.getData().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());
    }

    public void setNoteFromTextId(String textId) {
        Note note = notebook.loadByTextId(textId);
        DocumentArea.print(note, textArea, searchField);
        textArea.grabFocus();
    }

    public void cleanNoteWindow() {
        notebook.setActualNote(Note.empty());
        textArea.setText("");
        textArea.grabFocus();
    }
}
