package pl.cbr.notebook;

import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TagUtil {
    public static String PATTERN = "#[a-zA-Z]+";

    public static Set<String> search(String data) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(data);
        return matcher.results().map(MatchResult::group).collect(Collectors.toSet());
    }
}
