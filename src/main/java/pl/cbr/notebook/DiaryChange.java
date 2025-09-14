package pl.cbr.notebook;

import lombok.Data;
import lombok.ToString;
import pl.cbr.notebook.ui.swing.NoteLog;

@ToString
@Data
public class DiaryChange {
    private final static char BACKSPACE = '\b';

    private static int idCounter = 0;
    private int id;
    private String change;
    private int begin;
    private int end;

    private NoteLog log;

    public DiaryChange(int position) {
        change = "";
        begin = position;
        end = position + 1;
        id = idCounter++;
    }

    public boolean checkRange(int position) {
        return getBegin() <= position && position <= getEnd();
    }

    public void event(char letter, int position) {
        try {
            if (letter == BACKSPACE) {
                removeLetter(position);
            } else {
                addLetter(letter, position);
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println(id + "> letter:" + Integer.toHexString(letter) + "hex,pos:" + position + ">begin:" + begin + ",end:" + end + ":text=" + change);
            NoteLog.info(e.getMessage());
            throw  new StringIndexOutOfBoundsException();
        }
    }

    private void removeLetter(int position) {
        if (change.isEmpty() || position == 0) {
            return;
        }
        end--;
        int offset = position - begin;
        if (offset >= end) {
            System.out.println("MARIO> offset:" + offset);
        }
        if (position + 1 == end) {
            change = change.substring(0, offset);
        } else {
            String firstPart = change.substring(0, offset);
            String secondPart = change.substring(offset + 1);
            change = firstPart + secondPart;
        }
    }

    private void addLetter(char letter, int position) {
        int offset = position - begin;
        if (offset >= end) {
            System.out.println("MARIO> offset:" + offset);
        }
        if (position + 1 == end) {
            change = change + letter;
        } else {
            String firstPart = change.substring(0, offset);
            String secondPart = change.substring(offset);
            change = firstPart + letter + secondPart;
        }
        end++;
    }
}
