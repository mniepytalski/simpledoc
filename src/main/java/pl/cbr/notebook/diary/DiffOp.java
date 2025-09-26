package pl.cbr.notebook.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DiffOp {
    private final DiffType type;
    private final int lineNumber;
    private final String content;
}
