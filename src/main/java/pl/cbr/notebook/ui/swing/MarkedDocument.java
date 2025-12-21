package pl.cbr.notebook.ui.swing;

import lombok.Data;
import pl.cbr.notebook.utils.StringUtil;

@Data
public class MarkedDocument {
    private int start;
    private int end;
    private String text;

    public MarkedDocument() {
        clear();
    }

    public void set(int start, int end, String text) {
        this.start = start;
        this.end = end;
        this.text = text;
    }

    public void clear() {
        start = 0;
        end = 0;
        text = StringUtil.EMPTY;
    }
}
