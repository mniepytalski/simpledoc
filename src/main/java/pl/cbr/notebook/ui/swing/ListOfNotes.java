package pl.cbr.notebook.ui.swing;

import pl.cbr.notebook.Note;

import javax.swing.*;
import java.util.List;

public interface ListOfNotes {

    void clickMouseLeftButton(Note note);

    void clickMouseRightButton(Note note);

    void remove(Note note);

    int setSelectedIndex(Note note);

    void reCreate(List<Note> notes);

    void saveNote(Note note, Note actualNote);

    JScrollPane getScrollPane();
}
