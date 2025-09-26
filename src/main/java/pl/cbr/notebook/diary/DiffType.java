package pl.cbr.notebook.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DiffType {
    INSERT("+"),
    DELETE("-");

    private final String value;
}
