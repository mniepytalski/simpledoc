package pl.cbr.notebook.diary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class DiaryChange {
    private final String change;
    private final Date date;

    public String toString() {
        return change;
    }
}

