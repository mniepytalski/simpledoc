package pl.cbr.notebook.ui.swing;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pl.cbr.notebook.Config;
import pl.cbr.notebook.Note;
import pl.cbr.notebook.Notebook;
import pl.cbr.notebook.diary.Diary;
import pl.cbr.notebook.diary.DiaryChange;
import pl.cbr.notebook.diary.MyersDiff;
import pl.cbr.notebook.utils.JsonSimpleFormatter;
import pl.cbr.notebook.utils.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class NotebookUIModel {

    private final Notebook notebook;
    private TextPanel textArea;
    private ListOfNotes listOfTitlesUI;
    JTextField searchField;
    private boolean urlStyleChanged = false;

    private JPanel leftPanel;
    private JPanel rightPanel;

    private JSplitPane splitPane;
    private static boolean leftPanelVisible = true;

    private FindPanel findPanel;
    private ReplacePanel replacePanel;

    public NotebookUIModel() {
        this.notebook = new Notebook();
        Config.set("time", "" + System.currentTimeMillis());
    }
    public NotebookUIModel(Notebook notebook) {
        this.notebook = new Notebook();
        Config.set("time", "" + System.currentTimeMillis());
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
        listOfTitlesUI.remove(actualNote);
        cleanNoteWindow();
    }

    public void saveNote() {
        if (textArea.getText().trim().isEmpty()) {
            return;
        }
        notebook.getActualNote().setData(textArea.getText());
        Note note = notebook.save(notebook.getActualNote());
        note.setTitle(notebook.getNoteTitle(note.getData()));

        listOfTitlesUI.saveNote(note, notebook.getActualNote());

        notebook.getActualNote().setTitle(notebook.getNoteTitle(textArea.getText()));
        recreateList(searchNoteByText(searchField.getText()));
        log.info("save: {}", note.getFileName());
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

    public void copyTextToClipboard() {
        String text = textArea.getSelectedText();
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
        log.info("Copied: {}", text);
    }

    public void insertFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String pasteText = (String) clipboard.getData(DataFlavor.stringFlavor);
            textArea.replaceSelection(pasteText);
            log.info("Pasted: {}", pasteText);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    public void formatNote() {
        textArea.replaceSelection(JsonSimpleFormatter.format(notebook.getMarkedDocument().getText()));
        notebook.getActualNote().setData(textArea.getText());
        notebook.getMarkedDocument().clear();
    }

    public void rollbackNote() {
        calculateDiffWithUpdate();
        Diary diary = notebook.getActualNote().getDiary();
        if (!diary.getChanges().isEmpty()) {
            DiaryChange change = diary.getChanges().remove(diary.getChanges().size() - 1);
            log.info("==============");
            log.info("diff:\n{}", change.getChange());
            log.info("input:\n{}", textArea.getText());
            log.info("--------------");
            String reversed = MyersDiff.reverseApply(textArea.getText(), change.getChange());
            log.info("reverse changed:\n{}", reversed);
            textArea.setText(reversed);
            notebook.getActualNote().setData(textArea.getText());
        }
    }

    public void findDialog() {
        findPanel.setVisible(!findPanel.isVisible());
        findPanel.onStart();
    }

    public void replaceDialog() {
        replacePanel.setVisible(!replacePanel.isVisible());
        replacePanel.onStart();
    }

    public void leftSideOnOff() {
        leftPanelVisible = !leftPanelVisible;
        leftPanel.setVisible(leftPanelVisible);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.0);
        splitPane.revalidate();
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
        replacePanel.setSearchText(searchText);
        findPanel.setSearchText(searchText);
        if (searchText == null || searchText.isEmpty()) {
            return notebook.sortedNotes();
        }
        return notebook.getNotes().stream().filter(note -> note.getData().toLowerCase().contains(searchText.toLowerCase()))
                .sorted((o1, o2) -> o2.getFile().getModificationDate()
                        .compareTo(o1.getFile().getModificationDate()))
                .collect(Collectors.toList());
    }

    public void setNoteFromInternalLink(String internalLink) {
        Note noteForTitle = getNotebook().loadByTitle(internalLink);
        log.info("Try to open internal URL: {} found: {}", internalLink, noteForTitle.getTitle());
        if (noteForTitle.getTitle() != null) {
            if ( listOfTitlesUI.setSelectedIndex(noteForTitle) != -1) {
                setNoteFromTextId(noteForTitle.getTextId());
            }
        }
    }

    public void setNoteFromTextId(String textId) {
        Note note = notebook.loadByTextId(textId);
        notebook.setActualNote(note);
        DocumentArea.print(note, textArea, searchField);
        textArea.grabFocus();
        textArea.setCaretPosition(0);
    }

    public void cleanNoteWindow() {
        notebook.setActualNote(Note.empty());
        textArea.setText(StringUtil.EMPTY);
        textArea.grabFocus();
    }

    private void recreateList(List<Note> notes) {
        listOfTitlesUI.reCreate(notes);
    }
}
