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
    TITLE("\\A.*?(?=\\r?\\n|\\z)", TextStyle.titleStyle()),
    VARIABLE("[\"]{0,1}[a-zA-Z][a-zA-Z0-9-._]{0,50}[\"]{0,1}[ \t]{0,2}[:=]", TextStyle.getStyle(new Color(0, 150, 0), false)),
    TAG("#[a-zA-Z]+[a-zA-Z0-9]*", TextStyle.getStyle(Color.ORANGE, false)),
    URL("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", TextStyle.urlStyle()),
    LIST("(?m)^[ ]{0,5}[a-zA-Z0-9]{1,5}[.)][ \\t\\n]", TextStyle.getStyle(Color.BLACK, true)),
    COMMENT("((?m)^#[ ]+.*$)|((?m)^.*<!-- .+ -->.*$)", TextStyle.getStyle(Color.GRAY, false)),
    STRESS("[=]{3,}.", TextStyle.getStyle(Color.RED, true)),
    STRESS_MINUS("((?m)[-]{3,}.*$)", TextStyle.getStyle(Color.RED, false)),
    STRESS_PLUS("((?m)[+]{3,}.*$)", TextStyle.getStyle(new Color(0, 150, 0), false)),
    XML("<[^>]+>", TextStyle.getStyle(new Color(0, 150, 0), false)),
    CODE("(?s)\\{code:(java|sql)\\}(.*?)\\{code\\}",  TextStyle.getStyle(Color.BLACK));

    private final String regexp;
    private final SimpleAttributeSet style;

    public Set<String> search(String data) {
        Pattern pattern = Pattern.compile(this.getRegexp());
        Matcher matcher = pattern.matcher(data);
        return matcher.results().map(MatchResult::group).collect(Collectors.toSet());
    }
}
