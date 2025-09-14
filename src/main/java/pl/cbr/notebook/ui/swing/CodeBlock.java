package pl.cbr.notebook.ui.swing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.swing.text.SimpleAttributeSet;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class CodeBlock implements Comparable<CodeBlock> {
    private final Integer id;
    private final String regExp;
    private final SimpleAttributeSet style;

    @Override
    public int compareTo(CodeBlock codeBlock) {
        return codeBlock.getId().compareTo(getId());
    }
}
