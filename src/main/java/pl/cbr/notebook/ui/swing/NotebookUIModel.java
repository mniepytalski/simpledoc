package pl.cbr.notebook.ui.swing;

import lombok.Data;
import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.utils.JsonSimpleFormatter;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class NotebookUIModel {

    private final Notebook notebook;
    private JTextPane textArea;
    private DefaultListModel<Note> titleModel;
    JTextField searchField;
    private boolean urlStyleChanged = false;


    public NotebookUIModel() {
        this.notebook = new Notebook();
    }

    public void newNote() {
        NoteLog.info("new");
        cleanNoteWindow();
    }

    public void deleteNote() {
        Note actualNote = notebook.getActualNote();
        NoteLog.info("delete: {}", actualNote.getFileName());
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
        if (textArea.getText().trim().isEmpty()) {
            return;
        }
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
        recreateList(searchNoteByText(searchField.getText()));
        NoteLog.info("save: {}", note.getFileName());
    }

    public void formatNote() {
        textArea.replaceSelection(JsonSimpleFormatter.format(notebook.getMarkedDocument().getText()));
        notebook.getActualNote().setData(textArea.getText());
        notebook.getMarkedDocument().clear();
    }

    public List<Note> searchNote(String searchText) {
        List<Note> notes = searchNoteByText(searchText);
        recreateList(notes);
        return notes;
    }

    public List<Note> searchNoteByText(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return notebook.sortedNotes();
        }
        return notebook.getNotes().stream().filter(note -> note.getData().toLowerCase().contains(searchText.toLowerCase()))
                .sorted((o1, o2) -> o2.getFile().getModificationDate()
                        .compareTo(o1.getFile().getModificationDate()))
                .collect(Collectors.toList());
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

    private void recreateList(List<Note> notes) {
        titleModel.removeAllElements();
        notes.forEach(note -> titleModel.addElement(note));
    }
}
