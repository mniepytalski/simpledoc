package pl.cbr.notebook.ui.swing;

import lombok.Data;
import pl.cbr.notebook.Config;
import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.diary.Diary;
import pl.cbr.notebook.diary.DiaryChange;
import pl.cbr.notebook.diary.MyersDiff;
import pl.cbr.notebook.utils.JsonSimpleFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
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
        Config.set("time", "" + System.currentTimeMillis());
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

    public boolean calculateDiff() {
        return calculateDiff(getNotebook().getActualNote().getData(), getTextArea().getText());
    }

    public void calculateDiffWithUpdate() {
        if (calculateDiff()) {
            updateTextArea();
        }
    }

    public boolean calculateDiff(String oldText, String newText) {
        String diff = MyersDiff.diff(oldText, newText);
        if (diff.isEmpty()) {
            return false;
        }
        Diary diary = getNotebook().getActualNote().getDiary();
        diary.addDiff(diff);
        return true;
    }

    public void updateTextArea() {
        getNotebook().getActualNote().setData(getTextArea().getText());
    }

    public void copyTextToClipboard(ActionEvent e) {
        String text = textArea.getSelectedText();
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
        System.out.println("Copied: " + text);
    }

    public void insertFromClipboard(ActionEvent e) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String pasteText = (String) clipboard.getData(DataFlavor.stringFlavor);
            String oldText = textArea.getText();
            textArea.replaceSelection(pasteText);
            calculateDiff(oldText, textArea.getText());
            System.out.println("Pasted: " + pasteText);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void formatNote() {
        String oldText = textArea.getText();
        textArea.replaceSelection(JsonSimpleFormatter.format(notebook.getMarkedDocument().getText()));
        calculateDiff(oldText, textArea.getText());
        notebook.getActualNote().setData(textArea.getText());
        notebook.getMarkedDocument().clear();
    }

    public void rollbackNote() {
        calculateDiffWithUpdate();
        Diary diary = notebook.getActualNote().getDiary();
        if (!diary.getChanges().isEmpty()) {
            DiaryChange change = diary.getChanges().remove(diary.getChanges().size() - 1);
            System.out.println("==============");
            System.out.println("diff:\n" + change.getChange());
            System.out.println("--------------");
            System.out.println("input:\n" + textArea.getText());
            System.out.println("--------------");
            String reversed = MyersDiff.reverseApply(textArea.getText(), change.getChange());
            System.out.println("reverse changed:\n" + reversed);
            textArea.setText(reversed);
            notebook.getActualNote().setData(textArea.getText());
        }
    }

    public void dumpChanges() {
        Diary diary = notebook.getActualNote().getDiary();
        diary.toLogger();
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
