package pl.cbr.notebook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class NoteUrl {
    private final int start;
    private final int end;
    private final String url;
    private final NoteUrlType type;

    public int getLength() {
        return end - start;
    }
}
