package pl.cbr.notebook;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Diary {

    private final List<DiaryChange> changes = new ArrayList<>();

    public void event(char letter, int position) {
        DiaryChange change = getChange(position);
        if ( change == null ) {
            change = new DiaryChange(position);
            changes.add(change);
        }
        try {
            change.event(letter, position);
        } catch (StringIndexOutOfBoundsException e) {
            changes.forEach(ch -> System.out.println(ch.toString()));
        }

    }

    private DiaryChange getChange(int position) {
        return changes.stream()
                .filter(change -> change.checkRange(position))
                .findFirst().orElse(null);
    }
}
