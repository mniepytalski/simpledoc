package pl.cbr.notebook.ui.swing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Keyword {
    TITLE("\\A.*?(?=\\r?\\n|\\z)", TextStyle.titleStyle(), false),
    VARIABLE("[\"]{0,1}[a-zA-Z][a-zA-Z0-9-._]{0,50}[\"]{0,1}[ \t]{0,2}[:=]", TextStyle.getWithForeground(new Color(0, 150, 0), false), false),
    TAG("#[a-zA-Z]+[a-zA-Z0-9]*", TextStyle.getWithForeground(Color.ORANGE, false), false),
    URL("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", TextStyle.urlStyle(), false),
    MD_BOLD_ITALIC("\\*\\*\\*(.*?)\\*\\*\\*", TextStyle.boldItalicStyle(), false),
    MD_BOLD("\\*\\*(.*?)\\*\\*", TextStyle.boldStyle(), false),
    MD_ITALIC("\\*(.*?)\\*", TextStyle.italicStyle(), false),
    MD_QUOTE("(^|\\r?\\n)> [^\\r\\n]*", TextStyle.quoteStyle(), false),
    MD_PARAGRAPH4("(^|\\r?\\n)#### [^\\r\\n]*", TextStyle.paragraph4Style(), false),
    MD_PARAGRAPH3("(^|\\r?\\n)### [^\\r\\n]*", TextStyle.paragraph3Style(), false),
    MD_PARAGRAPH2("(^|\\r?\\n)## [^\\r\\n]*", TextStyle.paragraph2Style(), false),
    INTERNAL_URL("\\(\\([^\\r\\n]*\\)\\)", TextStyle.urlStyle(), false),
    LIST("(?m)^[ ]{0,10}[a-zA-Z0-9]{1,5}[.)]([a-zA-Z0-9]{1,5}[.)])*[ \\t\\n]", TextStyle.getWithForeground(Color.BLACK, true), false),
    COMMENT("((?m)^#[ ]+.*$)|((?m)^.*<!-- .+ -->.*$)", TextStyle.getWithForeground(Color.GRAY, false), false),
    STRESS("[=]{3,}.", TextStyle.getWithForeground(Color.RED, true), false),
    STRESS_MINUS("((?m)[-]{3,}.*$)", TextStyle.getWithForeground(Color.RED, false), false),
    STRESS_PLUS("((?m)[+]{3,}.*$)", TextStyle.getWithForeground(new Color(0, 150, 0), false), false),
    XML("<[^>]+>", TextStyle.getWithForeground(new Color(0, 150, 0), false), false),
    CODE("(?s)\\{code:(java|sql)\\}(.*?)\\{code\\}",  TextStyle.getWithForeground(Color.BLACK), false);

    private final String regexp;
    private final SimpleAttributeSet style;
    private final boolean replace;

    public Set<String> search(String data) {
        Pattern pattern = Pattern.compile(this.getRegexp());
        Matcher matcher = pattern.matcher(data);
        return matcher.results().map(MatchResult::group).collect(Collectors.toSet());
    }
}
