package pl.cbr.notebook.ui.swing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum CodeKeyword {
    SQL("sql",
            Set.of(
                    CodeBlock.of(1, "--.*", TextStyle.getStyle(Color.GRAY)),
                    CodeBlock.of(2, "(?i)\\b(SELECT|FROM|WHERE|INSERT|UPDATE|DELETE|JOIN|ORDER|BY|GROUP|HAVING|ASC|DESC|INTO|VALUES|SET)\\b",TextStyle.getStyle(Color.BLUE))
            )
    ),
    JAVA("java",
            Set.of(
                    CodeBlock.of(1, "\"([^\"\\\\]|\\\\.)*\"", TextStyle.getStyle(new Color(0, 120, 0, 255))),
                    CodeBlock.of(2, "(?<!\")//.*|(?<!\")/\\*[\\s\\S]*?\\*/", TextStyle.getStyle(Color.GRAY)),
                    CodeBlock.of(3, "(?i)\\b(VOID|PUBLIC|PRIVATE|FINAL|NEW|PACKAGE|IMPORT|ENUM|THIS|SUPER|INT|STATIC|WHILE|FOR|FALSE)\\b",TextStyle.getStyle(new Color(200, 112, 10, 255))),
                    CodeBlock.of(4, "@[a-zA-Z]+", TextStyle.getStyle(new Color(211, 186, 29, 255))),
                    CodeBlock.of(5, " [a-zA-Z0-9]+\\(", TextStyle.getStyle(new Color(14, 98, 225, 255))),
                    CodeBlock.of(6, "(?<=[. (])[.0-9XA-F]+(?=[;, )])", TextStyle.getStyle(new Color(24, 157, 228, 255))),
                    CodeBlock.of(7, "(?<=[,. (])[A-Z][A-Z0-9_]*(?=[,; )])", TextStyle.getStyle(new Color(177, 16, 177, 255)))
            )
    );

    private static final int GROUP_CODE = 2;
    private static final int GROUP_LANGUAGE = 1;

    private final String language;
    private final Set<CodeBlock> regExp = new HashSet<>();

    CodeKeyword(String code, Set<CodeBlock> codeBlocks) {
        this.language = code;
        this.regExp.addAll(codeBlocks);
    }

    public static void codeAnalyzer(Matcher matcher, StyledDocument doc, String text) {
        int startCode = matcher.start(GROUP_CODE);
        int endCode = matcher.end(GROUP_CODE);

        if (endCode <= text.length()) {
            String finalCodeBlock = text.substring(startCode, endCode);
            Stream.of(CodeKeyword.values())
                    .filter(k -> k.getLanguage().equals(matcher.group(GROUP_LANGUAGE)))
                    .findFirst().ifPresent(k ->
                            k.getRegExp().stream().sorted().forEach(block ->
                                markCodeKeyword(doc, block, finalCodeBlock, startCode)
                    ));
        } else {
            System.out.println("start: " + startCode + ", end: " + endCode + ", length: " + text.length());
        }
    }

    private static void markCodeKeyword(StyledDocument doc, CodeBlock block, String finalCodeBlock, int startCode) {
        Pattern p = Pattern.compile(block.getRegExp());
        Matcher m = p.matcher(finalCodeBlock);
        while (m.find()) {
            doc.setCharacterAttributes(startCode + m.start(), m.end() - m.start(), block.getStyle(), false);
        }
    }
}
