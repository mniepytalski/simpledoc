package pl.cbr.notebook;

import lombok.Data;


@Data
public class DiaryChange {
    private final static char BACKSPACE = '\b';

    private String change;
    private int begin;
    private int end;

    public DiaryChange(int position) {
        change = "";
        begin = position;
        end = position + 1;
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
            System.out.println(letter + "," + position + ">" + begin + "," + end + ":" + change);
            e.printStackTrace();
        }
    }

    private void removeLetter(int position) {
        if (change.isEmpty() || position == 0) {
            return;
        }
        end--;
        if (position + 1 == end) {
            change = change.substring(0, position);
        } else {
            int offset = position - begin;
            String firstPart = change.substring(0, offset);
            String secondPart = change.substring(offset + 1);
            change = firstPart + secondPart;
        }
    }

    private void addLetter(char letter, int position) {
        if (position + 1 == end) {
            change = change + letter;
        } else {
            int offset = position - begin;
            String firstPart = change.substring(0, offset);
            String secondPart = change.substring(offset);
            change = firstPart + letter + secondPart;
        }
        end++;
    }
}
