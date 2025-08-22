package pl.cbr.notebook.ui.swing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Keyword {
    TAG("#[a-zA-Z]+", TextStyle.getStyle(Color.ORANGE, true)),
    LIST("[ ]{0,5}[a-zA-Z0-9]{1,5}[.)][ \\t]", TextStyle.getStyle(Color.BLACK, true)),
    URL("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", TextStyle.getStyle(Color.BLUE, true)),
    VARIABLE("[\"]{0,1}[a-zA-Z]+[a-zA-Z0-9-._]+[\"]{0,1}[:=]", TextStyle.getStyle(new Color(0, 150, 0), true)),
    COMMENT("[=]{2,}.", TextStyle.getStyle(Color.RED, true)),
    XML("[<][/]{0,1}[a-zA-Z]+[a-zA-Z.]*[/]{0,1}[>]", TextStyle.getStyle(new Color(0, 150, 0), true)),
    NORMAL("^.*$", TextStyle.getStyle(Color.BLACK));

    private final String regexp;
    private final SimpleAttributeSet style;

    public static List<DocumentPart> analise(String text) {
        String allKeywords = EnumSet.allOf(Keyword.class)
                .stream()
                .filter(keyword -> keyword != NORMAL)
                .map(Keyword::getRegexp)
                .map(Keyword::group)
                .collect(Collectors.joining("|"));

        Pattern pattern = Pattern.compile(allKeywords);
        Matcher matcher = pattern.matcher(text);
        List<DocumentPart>  parts = new ArrayList<>();
        int start = 0;
        while (matcher.find()) {
            String normalText = text.substring(start, matcher.start());
            if (!normalText.isEmpty()) {
                parts.add(new DocumentPart(normalText, Keyword.NORMAL));
            }
            String findText = text.substring(matcher.start(), matcher.end());
            Optional<Keyword> optional = EnumSet.allOf(Keyword.class).stream().filter(expression -> findText.matches(expression.getRegexp())).findFirst();
            optional.ifPresent(keyword ->
                    parts.add(new DocumentPart(findText, keyword)));
            start = matcher.end();
        }
        String endText = text.substring(start);
        if (!endText.isEmpty()) {
            parts.add(new DocumentPart(endText, Keyword.NORMAL));
        }
        return parts;
    }

    private static String group(String regexp) {
        return "(" + regexp + ")";
    }
}
